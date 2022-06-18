# JPA
> 자바 표준 ORM

**O**bject **R**elational **M**apping


## 1. JPA
- 관계형 데이터베이스(RDB)가 웹 서비스의 중심이 되면서 모든 코드는 SQL 중심이 되었다.  
이는 관계형 데이터베이스가 SQL만 인식할 수 있기 때문인데, 이 때문에 각 테이블마다 기본적인 CRUD SQL을 매번 생성해야한다.  

- 관계형 데이터베이스는 **어떻게 데이터를 저장할것인가**에 초점이 맞춰진 기술인 반면에  
객체지향 프로그래밍은 **기능과 속성을 한 곳에서 관리**하는 기술이다. 여기에서 **패러다임 불일치**가 일어난다.   
자바에서 부모가 되는 객체를 가져오려면 다음과 같다
```java
User user = findUser();
Group group = user.getGroup();
```
하지만 데이터베이스가 추가되면 다음과 같이 변경된다
```java
User user = userDao.findUser();
Group group = groupDao.findGroup(user.getGroupId());
```
User 따로, Group 따로 조회하게 된다.  
상속, 1:N 등 다양한 객체 모델링을 데이터베이스로는 구현할 수 없는 문제점을 해결하기 위해 등장한 것이 JPA다.  
JPA를 통해서 SQL에 종속적인 개발이 아닌 객체지향적 프로그래밍을 계속 실현할 수 있다.

- **Spring Data JPA**   
  JPA는 인터페이스이며 이를 구현한 것이 Hibernate다. 그리고 이것을 좀 더 쉽게 사용하고자 추상화시킨것이 Spring Data JPA 모듈이다.  

---

# 2. 실습
* **요구사항**
> 게시판 기능
- 게시글 조회
- 게시글 등록
- 게시글 수정
- 게시글 삭제
> 회원 기능
- 구글/네이버 로그인
- 로그인한 사용자 글 작성 권한
- 본인 작성 글에 대한 권한 관리

## 2.1 domain   
- 도메인이란  
  게시글, 댓글, 회원, 정산, 결제 등 소프트웨어에 대한 요구사항 혹은 문제영역
  ```
  dao와의 차이
  기존의 Query Mapper에서 사용하던 dao는 xml에 쿼리를 담고, 클래스는 오로지 쿼리의 결과만 담았지만  
  jpa에서는 도메인 클래스에서 모두 해결된다.
  ```

## 2.1.1 Posts
실제 DB의 테이블과 매칭될 클래스이며 보통 *Entity* 클래스 라고 한다.  
JPA를 사용할 경우 이 클래스의 수정을 통해 DB데이터 작업을 할 수 있다.
```java
@Getter
@NoArgsConstructor // 2
@Entity // 1
public class Posts {

    @Id // 2
    @GeneratedValue(strategy = GenerationType.IDENTITY) //3
    private Long id;
    
    @Column(length = 500, nullable = false) // 4
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;
    
    @Builder // 6
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
}
```
1. @Entity  
테이블과 링크될 클래스임을 나타낸다  
기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍(_)으로 테이블 이름을 매칭한다.  
ex)SalesManager.java -> sales_manager table
2. @Id  
해당 테이블의 PK 필드를 나타낸다.  
3. @GeneratedValue  
PK의 생성 규칙을 나타낸다.  
GenerationType.IDENTITY 옵션을 추가해서 auto_increment한다.  
4. @Column  
굳이 선언하지 않아도 Entity클래스의 필드는 모두 칼럼이 되지만  
기본값 이외에 추가로 변경이 필요한 옵션이 있는 경우 사용한다.  
5. @NoArgsConstructor  
기본 생성자를 추가해준다  
6. @Builder  
해당 클래스의 *빌더 패턴 클래스* 를 생성  
생성자 상단에 선언하면 생성자에 포함된 필드만 빌더에 포함한다.


+ Entity에 Setter 메소드가 없는 이유  
setter가 있으면 해당 클래스의 인스턴스 값들이 언제, 어디서 변해야하는지  
코드상으로 명확하게 구분할 수가 없어서 차후 기능 변경시 복잡해진다.  
따라서 해당 필드 값의 변경이 필요하면 명확하게 그 목적과 의도를 나타내는 메소드를 추가해야한다.
```java
public class Order{
    public void cancelOrder(){
        this.status = false;
    }
}

public void 주문서비스의_취소이벤트(){
    order.cancelOrder();
}
```

