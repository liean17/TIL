# 머스테치로 화면 구성하기

### 1. 서버 템플릿 엔진
> 템플릿 엔진이란?

지정된 템플릿 양식과 데이터가 합쳐져 HTML문서를 출력하는 소프트웨어(JSP, Thymeleaf 등)  
템플릿 엔진은 다시 두가지로 나뉜다
- 서버 템플릿 엔진  
서버 템플릿 엔진(JSP)을 사용해서 화면을 생성할 경우, 서버에서 JAVA 코드로 문자열을 만든 뒤, 이 문자열을 HTML로 변환해서 **브라우저에 전달**한다.
- 클라이언트 템플릿 엔진  
서버에서는 Json, Xml 형식의 데이터만 전달하고 화면은 클라이언트에서 조립한다.

> 머스테치 템플릿 엔진  

다른 템플릿 엔진(JSP, Thymeleaf)보다 심플하며  
로직 코드를 사용할 수 없어서 View의 역할이 명확하다.

---

### 2. 기본 페이지 만들기. 
/resources/templates에 index.mustache 생성
```HTML
<!DOCTYPE HTML>
<html>
<head>
    <title>스프링 부트 웹 싸-비쓰</title>
    <meta http-equiv="cContent-Type" content="text/html;charset=UTF-8"/>

</head>
<body>
    <h1>스프링 부트로 시작허는 웹 싸비스</h1>
</body>
</html>
```

/web에 IndexController 생성
```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(){
        return "index";
    }
}
```
테스트 코드
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class IndexControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void 메인페이지_로딩(){
        //when
        String body = this.restTemplate.getForObject("/",String.class); // 1

        //then
        Assertions.assertThat(body).contains("스프링 부트로 시작허는 웹 싸비스");
    }
}
```
1. html body에 있는 문자열을 받아온다.

---

### 3. 게시글 등록 화면  
부트스트랩을 이용해서 화면을 만든다.  
프론트엔드 라이브러리를 사용하는 방법은 두가지이다. 
1. 외부 *CDN*사용  
2. 직접 라이브러리를 받아서 사용  

현재 프로젝트에서는 간단한 1번 방법을 사용하는데, 현업에서는 문제가 발생할 염려가 있기때문에 직접 라이브러리를 받아 사용한다.

### 3.1 header.mustache
```html
<!DOCTYPE HTML>
<html>
<head>
    <title>스프링부트 웹서비스</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head>
<body>
```

### 3.2 footer.mustache
```html
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

<script src="/js/app/index.js"></script>
</body>
</html>
```
css와 js의 위치가 서로 다른 이유는 페이지 로딩속도 때문이다.  
HTML은 위에서부터 코드가 실행되기 때문에 head가 다 실행되고 나서야 body가 실행된다.  
즉, head가 다 불러지지 않으면 백지 화면만 보인다.  
js 용량이 크면 클수록 늦어지기 때문에 js는 body하단에 두어 화면이 다 그려진 뒤에 호출하는 것이 좋다.

### 3.3 적용
index.mustache에 다음과 같이 작성한다
```html
<!DOCTYPE HTML>
<html>
<head>
    <title>스프링 부트 웹 싸-비쓰</title>
    <meta http-equiv="cContent-Type" content="text/html;charset=UTF-8"/>

</head>
<body>

{{>layout/header}}  <!-- 1 -->
    <h1>스프링 부트로 시작허는 웹 싸비스</h1>
    <div class="col-md-12">
        <div class="row">
            <div class="col-md-6">
                <a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
            </div>
        </div>
    </div>
{{>layout/footer}}

</body>
</html>
```

1. {{>~/~}} : 현재 위치를 기준으로 다른 파일을 가져온다.

글 등록주소(posts/save)에 해당하는 컨트롤러를 추가한다  
페이지에 관련된 모든 컨트롤러는 IndexController를 사용한다.
```java
...
@GetMapping("/posts/save")
public String postsSave(){
    return "posts-save";
}
```
그리고 글 등록 위치의 posts-save.mustache 파일을 생성한다.
```html
{{>layout/header}}

