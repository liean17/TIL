## 엔티티 매니저
- 엔티티 매니저 팩토리는 하나만 생성해서 전체 애플리케이션에서 공유
- 엔티티 매니저는 쓰레드간에 공유하지않는다.  

## 영속성 컨텍스트  
: 엔티티를 영구 저장하는 환경  

- 영속성 컨텍스트가 있음으로써 얻는 이득  
    - 1차 캐시에서 조회가능 : 단 엔티티 매니저가 사라지면 캐시도 사라져서 짧다  
    - 더티체킹 : 변경감지, 영속성 컨텍스트에서 1차캐시의 상태를 저장해두고 커밋시점에 해당 상태와 비교하여 변경이 있으면 반영시킨다.  
    - 지연 로딩  
    - 쓰기 지연
    - 동일성 보장
    
- 플러시 : 영속성 컨텍스트의 변경내용을 데이터베이스에 반영  
플러시가 발생하면, 변경을 감지한 뒤 수정된 엔티티 쓰기 지연 SQL 저장소에 등록 후 쿼리를 데이터베이스에 전송한다.  
참고로 플러시는 영속성 컨텍스트를 비우는 것이 아니다.  

- 준영속 상태  
캐시에 올라간 상태->영속상태  
영속 상태의 엔티티가 영속성 컨텍스트에서 분리되는것  
