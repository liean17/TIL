# 첫번째 협업 프로젝트
[리포지토리 링크](https://github.com/liean17/first-duo-project)

## 오늘 한 것
- 공지 기능  
공지로 설정된 글이 항상 상단에 위치할 수 있어야한다.  
    - 시도 : 템플릿에서 글 타입이 '공지'인것은 위에 한번 더 출력되게해봤다.  
    페이지네이션 때문에 현재 페이지에 글이 없으면 첫페이지라도 조회되지않는 문제가 발생했다.  
    : **애초에 정렬할때, 가장 위에 공지글이 오도록하면 되지 않을까?** or **템플릿에 공지를 따로 뿌리면?**  
    
    - 성공 : 공지만 따로 찾아서 공지리스트를 따로 뿌리는 방식을 택했다.  

        ```java
        public List<Posts> findNotice(){
            return postRepository.findAllByPostType(PostType.NOTICE);
        }
        ```
        Repository에 PostType별로 글을 찾을 수 있는 메서드를 명명한 후 Service에 공지를 찾는 메서드를 만들었다.  
        이후 컨트롤러에서 페이지네이션을 함과는 별개로 위 메서드를 통해서 찾은 공지를 modelAttriubute전달하도록했다.  
        그리고 템플릿에서 따로 받은 공지를 항상 기본 글위에 위치하도록 하면 된다.   
        PostType으로 글을 찾는 메서드는 이뿐만 아니라 글 타입별로 조회하는 데에도 사용하기 좋을 것 같다.

- 새로 작성한 모든 글이 잡답으로 저장되는 문제  
어떤 타입을 설정하건간에 새로 작성한 글은 잡답(PostType.CHAT)으로 저장되는 문제를 해결해야한다.  
    - 시도 : dto부터 무엇을 받는지 탐색해봤는데 **null값이 아니면 기본값으로 잡답이 설정되도록** 코드가 잘 짜여있었다.  
    즉 템플릿에서 글 타입을 제대로 받지 못한다는 것이다.  

    - 성공 : dto가 postType을 정확히 받을 수 있도록 수정해줬다.  

        ```html
        <!--수정전-->
        <select th:field="*{postType}" class="form-select">
                        <option th:each="postType : ${postTypes}" th:value="${postType}"
                                th:text="${postType.type}">CHAT</option>
                    </select>
        ```

        ```html
        <!--수정후-->
        <select name="postType" class="form-select">
                        <option th:each="postType : ${postTypes}" th:value="${postType}"
                                th:text="${postType.type}">CHAT</option>
                    </select>
        ```
        th:field="*{}"이 코드는 객체의 필드를 지정해서 데이터를 입력하는 코드다.  
        그리고 객체는 th:object="${}"로 지정해야하는데 이것도 없어서 그냥 없는 목적지에 값을 넣는 코드였다.  
        제대로 dto에 값이 입력되도록 name 태그를 사용해서 이름을 지정해줬다.  
        아마 이렇게가 아니라 위 처럼하는 대신에 th:object="${}"를 사용했어도 해결되었을 것 같다.  
        select태그가 아닌 option 태그에 name을 입력했다가 안되서 아주 잠깐 헤맸다.
        
        
        
        