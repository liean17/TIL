# 포토그램 인증

### **Spring Security**
내려받은 프로젝트를 실행해보면 아무런 설정을 하지 않았음에도  
로그인 페이지가 떠...야하지만 문제가 발생했다.  

> ***해결과정***  
**Failed to introspect Class [org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaConfiguration]**  
위와 같은 오류가 발생했다. stackoverflow에서 검색해본 결과, hibernate의 버전문제라는 것을 알게되서  
해당 답변에 있는 버전을 들고와서 의존성을 추가해주었는데  
이번에도 오류가 발생했다.  
그래서 이참에 maven이 아닌 gradle로 옮겨보려했다가 Jpa관련 오류가 나서  
더 복잡해질 것 같아서 다시 돌아온뒤 해당 오류를 다시 검색해보니  
버전 문제가 맞는데 내가 추가한 버전도 오래되어서 그런거였다.  
뒤에 나온 버전으로 의존성을 수정해주니 정상적으로 작동했다.

스프링 시큐리티에서 기본적으로 제공하는 페이지인데  
첫 페이지를 임의로 설정하기 위해서 설정을 해줘야한다.

```java
@EnableWebSecurity// 해당 파일로 시큐리티를 활성화
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 안에 있는 메소드 삭제
    }
}
```
WebSecurityConfigureAdapter를 상속받은 SecurityConfig를 만들고  
위 어노테이션을 추가하면 해당 파일을 시큐리티 파일로 인식한다  
그리고 configure 메소드를 오버라이딩 한 후 안의 메소드를 삭제하면 기존과 같은 로그인 페이지는 뜨지 않게된다.  

이후 아래 메서드를 추가하면 접근 제한이 가능하다  
```java
http.authorizeRequests()
        //다음 주소만 접근 허용
        .antMatchers("/","/user/**","/image/**","/subscribe/**","/comment/**").authenticated()
        //이외 주소 접근 제한
        .anyRequest().permitAll()//
        .and()
        .formLogin()
        //권한없는 페이지 접근시 이동할 로그인 페이지 설정
        .loginPage("/auth/signin")
        //로그인 되었을 경우 리다이렉션될 페이지
        .defaultSuccessUrl("/");
```
---
### CSRF 토큰 해제

CSRF 토큰 : 클라이언트가 서버에 요청할때 시큐리티가 검증하기 위해 사용하는 토큰  
페이지 요청에 대한 응답할때 함께 보내는데, 클라이언트는 이 토큰을 post과정에서 보내게 된다.


---
### 회원 가입  

회원정보를 가지는 User 엔티티와 클라이언트에게서 요청을 받을때 사용할 RequestDto를 만들어야한다.  
```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity //DB에 Table 생성
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String password;

    private String name;
    private String website;
    private String boi;
    private String email;
    private String phone;
    private String gender;

    private String profileImageUrl;
    private String role;

    private LocalDateTime createdDate;

    @PrePersist // DB INSERT되기 직전에 실행
    public void createDate(){
        this.createdDate=LocalDateTime.now();
    }
}
```
눈여겨 볼 어노테이션이 몇가지 있다.  
1. @Entity : 해당 클래스가 엔티티임을 인식하고 데이터베이스에 테이블이 생성되게 해준다.  
2. @Id, @GeneratedValue : 기본키를 설정하고 생성 규칙을 설정한다. IDENTITY는 DB에 저장될 때 번호가 생성된다.  
---
SignUpDto
```java
@Getter
@Setter
public class SignupDto {
    private String username;
    private String password;
    private String email;
    private String name;

    public User toEntity(){
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .name(name)
                .build();
    }
}
```
클라이언트에게서 회원 가입 정보를 받을때 사용되는 Dto이다.  
이후 컨트롤러에서 저장할 User 객체를 만들기 위해 toEntity 메서드가 추가되어있다.  

다음으로는 User가 저장될 Repository, 저장하는 역할을 할 Service를 구현한다.  

Repository는 JpaRepository를 상속받으면 준비 끝이다.

```java
@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User join(User user){
        User savedUser = userRepository.save(user);
        return savedUser;
    }
}
```
User 객체를 그대로 Repository에 저장 후 바로 그 객체를 반환한다.  
그리고 곧바로 컨트롤러에 사용하자.  

