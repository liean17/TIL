# 첫번째 협업 프로젝트
[리포지토리 링크](https://github.com/liean17/first-duo-project)


## 오늘 한 것
- 컨트롤러, 서비스 리팩토링  
: 기존 코드에서 컨트롤러가 많은 역할을 하는 부분이 있어서 서비스에 역할을 위임했다.  
주로 권한과 검증에 관한 코드였는데 최대한 서비스에서 해결하도록 리팩토링했다.  

- 글 Delete 수정, 회원 Delete 추가  
: 글의 삭제는 글을 쓴 사람 혹은 운영자만 가능하고, 그렇지 않은 사람은 권한이 없다는 예외를 띄운다.  
그러나 가입을 하지 않아도 글은 볼 수 있기때문에 삭제버튼을 누를 수도 있는데 이 경우에는 인증정보 자체가 없어서  
nullPointException이 떴다. 비회원이거나 글의 작성자가 아닌경우 템플릿을 통해 삭제 버튼이 보이지 않게하는 것도 가능했지만,  
로그아웃 상태에서 삭제버튼을 누르면 로그인을 위한 창을 띄우는게 더 사용이 좋을 것 같아서 수정했다.  
delete는 해당 페이지에서 포스트 요청이 오면 삭제하는 방식이었기 때문에  
securityConfig의 설정에서 해당 페이지에서 인증된 사람만 Post요청을 보낼 수 있게 설정함으로써 해결했다.  
회원 삭제는 간단하게 인증 정보를 받아서 회원 id와 일치하는지 여부에 따라 메서드를 실행하게했다.

### 후기
- 이전 시간까지는 진행이 너무 잘 되어서 놀랐는데, 서로 잘 모르는 부분에 들어가니 정말 많은 시간을 고민하는데 썼다.  
그리고 코드 리팩토링에도 정말 많은 고민들이 있었다. 현재 코드가 아닌 다른 방법들이 떠올라 그 떠오른 방법이 현재 방법보다  
괜찮은 점이 무엇인지 고민해야했고, 코드가 간결하고 보기에도 알기쉬우나 불필요해보이는 의존관계를 추가해야하는 경우도 있었다.  
생각하고 고민할수록 더 깊어지는 문제여서 일단 서로 최선이라고 여겨지는 방법으로 두었다.  
코딩을 배우면 배울수록 어렵다더니 이런것인가 싶다.  

- 또 페어프로그래밍 전에 확실히 공부를 해야한다는것을 가슴깊이 느꼈다. 어설프게 알다보니 기본 개념조차 깊게 고민하면서  
쓸모없이 보낸 시간들이 많았다. 이런저런 방법을 생각하면서 코드 자체는 많이 쳤는데 결국은 기본에 대한 문제였던 것이다.  
