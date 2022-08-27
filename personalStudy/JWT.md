# JWT

## 개인공부

- JWT 인증방식을 숙련한다.

### 정리  
- 토큰의 전달 원리  
Spring Security 설정으로 HTTP요청시 헤더에 특정 문자를 입력하지 않으면 접속할 수 없게 할 수 있다.  
```java
public class FirstFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setCharacterEncoding("UTF-8");
        if(req.getMethod().equals("POST")){
            String headerAuth = req.getHeader("Authorization");
            //인증값이 아래 설정한 값이 아니면 실패
            if (headerAuth.equals("Google")){
                chain.doFilter(req,res);
            }else{
                PrintWriter writer = res.getWriter();
                writer.println("인증 실패");
            }
        }
    }
}
```
그리고 SecurityConfig에 아래 코드를 추가한다. 

```http.addFilterBefore(new FirstFilter(), BasicAuthenticationFilter.class);```

Authorization값이 Google인 경우에만 페이지를 보여준다.  


---

* 후기

    - Security를 이용해 만들어진 Authentication을 통해 jwt토큰을 만들어 header로 전달하며 요청,응답한다.  
    해독에 필요한 코드도 정의가능한 듯 하다.  
    한번 쭉 반복했는데 아주 기초적인 흐름정도만 이해가간다.  좀더 학습이 필요하다

---