# 트랜잭션 AOP
앞선 코드에서 중복도 제거하고 추상화를 통해 유연성도 늘어났지만 큰 문제가 하나 있다.  
비즈니스 로직은 서비스의 핵심 기능이고 트랜잭션은 코드 전체에 사용되는 공통기능인데  
현재 두 코드가 혼재되어 코드변경시 어려움이 있다.  

---

### **프록시 도입**
서비스 코드는 그대로 두고  
서비스 코드를 대신 실행하는 프록시 객체를 만든 뒤 프록시 객체에 트랜잭션을 적용한다.  
이렇게하면 서비스 코드는 트랜잭션 코드와 분리된채 본래의 기능만 수행할 수 있다.  
이러한 프록시는 ```@Transaction``` 사용을 통해 알아서 구현된다.  

## **트랜잭션 AOP 적용**

```java
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
    }

    //...
}
```
트랜잭션이 필요한 메서드에 **```@Transaction```을 추가하면 끝**이다.  
클래스에 추가하게 되면 public인 메서드들을 트랜잭션 대상으로 인식한다.  

하지만 기존 테스트에 오류가 나는데 스프링 AOP를 적용하려면 스프링 컨테이너가 필요한데  
기존 테스트 코드는 순수 자바코드로 이루어져있기 때문이다.  
그래서 몇가지 수정이 필요하다  
1. 먼저 테스트코드 상단에 @SpringBootTest 어노테이션을 추가한다.  
이 어노테이션은 테스트용 스프링 컨테이너를 만들고 빈들을 사용할 수 있게 해준다. 
2. @TestConfiguration어노테이션을 가진 테스트용 내부 설정 클래스를 만들어 필요한 빈들을 추가한다.  
스프링이 제공하는 트랜잭션 AOP는 스프링 빈에 등록된 트랜잭션 매니저를 찾아서 사용하기 때문에 트랜잭션 매니저를 스프링 빈으로 등록해두어야한다.  

이렇게하면 테스트는 정상작동된다.