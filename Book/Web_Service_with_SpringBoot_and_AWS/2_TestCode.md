# Test Code

## **TDD** Test-driven development
>테스트가 주도하는 개발을 뜻한다.  

### 1. 테스트 코드란
- *레드 그린 사이클*  
RED : 항상 실패하는 테스트를 먼저 작성한다.  
GREEN : 테스트가 통과하는 코드를 작성한다.  
REFACTOR : 테스트에 통과하면 코드를 리팩토링한다.  

- TDD는 위 처럼 항상 테스트로 코드를 검증하면서 개발을 이어나가는 개발 방법이라고 할 수 있다.

- 실제 코드로 테스트를 진행하면 눈으로 확인할 수 있기 때문에 당장은 편하지만  
어플리케이션이 무거워질수록 확인하는 속도도 느려질 것이고 테스트를 위해서 코드를 수정하다 실수할 가능성도 존재한다.  

- 매번 기능이 추가될 때 마다 테스트코드를 작성하는 일은 번거롭긴 하지만 장기적인 개발을 위해서 반드시 익혀야할 기술이자 습관이다.

### 2. 테스트 코드 작성하기

```java
@RestController // JSON을 반환하는 컨트롤러
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
```

```java
@Getter
@RequiredArgsConstructor
public class HelloResponseDto {

    private final String name;
    private final int amount;

}
```

```java
class HelloResponseDtoTest {

    // ②
    @Test
    public void 롬복_기능_테스트(){
        //given
        String name = "test";
        int amount = 1000;
        //when
        HelloResponseDto dto = new HelloResponseDto(name,amount);

        //then
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getAmount()).isEqualTo(amount);
    }
}
```

```java
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = HelloController.class)
class HelloControllerTest {

    /** MockMvc
     * 웹 API 테스트에 사용, 스프링 MVC 테스트의 시작점이며
     * 이것을 통해 HTTP GET,POST 등에 대한 API 테스트를 할 수 있다.
     */
    @Autowired
    private MockMvc mvc;

    // ①
    @Test
    public void returnHello() throws Exception{
        String hello = "hello";

        mvc.perform(get("/hello")) // 요청
                .andExpect(status().isOk())  // Status 검증
                .andExpect(content().string(hello)); // 응답 결과 검증
    }

    // ③
    @Test
    public void return_helloDto() throws Exception{
        String name = "hello";
        int amount = 1000;

        mvc.perform(get("/hello/dto")
                .param("name",name)
                .param("amount",String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",is(name))) // json 응답값을 필드별로 검증
                .andExpect(jsonPath("$.amount",is(amount)));
    }
}
```

① HelloController 클래스를 만들고, 제대로 출력이 되는지 테스트를 만들어줬다.

② HelloResponseDto 클래스를 만든 뒤 롬복을 적용해서 롬복 기능이 잘 동작하는지 테스트를 만들었다

③ 마지막으로 HelloResponseDto가 파라미터를 정상적으로 받는지 테스트를 만들었다. 

---
## 기타

- 내장 WAS를 사용하는 이유 : 언제 어디서나 같은 황경에서 스프링 부트를 배포할 수 있기 때문
- API : 어떤 주제에 대한 프로그램 간의 소통을 위해 만들어진 신호체계
