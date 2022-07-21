# 트랜잭션 Transaction

### 트랜잭션은 어디에 사용해야 하는가?
비즈니스 로직이 있는 서비스 계층에서 시작해야한다.  
비즈니스 로직이 잘못되면 해당 비즈니스 로직으로 인해 문제가 되는 부분을 함께 롤백해야 하기 때문이다.  
그리고 트랜잭션을 사용하는 동안에는 같은 커넥션이 유지되어야한다.

---

## DB 락  
트랜잭션 자체는 원자성있게 진행되지만, 만약 또 다른 곳에서 같은 데이터를 수정하는 경우 원자성이 꺠지게 된다.  
DB락은 먼저 데이터 수정을 시작한 세션에게 수정 권한(락)을 부여해서 그동안 다른 사람이 데이터를 수정할 수 없게한다.  
락을 얻지 못한 사람은 그대로 수정에 실패하는게 아니라 일정 시간동안 대기하게되며 대기시간이 지나면 실패하게 된다.  

조회 시점에서도 락을 획득할 수 있다.  
```select for update``` 구문을 사용하면 조회할 때 락을 획득할 수 있는데  
일반적으로는 조회시점에서 락을 사용하지 않지만  
데이터를 조회한 후 중요한 계산을 하는데 사용한다면 조회시점에도 락이 필요할 수 있다.

---

## 트랜잭션 적용
애플리케이션에서 DB 트랜잭션을 사용하려면 트랜잭션으 사용하는 동안 같은 커넥션을 유지해야한다.  
대표적으로 커넥션을 파라미터로 매번 전달해서 사용하는 방법이 있다. 
```java
public void update(Connection con, String memberId, int money) throws SQLException {
    String sql = "update member set money=? where member_id=?";
    PreparedStatement pstmt = null;
    try {
        pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, money);
        pstmt.setString(2, memberId);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        log.error("db error", e);
        throw e;
    } finally {
    //connection은 여기서 닫지 않는다.
    JdbcUtils.closeStatement(pstmt);
    }
}
```
위 Repository코드 뿐만 아니라 서비스코드에서 사용할때도 커넥션을 가져오는 것은 물론이고  
try catch문을 통해 커밋, 롤백 코드를 일일이 작성해야한다.  
이렇게하면 긴 트랜잭션코드가 추가되어 비즈니스 코드를 알아보기 어려운 단점이 존재하게된다.

---

## Spring에서 트랜잭션

위에서 작성한 트랜잭션 코드는 크게 3가지 문제점을 가지고 있다

1. 트랜잭션 문제 : JDBC 구현 기술이 서비스 계층에 포함되어버렸고, 커넥션을 계속 파라미터로 넘겨야 하며, 코드 반복역시 많다.
2. 예외 누수 : 데이터 접근 계층의 예외(SQLException)이 서비스 계층으로 전파된다.
3. JDBC 반복 문제 : JDBC 코드에서 반복이 너무 많다.  

위 문제들을 스프링을 통해 해결해보자.  

---

### 트랜잭션 추상화
앞선 코드의 가장 큰 문제는 JDBC -> JPA 와 같이 기술을 변경하고자 하면 모든 코드를 변경해야한다는 점이다.  
이런 번거로움을 방지하기위해서 추상화를 사용할 수 있는데 스프링에 이미 구현된 기술이 있다.  

그리고 이전에는 같은 커넥션을 사용하기 위해서 파라미터로 커넥션을 전달했는데 스프링에서 제공하는 트랜잭션 매니저를 사용하면  
트랜잭션 동기화 매니저를 통해 같은 트랜잭션을 사용할 수 있도록 해준다.  

```java
DataSourceUtils.getConnection();
DataSourceUtils.releaseConnection();
```
트랜잭션 동기화를 위해서는 위 코드를 통해 트랜잭션을 가져오고, 반환하도록 해야한다.  
이때 트랜잭션 동기화 매니저가 관리하는 커넥션이 없으면 매니저가 새로 커넥션을 생성하게되고  
relese한 경우 기존에 있던 커넥션이 아니라 매니저가 생성한 커넥션이라면 알아서 종료해준다.  

---

### 트랜잭션 추상화 이후 트랜잭션 과정

- 트랜잭션 시작  
    1. 클라이언트 요청으로 서비스 로직이 실행된다.  
    2. 서비스 계칭에서 **트랜잭션 매니저**를 호출해서 트랜잭션을 시작한다.
    3. 트랜잭션 매니저는 내부의 **DataSource**를 사용해서 커넥션을 생성한다.
    4. 트랜잭션 매니저는 수동 커밋 모드로 변경 후 실제 트랜잭션을 시작한다.
    5. 생성된 커넥션은 **트랜잭션 동기화 매니저**에 보관한다
    6. 트랜잭션 동기화 매니저는 **쓰레드 로컬**에 커넥션을 보관한다.
- 로직 실행
    1. 서비스가 비즈니스 로직을 실행하면서 리포지토리의 메서드들을 호출한다
    2. 리포지토리 메서드들은 ```DataSourceUtils.getConnection()```을 사용해서 트랜잭션 동기화 매니저에 보관된 커넥션을 꺼내 사용한다.
    3. 획득한 커넥션을 사용해서 SQL을 데이터베이스에 전달해서 실행한다.
- 트랜잭션 종료
    1. 비즈니스 로직이 끝나고 트랜잭션을 종료한다. 커밋하거나 롤백하게되면 종료된다.
    2. 트랜잭션을 종료하기 위해 필요한 커넥션을 트랜잭션 동기화 매니저에서 가져온다.
    3. 획득한 커넥션을 통해 트랜잭션을 커밋하거나 롤백한다.
    4. 전체 리소스를 정리한다.
        - 트랜잭션 동기화 매니저를 정리
        - AutoCommit설정을 true로 되돌린다.
        - 커넥션 close()를 호출해서 커넥션을 종료한다.     

---

### 트랜잭션 탬플릿  
이로써 JDBC를 쓰던 JPA를 쓰던 서비스 코드를 변경하지 않을수 있게 되었다.  
이번에는 트랜잭션 사용시 반복되던 try / catch문을 템플릿을 통해 깔끔하게 바꿀 수 있다.  

```java
public class MemberService{

    //private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepository memberRepository;

    public MemberService(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        txTemplate.executeWithoutResult((status) -> {
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }
//        try{
//            //비즈니스 로직
//            bizLogic(fromId, toId, money);
//            transactionManager.commit(status);
//        }catch (Exception e){
//            transactionManager.rollback(status);
//            throw new IllegalStateException(e);
//        }
    //...

}
```
```TransactionTemplate```라고하는 스프링이 제공하는 템플릿을 사용한다.  
트랜잭션 매니저 대신 해당 템플릿을 DI 하고  
생성자에서 생성시 템플릿이 트랜잭션매니저를 가질수있도록 하면 된다.  
이전보다 코드가 확실히 줄었다.  