<h1>게시글 등록</h1>

<div class="col-md-12">
    <div class="col-md-4">
        <form>
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" class="form-control" id="title" placeholder="제목을 입력하세요">
            </div>
            <div class="form-group">
                <label for="author"> 작성자 </label>
                <input type="text" class="form-control" id="author" placeholder="작성자를 입력하세요">
            </div>
            <div class="form-group">
                <label for="content"> 내용 </label>
                <textarea class="form-control" id="content" placeholder="내용을 입력하세요"></textarea>
            </div>
        </form>
        <a href="/" role="button" class="btn btn-secondary">취소</a>
        <button type="button" class="btn btn-primary" id="btn-save">등록</button>
    </div>
</div>

{{>layout/footer}}
```

자바스크립트 index.js 작성
```js
var main = {
    init: function () {
        var _this = this;
        $('#btn-save').on('click', function () {
            _this.save();
        });
    }, save: function () {
        var data = {
            title: $('#title').val(),
            author: $('#author').val(),
            content: $('#content').val()
        };
        $.ajax({
            type: 'POST',
            url: '/api/v1/posts',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert('글이 등록되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
};
main.init();
```

이후 어플리케이션 실행 후 8080/posts/save에 가보면 페이지가 뜨고  
글 등록 역시 정상적으로 된다.

---
### 4 전체조회

### 4.1 index.mustache UI변경
```html
<!DOCTYPE HTML>
<html>
<head>
    <title>스프링 부트 웹 싸-비쓰</title>
    <meta http-equiv="cContent-Type" content="text/html;charset=UTF-8"/>

</head>
<body>

{{>layout/header}}
    <h1>스프링 부트로 시작허는 웹 싸비스</h1>
    <div class="col-md-12">
        <div class="row">
            <div class="col-md-6">
                <a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
            </div>
        </div>
        <br>
        <table class="table table-horizontal table-bordered">
            <thead class="thead-strong">
            <tr>
                <th>게시글번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>최종수정일</th>
            </tr>
            </thead>
            <tbody id="tbody">
            {{#posts}} <!-- 1 -->
                <tr>
                    <!-- 2 -->
                    <td>{{id}}</td> 
                    <td><a href="/posts/update/{{id}}">{{title}}</a></td>
                    <td>{{author}}</td>
                    <td>{{modifiedDate}}</td>
                </tr>
            {{/posts}}
            </tbody>
        </table>
    </div>
{{>layout/footer}}

</body>
</html>
```
1. {{#post}} : posts 라는 List를 순회한다 = 자바의 for문
2. {{변수명}} : List에서 뽑아낸 객체의 필드를 사용한다.

### 4.2 Repository 변경
PostsRepository에 Query를 추가한다
```java
public interface PostsRepository extends JpaRepository<Posts,Long> {

    @Query("SELECT p FROM Posts p ORDER BY p.id DESC")
    List<Posts> findAllDesc();

}
```
> Querydsl

FK의 조인, 복잡한 조건 등으로 Entity 클래스 만으로 처리하기 어려운 경우, 조회용 프레임 워크를 추가로 사용한다.  
저자는 Querydsl을 추천하는데 이유는 다음과 같다.  
1. 메소드를 기반으로 쿼리를 생성하기 때문에 오타나 존재하지 않는 칼럼명을 명시할 경우 IDE에서 자동으로 검출된다.
2. 국내 많은 회사에서 이용중이며 그에 따라 참고 자료가 많다.

### 4.3 PostsService 변경
아래 코드 추가
```java
    @Transactional(readOnly = true)  // 1
    public List<PostsListResponseDto> findAllDesc(){
        return postsRepository.findAllDesc().stream()
                .map(PostsListResponseDto::new)
                .collect(Collectors.toList());
    }
```
1. 트랜잭션 범위는 유지하되, 조회 기능만 남겨두어 조회 속도가 개선된다.  
등록, 수정, 삭제기능이 전혀 없는 서비스 메소드에 추천된다.

PostsListResponseDto 생성
```java
@Getter
public class PostsResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;

    public PostsResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
    }
}
```

### 4.3 Controller 변경
@RequiredArgsConstructor와
postsService 필드 추가도 해야한다
```java
    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("posts",postsService.findAllDesc());
        return "index";
    }
```

---

### 5. 게시글 수정 구현
게시글 수정 API는 이미 PostsApiController에 구현되어있다.

### 5.1 Posts-update.mustache 
```html
{{>layout/header}}

<h1>게시글 수정</h1>

<div class="col-md-12">
    <div class="col-md-4">
        <form>
            <div class="form-group">
                <label for="title">글 번호</label>
                <input type="text" class="form-control" id="id" value="{{post.id}}" readonly> <!-- 1 -->
            </div>
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" class="form-control" id="title" value="{{post.title}}">
            </div>
            <div class="form-group">
                <label for="author"> 작성자 </label>
                <input type="text" class="form-control" id="author" value="{{post.author}}" readonly>
            </div>
            <div class="form-group">
                <label for="content"> 내용 </label>
                <textarea class="form-control" id="content">{{post.content}}</textarea>
            </div>
        </form>
        <a href="/" role="button" class="btn btn-secondary">취소</a>
        <button type="button" class="btn btn-primary" id="btn-update">수정 완료</button>
        <button type="button" class="btn btn-danger" id="btn-delete">삭제</button>
    </div>
</div>

{{>layout/footer}}
```

1. {{post.id}} : 객체의 필드 접근 방법. {{객체명.필드명}} 으로 접근한다.

그리고 버튼을 누르면 update기능을 호출할 수 있게 js파일도 수정한다
```js
var main = {
    init: function () {
        var _this = this;
        $('#btn-save').on('click', function () {
            _this.save();
        });
        $('#btn-update').on('click', function () {
            _this.update();
        });
    }, save: function () {
        var data = {
            title: $('#title').val(),
            author: $('#author').val(),
            content: $('#content').val()
        };
        $.ajax({
            type: 'POST',
            url: '/api/v1/posts',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert('글이 등록되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
    update : function () {
        var data = {
            title: $('#title').val(),
            content: $('#content').val()
        };

        var id = $('#id').val();

        $.ajax({
            type: 'PUT',
            url: '/api/v1/posts/'+id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('글이 수정되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
};
main.init();
```

마지막으로 수정 화면을 연결할 Controller에 메서드를 추가한다
```java
    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model){
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post",dto);
        
        return "posts-update";
    }
```
id와 model을 받아서  
해당 게시물을 가져와 보여준다.

### 6. 게시글 삭제

### 6. js에 삭제 이벤트 추가
```js

    delete : function () {
        var id = $('#id').val();

        $.ajax({
            type: 'DELETE',
            url: '/api/v1/posts/'+id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8'
        }).done(function() {
            alert('글이 삭제되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
```

서비스 메소드에 delete 추가
```java
@Transactional
    public void delete(Long id){
        Posts posts = postsRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 없습니다. id="+id));
        postsRepository.delete(posts); // 1
    }
```
1. JpaRepository에서 지원하는 delete 메소드를 사용한다.  
존재하는 Posts인지 확인하고 그대로 삭제한다.

이후 컨트롤러를 수정한다  
```java
public class PostsApiController{

    @DeleteMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id){
        postsService.delete(id);
        return id;
    }
}
```

---
## 기타

- CDN : Content Delivery Network 지리적 제약 없이 전 세계 사용자에게 빠르게 콘텐츠를 전송하는 기술

