# 연관관계 매핑 기초

### 목표
객체와 테이블연관관계의 차이를 이해  

### 연관관계의 주인  
객체의 연관관계  : <-/-> 단방향 두개  
테이블의 연관관계  : <-> 양방향 하나  
따라서 객체의 경우, 둘 중 하나(연관관계의 주인)를 선택해서 외래 키를 관리해야한다.  

연관관계의 주인이 아닌 쪽은 읽기만 가능해야하며  
mappedBy 속성을 사용하지 않는다.  

연관관계의 주인은 Many(외래 키가 있는 곳)쪽에서 가져가는 것이 추천된다.  
one이 주인이 되는 경우, many하나만 변경되도 다른 테이블에 업데이트 쿼리가 나간다.  

- 순수 객체 상태를 고려해서 항상 양쪽에 값을 설정하는게 좋다.  

### 양방향 매핑  
단방향 매핑만으로도 이미 연관관계 매핑은 완료된다.  
따라서 양방향 매핑은 필요할 때 추가하면된다. 테이블에 영향을 주지 않기 때문이다.  

> 컨트롤러는 엔티티를 반환하면 안된다.  
1. 무한루프문제  
2. 엔티티변경하는 순간 API스펙이 변경되어버린다.  