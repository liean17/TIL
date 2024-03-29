# 첫번째 멘토링

### 사전질문
1. 프로젝트관련
    - 예외처리 시점(저번에는 기본적인 모든것을 구현한 후 하려다 시간이다됨)
    - 업데이트할때 setter를 사용하는게 좋을지, 새로운
    - 백엔드 끼리 협업할때 방식

2. 그외
    - 테스트 코드 작성시점
    - 엔지니어가 되기 위해 어떤것을 준비하셨는지


### 답변들
- **왜 이렇게 했는가?**

- DB정규화 - 일반성  
비슷한 정보가 산재되면 저장된 정보가 꼬인다.  

- 모임 테이블에 활동중인 인원은 항목을 따로 넣지않고 매핑테이블을 사용해서 count를 통해서 그냥 주면되지않나
모임장 ID를 회원 PK로걸어두는게 좋다.  

- 코어 지역에서 VIEW에 대한 관심을 지워야한다.  
회원수는 저장된 회원을 세면 되지 엔티티가 나서서 보여줘야하는 정보가 전혀 아니다.

- 팁 : 연령같은건 데이터로받아야한다.  
  지역 : 시군구를 데이터로받기 혹은  지도 api를 활용해서 좌표값을 저장하기

- 사진 : s3을 주로 이용한다.  
여러개를 올리려고 하면 테이블을 쪼개는게 좋다.  

- 프론트엔드와 nullable 에 대한 명시가 필요하다. 거기에서 많은 오류가 발생할 것이다.

- LocalDataType 에 대한 파싱이 필요하다. 프론트에서 LocalDataType을 바로 넘겨주지 않는다.

- Pet엔티티가 독립적일 필요가 있나?  
Pet이 독립적으로 무언가 실행하는게 없다면, members에 종속되면되지 굳이 엔티티를 가져야하는가에 대한 고민이 필요하다.

- 아이디를 email로 받으면 거기에 대한 유효성검사를 어떻게할지 고민이 필요하다.  

- unit테스트부터 할것. 테스트코드는 코드 작성을 효율적이게 만들어주는 것이지 단순히 내 코드를 검사하는것이 아니다.

- 많은 것을 배우지 않아도 좋은 평가를 받는 신입

1. 코드를 짤때 고민을 많이한 사람  
기본적인 코드를 작성함에도 많은 고민을 한 것이 보이는 사람.   
시간복잡도 측면에서 효율적이어야 한다는 것이 아니라, 있어야할 자리에 있게하는 것.  
2. 배운것에 대해서 정확하게 아는 사람  
이걸 배웠다 는 그걸 안다는건 아니다  
aws를 모른다고 두려워할게아니라 배워라

## 숙제
- aws (이틀안에 하기)
숙제 : VPC만들고 그안에 private, public subnet 만들기   
EC2는 프라이빗 서브넷에 띄우기 - 로컬에서 접근이 안될거임.  
LoadBalancer를 사용해서 접근이 되도록 하라

<!-- - 임베디드로 가기위한 노력  
웹에 대한 지식은 필수  
웹과 큰 차이는 물리적인 공간이 있다는것 - 한정된 공간을 다루기(알고리즘필수)  
운영체제 등 컴퓨터관련 지식이 필수적이다.  
C++로 알고리즘을 풀 수 있어야한다. 파이썬은 느려서 사용하지도 못한다. -->

- 취업팁 : 코틀린을 배워라 -> 자바에서 코틀린으로 변경하고있는 회사들이 많다.  
- (스타트업)채용공고 미리봐라.
- 지금부터 찾아보고, 공부해라.

---

## 후기  
- 한줄 소감을 남긴다면 멘토님은 정말 좋은 분이시다.

### 빠른 진행
- 오늘 코드를 작성하면서 느낀게 속도가 정말 빨랐다.  
내가 프리프로젝트때 대체 뭘하고 있었던거지라는 생각이 들 정도로 금방금방했다. 다른 백엔드분 코드만 작성되면 다듬기만 하면 끝일거라 생각했다.  
그리고 멘토링을 한 결과, 그게 결코 잘 하고 있는것만은 아니라는 것을 깨닫게되었다.  
'dto가 어떤 역할을 하고 어떤 책임을 갖는가?' dto매핑에 대해서만 이러쿵 저러쿵 고민하면서 정작 dto자체에 대해서는 큰 고민을 해본적이 없었다.  
코드작성, 문서화에 대해서 이런 질문들을 수없이 받았고 그 결과 나는 정말 의심없이 코드를 작성해왔다는 것을 알게되었다.  
코드에 정답은 없다곤 하지만 내심 내가 배워왔던 것들 중에서 정답을 정하고 그것만 해왔던 것이다.  
고민하지않은 결과로 불필요하게 만들어진 로직들이 있었다. 상관은 없지만 상당히 효율이 떨어지고 데이터베이스에서 많은 비효율을 가지고 온다고 들었다.  

- 확실히, 전혀, 앞서나갈때가 아니었다. 부족한것은 채우고 아는것도 계속 질문해야한다.  
일단 배포부터 하고, 기본적인 코드부터 제대로 고민하자. 추가적인 욕심은 그 뒤에하자.
