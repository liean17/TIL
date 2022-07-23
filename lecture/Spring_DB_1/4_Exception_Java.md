# 자바 예외 이해

### 예외 계층
Object -> Throwable -> **Exception**  
> 예외를 Throwable로 잡으면 안된다! error까지 잡아버리기 때문이다.  
error는 개발로 해결할 문제가 아니다.  

Exception의 자식인 SQLException, IOException은 컴파일러가 체크하는 **체크 예외**  
하지만 예외적으로 RuntimeException과 그 자식 예외는 모두 **언체크 예외**다.  

### 예외의 기본 규칙
예외는 처리하거나, 처리할 수 없으면 밖으로 던져야한다.  
예외를 잡거나 던질때, 지정한 예외뿐만 아니라 그 예외의 자식들도 함께 처리된다.  

> ***예외를 처리하지 못하고 계속 밖으로 던지계되면?***  
자바 main()쓰레드는 에외 로그를 출력하고 시스템을 종료한다.   
웹 애플리케이션은 WAS가 해당 예외를 받아서 오류 페이지를 보여준다.  

## 체크 예외 

잡아서 처리하거나, 또는 밖으로 던지도록 선언하지않으면 컴파일 오류가 발생한다.  

```java
    static class Service{
        Repository repository = new Repository();

        /**
         * 예외 처리
         */
        public void callCatch(){
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("예외 처리, message={}",e.getMessage(),e);
            }
        }
    }

    static class Repository{
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
```

체크 예외의 장단점  
- 장점  
개발자가 실수로 예외를 누락하지 않도록 컴파일러를 통해 문제를 잡아주는 안전 장치이다.  
- 단점  
개발자가 모든 체크 예외를 직접 처리해야 하기 떄문에 너무 번거로운 일이다.

## 활용
기본적으로 언체크 예외를 사용하는게 좋다.  
체크 예외는 비즈니스 로직 상 의도적으로 던지는 예외에만 사용하자.  
개발자 스스로 중요하게 생각해서 절대 놓쳐서는 안된다고 생각할때 사용한다.
>**ex**  
계좌 이체 실패, 결제 포인트 부족, 로그인 실패  
이 경우에도 100% 체크 예외로 만들어야 하는 것은 아니다.

### 체크 예외의 문제점  
DB혹은 네크워크에서 발생하는 예외의 경우 서비스까지 예외가 올라오게 되는데  
서비스는 이 두 예외를 처리할 방법이 없다. 컨트롤러도 마찬가지라서 결국 ControllerAdvice같은 곳에서 공통으로 처리해야한다.  
하지만 이런 문제를 사용자에게 자세하게 설명하기는 곤란하다.  

```java
public class CheckedAppTest {

    static class Controller{
        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service{
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws ConnectException, SQLException {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient{
        public void call() throws ConnectException {
            throw new ConnectException("ex");
        }
    }
    static class Repository{
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }
}
```
예시 코드의 2가지 문제
1. 복구 불가능 한 예외  
SQL문제, 데이터베이스 문제는 서비스, 컨트롤러에서 복구 불가능하다.  
이런 문제들은 일관성 있게 공통으로 처리해서 오류 로그를 남기고 개발자가 빠르게 인지하는 것이 필요하다.
2. 의존 관계에 대한 문제  
체크 예외는 본인이 처리할 수 없어도 어쩔 수 없이 던지는 예외를 선언해야한다.  
이렇게되면 서비스,컨트롤러는 해당 예외들을 의존하게되는데 만약 기술을 변경하게된다면  
기존 예외에 의존하던 모든 코드를 다시 변경해야한다.
> **Exception 으로 묶어서 처리하는 경우**  
의존관계는 해결이되지만... 모든 예외를 밖으로 던져바리는 문제가 발생한다.  
결과적으로 다른 체크 예외를 체크할 수 없게되어 중요한 체크 예외를 다 놓치게 된다.  
중간에 중요한 체크 예외가 발생해도 문법에 맞기 때문에 컴파일러 오류가 발생하지 않는다.
---

## 언체크 예외

체크 예외와 기본적으로 동일하지만 예외를 던지는 부분을 생략할 수 있다.  
```java
    static class Service{
        Repository repository = new Repository();
        
        //필요한 경우 잡아서 처리도 가능
        public void callCatch(){
            try {
                repository.call();
            }catch (MyUncheckedException e){
                log.info("예외 처리, message={}",e.getMessage(),e);
            }
        }
        //예외를 잡지 않아도 자연스럽게 상위로 넘어간다.
        public void callThrow(){
            repository.call();
        }
    }
    static class Repository{
        public void call(){
            throw new MyUncheckedException("ex");
        }
    }
```

언체크 예외의 장단점
- 장점  
신경쓰고 싶지 않은 언체크 예외를 무시할 수 있다. 또한 신경쓰고 싶지 않은 예외의 의존관계를 참조하지 않아도 된다.
- 단점  
실수로 예외를 누락할 수 있다.

### 활용

```java
public class UnCheckedAppTest {

    static class Controller{
        Service service = new Service();

        public void request(){
            service.logic();
        }
    }

    static class Service{
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic(){
            repository.call();
            networkClient.call();
        }
    }
    static class NetworkClient{
        public void call(){
            throw new RuntimeConnectException("ex");
        }
    }
    static class Repository{
        public void call(){
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }
        public void runSQL () throws SQLException{
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException{
        public RuntimeConnectException(String message){
            super(message);
        }
    }
    static class RuntimeSQLException extends RuntimeException{
        public RuntimeSQLException(Throwable cause){
            super(cause);
        }
    }
}
```
리포지토리에서 체크 예외인 SQLException이 발생하면 런타임 예외로 전환해서 던졌다.  
서비스나 컨트롤러에서 복구 불가능한 예외를 신경쓰지 않아도 된다.  
동시에 의존 관계에 대한 문제 역시 해결되었다.  

> 런타임 예외는 놓칠 수 있기 때문에 문서화가 중요하다.  
