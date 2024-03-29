# 사전 프로젝트
## 프론트와 첫번째 통신 테스트

### 학습을 앞두고
- 오늘의 목표   
프론트와 통신 테스트를 마치고, 중요 수정사항 반영

### 학습 이후
- USER  
원래 내 담당은 아니다. 팀의 백앤드분과 상의한결과 이번 pre-project는 내가 모두 맡기로했다. 차라리 편하다.  
CRUD를 구현했고 연관관계 매핑이 남았다.  
    - 트러블슈팅  
    1. 데이터는 정상적으로 저장되는데 사이트가 안나왔다 -> @RestController가 아닌 그냥 컨트롤러 어노테이션을 붙였다.  
    2. User테이블이 생성되지않음 -> USER가 이미 사용되는 단어라서 생성이 불가능한거였다. @Table(name = "user_table")를 통해 테이블 명을 지정해줬다.  

- SERVER  
프론트와 통신시 CORS오류가 자주 발생한다고한다.  
해결책은 컨트롤러에 ```@CrossOrigin(origins = "*",allowedHeaders = "*")```를 추가해주면 된다.  
서버를 열때, 그냥 8080으로 열어버리면 서버를 열고 작업하는데 있어서 불편함이 생긴다.  
application.properties에 port설정을 해주고 jar파일로 빌드해서 서버와 개발 포트를 분리해야한다.

- 연관관계 오류  
연관관계 매핑과 관련해서 많은, 같은 메세지의 오류들을 만났다.  
해결한 결과 모두 매핑대상을 잘못 적거나 덜 적어서 그런거였다. 즉 이해가 부족하다.  
오류를 고쳐나가면서 무엇이 잘못되었는지는 알게되었지만 최종적으로는 JPA에 대한 공부를 해야겠다.  


### 후기
- 기본 CRUD 만들기는 익숙한데, 조금씩 모르는것들이 있다.  
대부분 검색을 하면 나와서 침착하게 하면 될 것 같다.  

- 성공!  
시도했을때 접할 수 있는 오류에 대해서 이미 들어둔 덕인지 무난하게 성공했다!  
내가 만든 코드들이 다른 사람이 가공한 형태로 나온다는것이 신기했다.  
산 하나는 넘은 느낌인데 물론 앞으로 할게많다. 할수있는만큼 해나가자.
