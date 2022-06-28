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

---
구글 로그인 이후 가져온 사용자의 정보들을 기반으로 가입 및 정보수정, 세션 저장 등의 기능을 지원하는  
**CustomOAuth2UserService**생성  

```java
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 1
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName(); // 2
        
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes()); // 3
        
        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user",new SessionUser(user)); // 4
        
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }
    
    private User saveOrUpdate(OAuthAttributes attributes){
        User user = UserRepository.findByEmail(attributes.getEmail())
                .map(entity->entity.update(attributes,getName, attributes.getPicture()))
                .orElse(attributes.toEntity());
        
        return userRepository.save(user);
    }
}
```

1. registrationId : 현재 로그인 진행중인 서비스를 구분하는 코드. 구글만 사용하면 필요없지만 추후 네이버 로그인 연동시 구분하기 위함이다.  
2. userNameAttributeName : OAuth2 로그인 진행시 키가 되는 필드값이며 Primary Key와 같은 의미다. 네이버 로그인과 구글 로그인을 동시 지원할 때 사용된다.  
3. OAuthAttributes : OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스이며 이후 다른 소셜 로그인도 이 클래스를 사용한다.  
4. SesstionUser : 세션에 사용자 정보를 저장하기 위한 Dto 클래스  

다음으로 OAuthAttributes 클래스를 생성하는데, 저자는 이를 Dto로 취급한다고 한다.  
```java
@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    // 1
    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes){
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    //2
    public User toEntity(){
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST)
                .build();
    }
}

```
1. of() : OAuth2User에서 반환하는 사용자 정보는 Map이기 때문에, 값 하나하나를 반환해야만 한다.  
2. toEntity() : User 엔티티를 생성한다. OAuthAttributes에서 엔티티를 생성하는 시점은 처음 가입할때 이다  
가입할 때의 기본권한을 GUEST로 준다.  

그리고 직렬화된 SesstionUser 클래스를 추가한다
```java
@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
```
User를 그대로 사용하지 않는 이유는 엔티티가 언제 다른 엔티티와 관계가 형성될지 모르기 때문이다.  
@OneToManu, @ManyToMany등 자식 엔티티를 갖고 있다면 직렬화 대상에 자식들까지 포함되어 성능 이슈나 부수 효과가 발생할 확률이 높다.  
그렇기에 직렬화 Dto를 하나 추가로 만드는 것이 이후 운영 및 유지보수에 많은 도움이 된다.  

---

### 4. 로그인 테스트  

index.mustache에 다음을 추가한다.
```html
<div class="row">
    <div class="col-md-6">
    <a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
        {{#userName}} <!-- 1 -->
        Logged in as: <span id="user">{{userName}}</span>
        <a href="/logout" class="btn btn-info active" role="button">Logout</a> <!-- 2 -->
        {{/userName}}
        {{^userName}} <!-- 3 -->
        <a href="/oauth2/authorization/google" class="btn btn-success active" role="button">Google Login</a> <!-- 4 -->
        <a href="/oauth2/authorization/naver" class="btn btn-secondary active" role="button">Naver Login</a>
        {{/userName}}
    </div>
</div>
```
1. 머스테치는 if문을 제공하지 않고 true/false여부만 판단한다.  
따라서 항상 최종값을 넘겨줘야 한다.
2. 스프링 시큐리티에서 기본적으로 제공하는 로그아웃 URL이다.  
3. 머스테치에서 해당 값이 존재하지 않는 경우 ^ 를 사용한다.  
4. 스프링 시큐리티에서 기본적으로 제공하는 로그인 URL. 

그리고 IndexController를 수정한다.
```java
private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("posts",postsService.findAllDesc());
        SessionUser user = (SessionUser) httpSession.getAttribute("user"); // 1
        
        if (user!=null){ // 2
            model.addAttribute("userName",user.getName());
        }
        
        return "index";
    }
```
1. CustomOAuth2UserService에서 로그인 성공 시 세션에 SessionUser를 저장한다
2. 세션에 저장된 값이 있을 때만 model에 userName으로 등록  

이후 애플리케이션을 실행시키면 흔히 보던 구글 로그인 기능을 사용할 수 있다  
글 작성을 하려하면 오류가나는데 처음 로그인한 사용자의 등급이 GUEST라서 그렇다.

---

### 5. 기존 테스트 수정  

테스트를 실행하면 다음 오류들이 발생한다.  

1. CustomOAuth2UserService를 생성하는데 필요한 소셜 로그인 관련 설정값들이 존재하지 않음  
: src/main 환경과 src/test 환경의 차이 때문에 발생하는 문제다. 가짜 설정값을 등록하면 된다.  
```properties
spring.security.oauth2.client.registration.google.client-id=test
spring.security.oauth2.client.registration.google.client-secret=test
spring.security.oauth2.client.registration.google.scope=profile,email
```

2. 인증되지 않은 사용자의 요청문제  
: 임의로 인증된 사용자를 추가해서 API만 테스트 할 수 있게 하면 된다.  
```gradle
testCompile("org.springframework.security:spring-security-test")
```
그리고 오류가 나는 테스트에 @WithMockUser(roles = "USER") 어노테이션을 추가한다.  
마지막으로 @SpringBootTest에서 MockMvc를 사용하도록 변경하면 된다.  

3. @WebMvcTest가 CustomOAuth2UserService를 스캔하지 않는 문제  
: 스캔 대상에서 SecurityConfig를 제거한 뒤 @WithMockUser(roles = "USER") 어노테이션을 추가한다.  
또 @EnableJpaAuditing과 @SpringBootApplication을 분리하면 된다.