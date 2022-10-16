# 엔티티 매핑

1. 객체와 테이블 매핑  
@Entity가 붙은 클래스는 JPA에서 관리된다.  
    - 기본생성자 필수
    - final, enum, interface, inner 클래스 사용 불가
    - 저장할 필드에 final 불가  
    - 속성 name : JPA에서 사용할 엔티티 이름을 지정한다.
    - @Table(name = "") : 엔티티와 매핑할 테이블 지정   

    > **데이터베이스 스키마 자동생성**  
    create : 기존 테이블 삭제 후 다시 생성  
    create-drop : 종료시점에 drop. 
    update : 변경분만 반영  
    validate : 정상 매핑 여부만 확인  

    > ***운영 장비에는 절대 create, update 금지***  
    개발 초기 : create 또는 update  
    테스트 서버 : update 또는 validate  
    스테이징, 운영서버 : validate 또는 none  

2. 필드와 컬럼 매핑  
@Column 
    - name : 객체와 테이블 매핑 이름 설정   
    - insertable, updatable : 등록 변경 가능 여부
    - nullable : null값 적용 여부
    - unique : 한 컬럼에 간단히 유니크 제약조건을 거는것 -> 이름 반영이 어려워서 @Table에서 uniqueConstraints를 설정하는 것이 선호된다.  


    @Temporal(TemporalType.TIMESTAMP) : 시간타입 설정 -> 최신버전은 LocalDate, LocalDateTime 타입은 그냥 저장 가능  
    @Lob : varchar를 넘어서는 큰 타입  
    @Enumerated : Enum타입 매핑, ORDINAL은 순서를 데이터베이스에 저장하는 것.  

3. 기본 키 매핑  
- @Id : 직접할당
- @GeneratedValue
    - AUTO : DB에 맞게 자동 생성
    - IDENTITY : DB에 위임.  
    Insert 쿼리를 날려야 PK값이 생성되기 때문에 persist한 시점에 쿼리가 날아간다.  
    따라서 모아서 insert하는 전략이 불가능하다.(하지만 버퍼링을 두고 쿼리를 날리는 전략이 현저한 효과는 보기어렵다.)  
    - SEQUENCE : 시퀸스 오브젝트 사용, ORACLE.  
    - TABLE : 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략  
    > 권장하는 식별자 전략  
    null이 아니고, 변하면 안된다.  
    미래까지 이 조건을 만족하는 자연키는 찾기 어렵기 때문에 Long형 + 대체키 + 키 생성전략 사용을 권장한다.  

