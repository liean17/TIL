# 트랜잭션 AOP 주의사항

## 1. 내부호출
- `@Transational` 을 사용하면 스프링 트랜잭션 AOP가 적용된다.
- 이후 프록시 객체가 요청을 먼저 받아서 트랜잭션을 처리한 후 실제 객체를 호출해준다.
- 따라서 트랜잭션을 적용하려면 항상 프록시를 통해서 객체를 호출하게 된다.
- **그러므로** 프록시를 거치지 않고 대상 객체를 직접 호출하게되면 AOP가 적용되지 않고, 트랜잭션도 적용되지 않는다.  
    - 일반적으로 대상 객체를 직접 호출하는 일은 없지만, **대상 객체 내부에서 메서드 호출이 발생하면 프록시를 거치지않게된다.**

### 코드
~~~java
@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired CallService callService;

    @Test//내부에 트랜잭션이 있으니 프록시가 등록될것
    void printProxy(){
        log.info("callService class={}",callService.getClass());
    }

    @Test
    void internalCall(){
        callService.internal();
    }

    @Test
    void externalCall(){
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig{
        @Bean
        CallService callService(){
            return new CallService();
        }
    }

    @Slf4j
    static class CallService{
        
        public void external(){
            log.info("call external");
            printTxInfo();
            
            internal();
        }

        @Transactional
        public void internal(){
            log.info("call internal");
            printTxInfo();
        }

    }
    static void printTxInfo(){
        boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("tx active={}",txActive);
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        log.info("tx readOnly={}",readOnly);
    }
}
~~~
internal()을 실행하면 정상적으로 트랜잭션이 적용된다.  
하지만 external()를 실행하면 내부에서 실행하는 internal()마저 트랜잭션이 적용되지 않는다.

### 이유
- 자바에서 메서드를 실행할때는 일반적으로 'this.'가 생략된다.  
this.는 나 자신의 인스턴스 주소를 뜻하므로 실제 객체의 인스턴스를 가져와서 프록시를 거치지 않게되는 것이다.  
- 따라서 프록시 방식의 AOP는 메서드 내부 호출에 프록시를 적용할 수 없다.  

### 해결
- 어렵지만 다른 코드를 수정하지 않는 방법으로는 컴파일 과정에서 트랜잭션을 알아서 적용하도록 직접 AOP관련 코드를 수정하는 방법이 있다.  
- 그리고 간단한 방법으로는 애초에 별도의 클래스로 분리하는 것이 있다.  
```java
@Slf4j
@SpringBootTest
public class InternalCallV2Test {

    @Autowired CallService callService;

    @Test
    void printProxy(){
        log.info("callService class={}",callService.getClass());
    }

    @Test
    void externalCallV2(){
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig{
        @Bean
        CallService callService(){
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService(){
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService{

        private final InternalService internalService;

        public void external(){
            log.info("call external");
            printTxInfo();
            //외부 클래스의 메서드를 스행
            internalService.internal();
        }

    }
    static void printTxInfo(){
        boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("tx active={}",txActive);
    }

    static class InternalService{

        @Transactional
        public void internal(){
            log.info("call internal");
            printTxInfo();
        }

        static void printTxInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}",txActive);
        }
    }
}
```
external()이 호출한 internal()은 외부의 interService의 메서드이므로  
프록시인 interService의 메서드를 실행하는 것이 되어 트랜잭션이 적용된다.  

---

## 초기화
- 초기화 코드에 @Transactional 을 함께 사용하면 트랜잭션이 적용되지 않는다.
- 초기화 코드가 먼저 호출 된 다음에 트랜잭션 AOP가 적용되기 때문이다.  
```java
@PostConstruct
@Transactional
public void initV1(){
    boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
    log.info("Hello init @PostConstruct tx active={}",actualTransactionActive);
}

@EventListener(ApplicationReadyEvent.class)
@Transactional
public void initV2(){
    boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
    log.info("Hello init ApplicationReadyEvent tx active={}",actualTransactionActive);
}
```
@EventListener를 사용해서 초기화가 끝났을때 실행하도록 설정하면 된다.

--- 
### public - transacion
- 스프링의 트랜잭션 AOP 기능은 public 메서드에만 적용되는 것이 기본설정이다.  
- 클래스 레벨에서 트랜잭션을 적용하는 경우, 필요한 메서드에만 트랜잭션이 적용되도록 하기 위함이다.  
- public이 아닌 메서드에 적용하면 예외가 발생하지않고 그냥 무시된다.
    - 따라서 주의가 필요하다.