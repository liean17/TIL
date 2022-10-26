# 객체지향 쿼리 언어

## JPQL
- SQL을 추상화한 객체 지향 쿼리 언어

```java
String jpql = "select m from Member m where m.username like '%kang%'";
```

- SQL은 테이블이 대상이라면 JPQL은 엔티티 객체를 대상으로 쿼리한다.


## QueryDSL
- 쿼리를 메서드화한 것
- 컴파일 과정에서 오류를 잡기 쉽고, 동적쿼리를 사용하기 좋다.  
- 결국 JPQL을 알아야 한다.

### Native SQL
- SQL을 직접 사용
- JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능
- JDBC 커넥션을 직접 사용하거나, SpringJdbcTemplate 등을 함께 사용 가능하다.  
    - 이 경우 JPA를 사용하지 않기 때문에 flush를 직접 해줘야할 수 있다.

---
# JPQL
- sql을 추상화해서 특정데이터베이스 sql에 의존하지 않는다.
- 결국 jpql은 sql로 변환된다.  

- 문법
    - 엔티티와 속성은 대소문자를 구분
    - jpql키워드는 대소문자를 구분하지않는다.
    - 테이블 이름이 아닌 엔티티 이름을 사용해야한다.
    - 별칭이 필수다.

- 반환타입
    - 쿼리타입
        - TypeQuery<> : 반환 타입이 명확할 때 사용한다.
        - Query : 반환 타입이 명확하지 않을때
    - 결과타입
        - query.getResultList() : 결과가 하나 이상일 때,
        - query.getSingleResult() : 결과가 반드시 하나. 결과가 없거나 두개이상이면 예외가 발생한다.

- 파라미터 바인딩
    - 직접 - ':username' / query.setParameter("username","member1")
    - 위치기반(비추천) - '?1' / query.setParameter(1,"member1") - 위치가 밀릴 위험이 존재한다.  

- 프로젝션(SELECT)
    - 대상 : 엔티티, 임베디드 타입, 기본 타입
    - 여러타입 조회시  
        - Query 타입으로 조회(Object 리스트)
        - new 명령어 사용
            - ```"select new jpql.MemberDto(m.username,m.age) from Member m"```
            