```java
@PostMapping("/auth/signup")
public String signUp(SignupDto signupDto){
    User user = signupDto.toEntity();
    User userEntity = authService.join(user);
    return "auth/signin";
}
```
toEntity 메서드를 통해 저장할 수 있는 user객체로 만든 뒤  
서비스의 join메서드로 데이터베이스에 저장한다.  
이후 데이터베이스를 확인해보면 정상적으로 가입 데이터가 들어가있다.  

---

### 비밀번호 암호화

BCryptPasswordEncoder를 사용한다.  
SecurityConfig에 위 인코더를 생성하고 서비스에서 DI를 통해 사용하면 된다.  
```java
@Transactional// Write(Insert, Update, Delete)
public User join(User user){
    String rawPassword = user.getPassword();
    String encPassword = bCryptPasswordEncoder.encode(rawPassword);
    user.setPassword(encPassword);
    User savedUser = userRepository.save(user);
    return savedUser;
}
```
기존비밀 번호를 인코더를 통해 암호화 시킨 후 저장하면 된다.

---
## 전처리, 후처리  
회원 가입시 입력 값에 대해 검사를 한다고 하자  
아이디 중복의 경우에는 반드시 데이터베이스를 확인해야 검사할 수 있고  
문법오류는 요청을 보낼때 즉시 파악이 가능하다.  
이처럼 즉시 처리하는 것을 전처리, DB를 검사하는 등 뒷 단에서 해결하는 것을 후처리 라고 하며  
이렇게 어플리케이션 공통에 미치는 기능을 AOP라고 한다.

### 유효성 검사

@Validation 의존성을 추가해서 입력에대한 예외처리를 간단하게 할 수 있다.  
Dto에 @NotBlank등을 추가해서 전처리를 할 수 있고, 엔티티에 @Column 어노테이션을 통해 후처리를 할 수 있다.  
그리고 컨트롤러에서 BindingResult를 사용해서 에러 확인이 가능하다.

### 예외처리  
BindingResult는 예외를 보여줄 뿐 처리하는 것은 아니다.  
@ControllerAdvice 어노테이션을 가지고 있는 핸들러를 만들면  
컨트롤러에서 발생하는 예외를 전부 캐치해준다.  
```java
@RestController // 데이터형식으로 반환
@ControllerAdvice // 컨트롤러에서 발생한 예외를 전부 캐치
public class ControllerExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Map<String, String> validationException(CustomValidationException e){
        return e.getErrorMap();
    }
}
```
그리고 @ExceptionHandler를 사용해서 원하는 예외를 캐치한 뒤 원하는 형식으로 반환하게 하면 된다.  
위의 경우에는 Map으로 반환하는 예외를 임의로 생성했다.  

### 공통 응답 DTO

항상 오류만 응답하는게 아니라 정상적인 경우에는 오류가 아닌 객체를 응답해야할 수 있기 때문에  
공통으로 응답할 수 있는 DTO를 만든다(다른곳에서 배우기로는 응답별로 DTO를 구분하는게 좋다고 들었다.)  
```java
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class CMResponseDto<T> {
        private int code; //1 : 성공, -1 : 실패
        private String message;
        private T data;
}

    @ExceptionHandler(RuntimeException.class)
    public CMResponseDto<?> validationException(CustomValidationException e){
        return new CMResponseDto(-1,e.getMessage(),e.getErrorMap());
    }
```
오류만이 아니라 모두 받을 수 있도록 타입을 T로 둔 응답 DTO를 만들고  
응답할때는 와일드카드를 사용해서 모든 타입을 반환할 수 있도록 한다.  

### 로그인 구현  
로그인 버튼을 클릭했을때 로그인 처리가 되도록 구현한다.  
jsp에서 로그인 버튼 클릭시 GET이 아닌 POST요청을 보내도록 해야한다  
GET요청을 사용할 경우 중요정보가 파라미터로 넘어갈 수 있기 때문이다.  

- 현재까지 코드는 유효성 검사가 되어있지않다.  
따라서 유효성검사와 함께 프론트 단에서도 입력을 제한시키도록 한다.