- 생성자 패턴과 빌더 패턴  
@Builder를 통해 제공되는 빌더 클래스는 생성자와 마찬가지로 생성 시점에 값을 채워주는 것은 동일하다.
```
public Example(String a, String b){
    this.a = a;
    this.b = b;
}

new Example(b,a);
```
하지만 위의 코드 처럼 a와 b의 입력 위치가 변경되어도 코드 실행 전까지는 문제를 찾기 어렵다.
빌더를 사용하면 다음과 같이 어느 필드에 어떤 값을 채워야하는지 명확하게 알 수 있다.
```
Example.builder()
    .a(a)
    .b(b)
    .build();
```

---

## 2.1.2 JpaRepository
Repository는 MyBatis에서는 Dao라고 불리는 DB 레이어 접근자다.  
단순히 인터페이스를 생성 후, JpaRepository<Entity 클래스, PK 타입>를 상속하면 기본적인 CRUD메소드가 자동으로 생성된다.
```java
package com.liean.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts,Long> {
}

```
**테스트 코드**
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostsRepositoryTest {
    
    @Autowired
    PostsRepository postsRepository;
    
    @AfterEach
    public void clean(){
        postsRepository.deleteAll();
    }
    
    @Test
    public void 게시글저장_불러오기(){
        //given
        String title = "테스트 제목";
        String content = "테스트 본문";
        
        postsRepository.save(Posts.builder() // 1
                .title(title)
                .content(content)
                .author("KDK")
                .build());
        //when
        List<Posts> postsList = postsRepository.findAll();
        
        //then
        Posts posts = postsList.get(0);
        Assertions.assertThat(posts.getTitle()).isEqualTo(title);
        Assertions.assertThat(posts.getContent()).isEqualTo(content);
    }
}
```
1. postsRepository.save()  
테이블에 insert/update 쿼리를 실행한다.  
id값이 있다면 update, 없다면 insert 쿼리가 실행된다.

---

## 2.2 등록/수정/조회 API
API를 만들기 위해서는 총 3개의 클래스가 필요하다
1. Request 데이터를 받을 Dto
2. API 요청을 받을 Controller
3. 트랜잭션, 도메인 기능 간의 순서를 보장하는 Service

여기에서 Service는 트랜잭션, 도메인 간 순서 보장의 역할만 담당한다.  
그리고 비즈니스 처리는 Domain에서 담당한다.  
```
기존에 서비스로 처리하던 방식을 트랜잭션 스크립트라고 한다.  
모든 로직이 서비스 클래스 내부에서 처리되다보니 서비스 계층이 무의미하고 
객체는 단순히 데이터 덩어리 역할만 했다.  
domain이 비즈니스 로직을 담당하게 하면 객체가 본인의 이벤트 처리를 각자 담당할 수 있게된다.
```
## 2.2.1 PostsApiController

```java
@RequiredArgsConstructor
@RestController
public class PostsApiController {
    
