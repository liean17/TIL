# 첫번째 협업 프로젝트
[리포지토리 링크](https://github.com/liean17/first-duo-project)

## 오늘 한 것

- 댓글 구현
Posts 내에 댓글을 구현했다.  
Comment 엔티티와 요청,응답,수정에 대한 Dto를 생성했다.  
한 글에는 여러 댓글이 달리기 때문에 일대다 관계다.  
그리고 Comment는 글과 회원에 대한 정보를 모두 가진다.  
구현은 어렵지않았으나 템플릿에서 댓글을 받는데서 문제가 생겼다.  
    - **requestDto가 템플릿에서 입력을 받아오지 못하는 문제 :**   
분명 post로 확실하게 requestDto로 넘겼는데 날짜와 멤버정보는 입력되었으나  
댓글 내용은 로그를 확인해봐도 null만 반환했다.  
requestDto에서 입력한 댓글을 제대로 받지 못하는 문제였는데  
템플릿에 익숙하지않다보니 템플릿 사용문제인가 싶어 한참을 헤맸다.  
    - **해결 :**  
    문제는 requestDto에 생성자를 제대로 만들어주지 않아서였다.  
    @NoArgumentConstructor, @AllArgumentConstructor를 생각없이 사용하다보니  
    정작 필요한 생성자를 만들지않았다.  
    이전에 작성한 코드에서도 쓸모없는 생성자들이 있었을것같다.  
    


---

- @Orderby("fieldname ASC|DESC") 리스트 정렬 어노테이션
- 날짜 생성시 포멧 지정법 : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));