1. ORM이란
* 객체와 RDB를 매핑(연결)
* 근데 굳이 왜 ORM으로? : 객체와 RDB 간 패러다임의 차이를 메꿔준다.
* 객체를 테이블에 맞춰 디자인 하면 객체지향이 무너진다.
* 객체를 객체답게 모델링하면 DB모델이 무너진다.
* ORM이 위 두 가지 문제를 해결해준다. : 객체는 객체지향적으로, DB는 RDB답게.

2. JPA란
* 압도적인 생산성
* 객체와 RDB모델링을 비교적 자유롭게
* 추상화로 인해 벤더 종속적 환경에서 탈출

3. 사용법
* 라이브러리 추가, persistence.xml(연결할 DB 정보) 정의
    * persistence.xml에서 정의할 수 있는 것들 : 연결할 DB 정보, DB dialect(사용할 DB벤더 선택), DDL/SQL 사용 전략 등
* EntityManagerFactory, EntityManager 생성. @Entity annotation 사용(& @Id), jpa entity 객체는 생성자 정의 시 기본 생성자를 꼭 만들어줘야 한다.
* EntityTransaction - getTransaction() : 트랜잭션 생성 / begin() : 작업 시작 / persist(obj): 객체를 영속화, find(): 객체를 찾음(select) / commit() / close()

4. 내부 동작
* 비영속 : 영속 상태와 아무 상관 없는 상태
* 영속 : 영속성 컨텍스트로 관리중인 상태(EntityManager - persist(object))
    * persist() 한다고 바로 쿼리가 수행되는 것이 아니다. 저장되었다가 commit()할 때 비로소 실행된다.
        * 1차 캐시에 아이디/엔터티/스냅샷(당시 상태)을 저장해둔다.
        * commit() 시점에 내부적으로 flush()가 실행되고 - DB에 쿼리 날림 - 그리고 반영된다.
* 준영속 : 영속성 컨텍스트로 관리되다가 분리된 상태
* 삭제 : 삭제된 상태