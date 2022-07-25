# 게시판 만들기 일지

- 게시판을 만들면서 있었던 모든것을 기록한다.  
- 사소한 실수부터 고민, 갈등 모두 기록한다.

- 의존성  
    - Spring Web
    - Lombok
    - Thymeleaf
    - Validation
    - Spring Data JPA
    - H2(220725추가)
    - SLF4J(220725추가)
    
---

## 220725
- 이것저것 생각하기보다 일단 등록,조회,수정,삭제가 가능한 게시판을 만들고자 한다.  
- 간단하게 H2데이터베이스를 사용하며, 로그인은 일단 구현하지 않는다.

- Repository 테스트
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @AfterEach
    public void clean(){
        postRepository.deleteAll();
    }

    @Test
    public void 게시글저장(){
        //given
        String title = "title";
        String content = "content";

        postRepository.save(Post.builder()
                .title(title)
                .content(content)
                .author("kdk").build());
        //when

        List<Post> all = postRepository.findAll();

        //then

        Post post = all.get(0);
        assertThat(post.getPostId()).isEqualTo(1);
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getAuthor()).isEqualTo("kdk");
    }
}
```
테스트는 전에 이동욱님의 책을 바탕으로 작성했다.
> **uses unchecked or unsafe operations.**  
테스트를 실행하니 위 오류가 발생했다.  
원인은 JpaRepository를 만들때 제네릭 타입을 확실히 지정해주지 않아서였다.  
아무리 알아서해주는게 많다지만 이정돈 지정해야하는 듯 하다.  
모르면서 무작정 따라치지 않으려고 용도를 모르면 일부러 비워뒀기 때문에 어느정도 의도된 오류였다.  


- ### Post, Get  
등록 폼, 조회 폼은 모두 김영한님의 강의에 있는 html을 참고했다.  
***Controller***
```java
@RequiredArgsConstructor
@Controller
public class PostController {

    private final PostService postService;

    @GetMapping("/post/{id}")
    public String getPost(@PathVariable Long id, Model model){
        PostResponseDto post = postService.findById(id);
        model.addAttribute("post",post);
        return "posts/post";
    }

    @GetMapping("/post")
    public String getAddPost(){
        return "posts/addPost";
    }

    @PostMapping("/post")
    public String addPost(@ModelAttribute PostSaveRequestDto requestDto){
        postService.save(requestDto);
        return "redirect:/";
    }

    @GetMapping("/")
    public String getPosts(Model model){
        List<Post> posts = postRepository.findAll();
        model.addAttribute("posts",posts);
        return "posts/posts";
    }
}
```
***Service***
```java
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Long save(PostSaveRequestDto requestDto){
        return postRepository.save(requestDto.toEntity()).getPostId();
    }

    public PostResponseDto findById(Long id){
        Post entity = postRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 없습니다. id="+id));
        return new PostResponseDto(entity);
    }
}
```
- getPost : id를 받아서 서비스의 findById를 통해 PostResponseDto를 반환한다.  
그러지않고 컨트롤러에서 Respository를 DI해서 직접 찾아오는 방법도 있긴한데 어느게 좋은 방법인지 잘은 모르겠다.  
굳이 Service를 활용한 이유는 좀 더 역활이 명확해지는 것 같아서 이다.  

- addPost : addForm요청을 PostRequestDto형식으로 받아서 RequestDto내의 toEntity 빌더 생성자를 사용해 Post 형식으로 저장한다.  
```java
@Getter
public class PostSaveRequestDto {
    private String author;
    private String title;
    private String content;

    @Builder
    public PostSaveRequestDto(String author,String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public Post toEntity(){
        return Post.builder()
                .author(author)
                .title(title)
                .content(content)
                .build();
    }
}
```

- getPosts : findAll로 List를 받아와서 model에 넘겼다. 타임리프에서는 다음 방법으로 리스트의 객체를 사용한다  
```html
<tr th:each="post : ${posts}">
    <td><a href="post.html" th:href="@{/posts/{postId}(postId=${post.postId})}" th:text="${post.postId}">글 id</a></td>
    <td><a href="post.html" th:href="@{/posts/{postId}(postId=${post.postId})}" th:text="${post.title}">글 제목</a></td>
    <td th:text="${post.author}" th:value="${post.author}">작성자</td>
</tr>
```
---
