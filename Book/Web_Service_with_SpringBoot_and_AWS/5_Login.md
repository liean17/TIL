# 스프링 시큐리티와 OAuth 2.0으로 로그인 기능 구현하기

> **로그인 최근 경향**  
최근 많은 서비스에서 독자적인 id/password를 사용한 가입 보다 소셜 로그인 기능을 사용한다.  
직접 구현하는 과정이 매우 복잡하고 구현할게 많기때문이다.

### 1. 구글 서비스 등록
> 책을 따라 구글 클라우드 서비스에서 진행

이후 application-oauth.properties에 클라이언트 ID와 비밀코드 등록  
중요정보기에 **절대 깃에 올려서는 안된다.** - .gitignore등록  

### 2. 구글 로그인 연동하기  
user패키지와 User 도메인 클래스를 생성한다.

```java
@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING) // 1
    @Column(nullable = false)
    private Role role;

    @Builder
    public User update(String name, String picture){
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }
}
```
1. JPA에서 Enum 값을 저장할 때 어떤 타입으로 저장할지를 선택한다.  
기본적으로는 int값을 저장하는데 이 경우 값이 어떤 의미인지 알 수 없다. 

Role Enum을 정의한다
```java
@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST","손님"),
    USER("ROLE_USER","일반 사용자");

    private final String key;
    private final String title;
}
```
**스프링 시큐리티에서는 권한 코드에 항상 ROLE_이 앞에 있어야한다**  

그리고 User의 CRUD를 책임질 UserRepository도 생성한다.
```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email); // 1
    
}
```
1. 소셜 로그인으로 반환되는 값 중 email을 통해 이미 생성된 사용자인지 아닌지를 판단하기 위한 메소드

---

### 3. 시큐리티 설정  

compile('org.springframework.boot:spring-boot-starter-oauth2-client') 의존성 추가  
config.auth패키지 생성 후 SecurityConfig 클래스 생성  
```java
@RequiredArgsConstructor
@EnableWebSecurity // 1
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable()
                .headers().frameOptions().disable() // 2
                .and()
                .authorizeRequests() // 3
                .antMatchers("/","/css/**","/images/**",
                        "/js/**","/h2-console/**").permitAll()
                .antMatchers("/api/v1/**").hasRole(Role.USER.name()) // 4
                .anyRequest().authenticated() // 5
                .and()
                .logout()// 6
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login() // 7
                .userInfoEndpoint() // 8
                .userService(customOAuth2UserService); // 9
    }
}
```

1. Spring Sevurity 설정 활성화
2. h2-console 화면을 사용하기 위한 옵션
3. URL별 권한 관리를 설정하는 옵션의 시작점  
4. 권한 관리 대상을 지정하는 옵션
5. 설정된 값 이외의 URL, authenticated를 추가해서 나머지는 인증된 사용자에게만 허용하도록 했다
6. 로그아웃관련. 성공시 / 주소로 이동한다
7. OAuth2 로그인 기능에 대한 여러 설정의 진입점
8. OAuth2 로그인 성공 이후 사용자의 정보를 가져올때의 설정
9. 소셜 로그인 성공시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록