# 사전 프로젝트
## User CRUD 생성

### 학습을 앞두고
- 오늘의 목표 :  
User CRUD 완성, 프로필 이미지 등록기능

### 학습 이후
- USER  
원래 내 담당은 아니다. 팀의 백앤드분과 상의한결과 이번 pre-project는 내가 모두 맡기로했다. 차라리 편하다.  
CRUD를 구현했고 연관관계 매핑이 남았다.  
    - 트러블슈팅  
    1. 데이터는 정상적으로 저장되는데 사이트가 안나왔다 -> @RestController가 아닌 그냥 컨트롤러 어노테이션을 붙였다.  
    2. User테이블이 생성되지않음 -> USER가 이미 사용되는 단어라서 생성이 불가능한거였다. @Table(name = "user_table")를 통해 테이블 명을 지정해줬다.  

### 후기
- 기본 CRUD 만들기는 익숙한데, 조금씩 모르는것들이 있다.  
대부분 검색을 하면 나와서 침착하게 하면 될 것 같다.