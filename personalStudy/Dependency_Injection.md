# Dependency Injection 의존성 주입

### 의존성 주입을 하는 이유

- 의존
```java
public class Service{

    //
    private HelloWord helloword = new KrHelloword();

    public void printHello(){
        String hello = helloword.getHello();
        String kr = helloword.getCountry();
        System.out.println(hello + " 는 "+kr+"의 인삿말 입니다.");
    }
}
```
위는 서비스 코드를 예시로 든 것이다.  
서비스는 말 그대로 애플리케이션의 다양한 서비스들에 대한 로직을 담당한다.  
코드를 보면 서비스 로직 뿐만 아니라 HelloWord의 구현체로 어떤것이 와야하는지도 정해야한다.  
'정해도 되지'싶을 수도 있는데 서비스는 어떤 구현체가 있어도 똑같이 실행될 수 있어야하며  
서비스의 역할에만 집중해야한다. 분리하지 않으면 코드하나를 바꾸는데 있어서 여러가지 코드를 한번에 수정해야할 것이다.  



--- 
* 몰랐거나 고민했던 것

    1. 
* 풀이

    1. 
* 후기

    1. 

---