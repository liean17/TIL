# 스프링 시큐리티

## 프로젝트 대비 복습

- 스프링 시큐리티의 적용 및 설정방법 그리고 사용법을 안다.

--- 
* 정리
    - Spring Security  
    : 스프링 시큐리티를 적용하면, 먼저 어느 페이지를 들어가도 자체 제공되는 로그인 창이 뜬다.  
    따라서 원하는 주소에 원하는 설정으로 화면을 출력하게하려면 설정이 필요하다.  
    ```java
    @Configuration
    @EnableWebSecurity // 1
    public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf().disable(); // 2
    		http.headers().frameOptions().disable(); // 3

            http.authorizeRequests()
                    .antMatchers("/user/**").authenticated() // 4
                    .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") // 5
                    .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                    .anyRequest().permitAll();
                    .and()
                    .formLogin()
                    .loginPage("/login"); // 6
            return http.build();
        }
    }
    ```
    1. 스프링 시큐리티 필터를 스프링 필터체인에 등록하기 위한 어노테이션, 즉 아래 코드로 시큐리티를 활성화 하겠다는 선언
    2. form태그 외에 postman과 같은 외부에서 오는 요청을 받지 않도록 해준다.
    3. h2 콘솔 화면을 사용하기 위해 필요한 설정 -  
    : 정확하게는 ClickJacking을 방지하기 위한 X-frame 설정이다.  
    클릭재킹은 해커가 정상적인 페이지에 임의로 해킹을 위한 보이지않는 레이어를 만들어서 클릭하도록 유도하는 해킹 수법이다.  
    위 설정을 통해 요청 사이트에 대한 프레임 표시 설정을 할 수 있다.
    4. 인증되어야 한다는 뜻 -> 인증되기만 하면 OK
    5. 인증은 물론이고 권한이 임의로 설정된 권한과 동일해야 접근이 가능하다는 뜻
    6. 인증되지 않은채 시도된 접근을 해당 페이지로 이동시킨다.   
    ---

    - 컨트롤러 예외처리하기[이지업 포토그램 24강](https://easyupclass.e-itwill.com/classroom/index.jsp?cuid=1149)  
    유효성 검사에서 걸러진 예외는 바깥으로 나와 예외 페이지를 출력하는데  
    예외 페이지는 깔끔하지도 않고 중요한 정보가 있을 수 있기에 사용자에게 보여지면 안된다.  
    따라서 @ResponseBody와 BindingResult를 사용하면 에러메세지만 골라 출력할 수 있다.  
    이 경우 문제는 템플릿을 사용하는 경우 템플릿을 반환하는게 아니라 문자 그대로 반환하게 되어버린다.  
    여기서 ExceptionHandler가 등장한다.  
    기존 컨트롤러에서는 지정한 예외를 발생시키고  
    ExceptionHandler는 해당 예외를 받아서 예외 정보를 출력하게 하면 된다.  
    또한 임의로 예외를 생성해서 **Dto를 생성**하면 메세지 뿐만 아니라 구체적인 에러 정보만 골라 출력하게 할 수 있다.  
        - 공통 응답 DTO 만들기  
        예외가 발생한경우 예외 전용 DTO를 만들 수 있지만  
        정상 응답이 가능하면 정상적으로 객체를 반환해야한다  
        이 두가지경우를 따로만드는 것도 가능하나 한번에 하는 것도 가능하다  
        제네릭 타입을 사용하는 것이다.  

        ```java
        public class CommonResponseDto<T>{
            private int code;
            private String message;
            private T data;
        }
        ```
        그리고 사용할때 타입은 와일드카드를 사용하면 무엇이든 전달할 수 있게된다  
        CommonResponseDto<?>  

    - 스프링 시큐리티를 통한 로그인  
    회원가입과 달리 로그인 즉 검증 과정은 스프링 시큐리티에게 맡긴다.  
    방법은 SecurityConfig에 .loginProcessingUrl()을 등록하는 것이다.  
    안에 Post요청을 하는 로그인 페이지 주소를 담으면 스프링 시큐리티가 로그인 과정을 진행해준다.  
        - 과정 설명
        1. 스프링 시큐리티에 설정을 추가하면 해당 주소로 로그인 요청이 오는것을 기다린다.  
        2. 해당 주소로 로그인 요청이 오면 IoC에 있는 UserDetailsService가 로그인을 진행한다.  
        3. UserDetailsService를 상속받은 클래스가 있다면 해당 클래스를 실행해서 로그인 과정을 진행한다.  
        4. username(로그인 id)를 통해 로그인 과정을 진행한다. 비밀번호는 시큐리티가 알아서 진행한다.  
        5. 해당 메서드는 username을 받아 UserDetails타입을 반환한다. 반환에 성공하면 로그인에 성공한 것이다.
        6. 먼저 repository에서 username을 통해 user객체를 가져와야한다.  
        7. 그리고 UserDetails타입으로 반환하기 위해서 이를 상속받은 객체를 따로 만들어 매핑할 수 있도록 한다.  
        8. 콜렉션타입인 권한을 가져오는 메서드를 만들어야한다.  권한을 생성함과 동시에 User의 Role을 집어넣으면 된다. 
        9. 로그아웃은 스프링 시큐리티에서 /logout으로 이동하면 로그아웃을 해주는 기능이 구현되어있다.  

    - 스프링 시큐리티를 통해 만들어진 세션에 접근하는 방법  
    : 로그인을 통해 UserDetailsService에서 성공적으로 UserDetails타입이 반환이 되었다면(보통 PrincipalDetails라는 이름의 클래스로 구현한다)  
    해당 정보가 세션에 저장되는데, 그냥 저장되는 것이 아니라 그 안의 SecurityContextHolder에 저장되며,  
    또 UserDetails는 Authentication이라는 객체에 한번 더 감싸져서 저장이 된다.  
    따라서 직접 꺼내려면 많은 단계를 거쳐야한다.  
    하지만 이를 단번에 꺼내기 위한 어노테이션이 존재한다.  
    @AuthenticationPrincipal 을 사용하면 세션에 저장된 객체를 바로 꺼내 쓸 수 있다.


* 더 알아볼 내용

    1. 
* 후기

    - 권한Role 이 필요하다.  

---