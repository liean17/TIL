# 스프링과 문제 해결
기존 리포지토리는 다른 기술을 사용할떄 유연성이 떨어진다.  
따라서 인터페이스를 통해 편하게 변경할 수 있도록 하면 되는데 문제는 예외처리이다.  
인터페이스를 만들어도 체크 예외는 인터페이스까지 올라와서 인터페이스가 특정 예외에 종속적이게 되어버린다.  
이를 해결할 수 있는 것은 언체크 예외이다.  

```java
package hello.jdbc.repository.ex;

public class MyDbException extends RuntimeException{

    public MyDbException() {
    }

    public MyDbException(String message) {
        super(message);
    }

    public MyDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDbException(Throwable cause) {
        super(cause);
    }
}

```
```java
// Repository에서의 코드//
catch (SQLException e) {
    throw new MyDbException(e);
}
```

RuntimeException을 상속받는 임의의 예외를 만들고  
Repository에서 예외를 던질때 해당 예외로 감싸면 체크예외이기 때문에 더이상 SQLException을 여기저기 던지지 않아도된다.  

하지만 지금 방식은 새로 만든 임의의 예외만 발생하기 떄문에 예외의 구분이 어렵다.  

해결방법은 있는데, 예외에는 데이터베이스마다 지정한 예외 코드가 따라온다.  
그렇기에 다시 임의의 예외를 만든 후 예외 코드를 받아서 적절한 처리를 해줄 수 있도록 하면 된다.  
그러나 이 해결책의 문제는 예외코드가 한두가지가 아니라 수백가지가 존재하고 데이터베이스마다 다르기 때문에
일일이 지정해줄 수 없다는 것이다.

--- 

## 스프링 예외 추상화
스프링은 데이터 접근 계층에 대한 수십 가지의 예외를 정리해서 일관된 예외 계층을 제공한다.  
RuntimeException을 상속받은 ```DataAccessException```이라는 것을 제공하는데  
일시적인 예외인 ```Transient```와, 반복해서 실행하면 실패하는 ```NonTransient```두가지로 나뉜다.  