    private final PostsService postsService;
    
    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto){
        return postsService.save(requestDto);
    }
}
```

## 2.2.1 PostsService

```java
@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto){
        return postsRepository.save(requestDto.toEntity()).getId();
    }
}
```

## 2.2.1 PostsRequestDto

```java
@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {
    private String title;
    private String content;
    private String author;
    @Builder
    public PostsSaveRequestDto(String title, String content,String author){
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public Posts toEntity(){
        return Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
```

Entity클래스와 유사하지만 **절대로 Entity 클래스를 Request/Response 클래스로 사용해서는 안된다.**
Entity 클래스는 데이터베이스와 맞닿은 핵심 클래스이기 때문에 Entity클래스의 변경은 많은 클래스에 영향을 끼친다.

---

## 2.3 PostsApiContoller Test
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 1
class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @AfterEach
    public void tearDown() throws Exception{
        postsRepository.deleteAll();
    }

    @Test
    public void Post_등록된다() throws Exception{
        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();
        String url = "http://localhost:"+port+"/api/v1/posts";
        
        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url,requestDto,Long.class); // 2
        
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
        
    }
}
```
전에 쓰던 @WebMvcTest를 쓰지 않은 이유는 JPA 기능이 작동하지 않기 때문이다.

---
## 2.4 Update, Response
PostsApiController 추가
```java
    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto){
        return postsService.update(id,requestDto);
    }
    
    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById(@PathVariable Long id){
        return postsService.findById(id);
    }
```
PostsResponseDto
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
Entity의 필드 중 일부만 사용하기 때문에 생성자로 Entity를 받아서 필드 값을 채운다.  
굳이 모든 필드를 가진 생성자가 필요하지 않기 때문이다.

PostsUpdateDto
```java
@Getter
@NoArgsConstructor
public class PostsUpdateRequestDto {
    
    private String title;
    private String content;
    
    @Builder
    public PostsUpdateRequestDto(String title, String content){
        this.title;
        this.content;
    }
}
```
Posts에 update메서드 추가
```java
public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
```
PostsService에 update와 findById추가
```java
@Transactional
public Long update(Long id, PostsUpdateRequestDto requestDto){
    Posts posts = postsRepository.findById(id)
            .orElseThrow(()-> new IllegalArgumentException("해당 게시글이 없습니다. id= "+id));

    posts.update(requestDto.getTitle(),requestDto.getContent());
    return id;
}

public PostsResponseDto findById(Long id){
    Posts entity = postsRepository.findById(id)
            .orElseThrow(()-> new IllegalArgumentException("해당 게시글이 없습니다. id= "+id));
    return new PostsResponseDto(entity);
}
```
update 기능에서 쿼리를 날리지 않는다.  
JPA의 핵심내용인 영속성 컨텍스트 덕분이다.  
JPA의 엔티티 매니저가 활성화된 상태로 **트랜잭션 안에서 데이터베이스에서 데이터를 가져오면** 이 데이터는 영속성 컨텍스트가 유지된 상태다.  
이 상태에서 데이터 값을 변경하면 **트랜잭션이 끝나는 시점에 해당 테이블에 변경분을 반영**한다. 따라서 Entity객체의 값만 변경하면 별도로 쿼리를 날릴 필요가 없는 것이다.
이 개념을 **더티 체킹**이라고 한다.  
저자 블로그 : https://jojodu.tistory.com/415

---

## 2.4.1 Update 테스트  

```java
    @Test
    @DisplayName("수정되었다.")
    void Posts_Update() throws Exception{
        //given
        Posts savedPosts = postsRepository.save(Posts.builder()
                        .title("title")
                        .content("content")
                        .author("author")
                        .build());

        Long updateId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/"+updateId;

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,requestEntity,Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
    }
```

---
### 2.5 JPA Auditing으로 생성시간/수정시간 자동화  
도메인에 아래 클래스를 추가
```java
@Getter
@MappedSuperclass // 1
@EntityListeners(AuditingEntityListener.class) // 2
public abstract class BaseTimeEntity {
    
    @CreatedDate // 3
    private LocalDateTime createDate;
    
    @LastModifiedDate // 4
    private LocalDateTime modifiedDate;
}
```
1. JPA Entity 클래스들이 BaseTimeEntity를 상속할 경우 필드들도 칼럼으로 인식하도록 한다.
2. Auditing 기능(시간을 자동으로 넣어주는 기능)을 포함시킨다.
3. Entity가 생성되어 저장될 때 시간이 자동 저장된다.
4. 조회한 Entity의 값이 변경될 때 시간이 자동 저장된다.

이후 Posts가 위 클래스를 상속받도록 변경하고,  
Application 클래스에 @EnableJpaAuditing 어노테이션을 추가하면 된다.

### 테스트코드

```java
    @Test
    @DisplayName("시간 자동 저장")
    void BaseTimeEntity() {
        //given
        LocalDateTime now = LocalDateTime.of(2022,6,13,0,0,0);
        postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("KDK")
                .build());
        //when
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts posts = postsList.get(0);

        System.out.println(">>>>>>>>> createDate="+posts.getCreateDate()+", modifiedDate="+posts.getModifiedDate());

        assertThat(posts.getCreateDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);
    }
```

---
## 기타

- MyBatis는 ORM이 아닌 SQL Mapper에 해당한다  
SQL Mapper : 객체와 관계형 데이터베이스의 데이터를, 개발자가 작성한 SQL로 매핑시켜주는 프레임워크

- **DAO** : Data Access Object    
    데이터베이스의 데이터에 접근하기 위한 객체  
  **DTO** : Data Transfer Object  
    계층간 데이터 교환을 위해 사용하는 객체

- **Entity**  
  저장되고, 관리되어야하는 데이터의 집합.  
  id와 같은 유일한 식별자를 가지고 있어야한다.
- 빌더 패턴 : 디자인 패턴 중 하나  
  필수값에 대해서는 생성자로 받고, Optional한 값은 메소드를 통해 선택적으로 입력받는 패턴  
  https://readystory.tistory.com/121
- ### **Spring Web 계층**  
> - **Web Layer**   
Controller와 JSP/Freemaker 등의 뷰 탬플릿 영역이다.  
외부 요청과 응답에 대한 전반적인 영역을 이야기한다.
> - **Service Layer**  
일반적으로 Controller와 Dao의 중간 영역에서 사용된다.  
@Transacional이 사용되어야하는 영역이기도 하다.
> - **Repository Layer**  
Database와 같이 데이터 저장소에 접근하는 영역이다.
> - **Dtos**  
Dto는 계층간에 데이터 교환을 위한 객체를 이야기하며 Dtos는 이들의 영역을 이야기한다.
> - **Domain Model**  
도메인이라고 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화시킨 것을 도메인 모델이라고 한다.  
다만, 무조건 데이터베이스의 테이블과 관계가 있어야만 하는 것은 아니다.

