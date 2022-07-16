# 프로필 페이지

## 모델 만들기
```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String caption;
    private String postImageUrl;

    @JoinColumn(name = "userId")
    @ManyToOne
    private User user;

    //좋아요

    //댓글

    private LocalDateTime createdDate;

    @PrePersist
    public void createDate(){
        this.createdDate=LocalDateTime.now();
    }
}
```
caption은 사진과 함께 개시되는 글이고  
postImageUrl은 서버 내 사진의 주소다.  
그리고 이미지를 받는 Dto를 생성한 후, imageController에 사진 업로드 요청을 추가한다
```java
@Getter
@Setter
public class ImageUploadDto {

    private MultipartFile file;
    private String caption;
}
//===============================//아래는 컨트롤러코드
@PostMapping("/image")
public String imageUpload(ImageUploadDto imageUploadDto, @AuthenticationPrincipal PrincipalDetails principalDetails){
    imageService.사진업로드(imageUploadDto,principalDetails);
    return "redirect:/user/"+principalDetails.getUser().getUserId() ;
}
```
---

## 이미지 서비스 구현
```java
@Value("${file.path}")
private String uploadFolder;

public void 사진업로드(ImageUploadDto imageUploadDto, PrincipalDetails principalDetails){
    UUID uuid = UUID.randomUUID();

    String imageFileName = uuid+"_"+imageUploadDto.getFile().getOriginalFilename();
    
    Path imageFilePath = Paths.get(uploadFolder+imageFileName);
    try {
        Files.write(imageFilePath,imageUploadDto.getFile().getBytes());
    }catch (IOException e) {
        e.printStackTrace();
    }
}
```
@Value()어노테이션을 통해 application.yml에 작성되어있는 경로를 받아올 수 있다.  
저장되는 사진 이름이 같은 경우 덮어씌워질 위험이 있으므로 uuid를 통해 고유화 시킨다.  
그리고 아래 Files.write를 통해서 실제로 원하는 경로에 파이을 저장할 수 있다.  


> **이미지를 외부 폴더에 저장하는 이유**  
이미지를 내부에 저장하게 되면 실행시 컴파일 과정을 거치게 되는데,  
이미지가 컴파일되진 않지만 전체 코드가 컴파일되고 실행되는데 걸리는 시간이  
페이지 이동시간보다 오래걸리게된다면 사용자는 이동된 페이지에서 이미지를 확인할 수 없다.  
따라서 파일을 외부에두어 참조만 할 수 있게 함으로써 시간차로 발생하는 지연을 방지할 수 있다.  

## 이미지 예외 처리  

이미지 저장시 파일 형식이 MultiPartFile이기 때문에  
String 처럼 예외처리할 수 없다.  
그래서 컨트롤단에서 이미지 파일을 확인한 후 null이라면 예외를 발생시키는 형식으로 예외처리를 했다.  
여기서 주의해야할 점은 null로 받게될 errorMap처리 역시 해야한다는 점이다.

---

## 유저와 이미지의 양방향 매핑  
Image 모델은 User에 대한 정보를 가지고있지만  
User는 없기 때문에 아래 코드를 통해 양방향 매핑을 해준다  
```java
@OneToMany(mappedBy = "user")
private List<Image> images;
```
위 mappedBy의 뜻은 아래 객체가 연관관계의 주인이 아니므로, 테이블에 칼럼을 만들지말고,  
기본값인 fatch.LAZY의 뜻은 User를 select할때 해당 user id로 등록된 이미지를 가져오지 말되 getImages함수가 호출될때 가져오라는 뜻이다.  
LAZY가 아닌 EAGER는 select시 모든 이미지를 가져오게한다.

### 이미지 출력하기  
이후 jsp 파일을 수정하면 저장된 사진을 읽어오는 것 같으나,  
파일 이름은 뜨지만 이미지가 보이지 않는다. 따라서 경로를 설정해줘야한다.  

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {//web 설정파일

    @Value("${file.path}")
    private String uploadFolder;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);

        registry
            .addResourceHandler("/upload/**") // 1
            .addResourceLocations("file:///"+uploadFolder) // 2
            .setCachePeriod(60*10*6) // 1시간
            .resourceChain(true)
            .addResolver(new PathResourceResolver());
    }
}
```
WebConfig 파일을 추가한다.  
아래 메서드의 의미는  
1. 해당 주소형식에 접근하면  
2. 와 같은 주소로 변경된다는 뜻이다.  
이후 jsp에서 이미지 src 경로에 "/upload/"를 추가해주면 정상적으로 이미지 출력이 된다.  

---

### Open in View
- 클라이언트의 요청이 있을때  
디스패쳐 -> 컨트롤러 -> 서비스 -> 리포지토리 -> 영속성컨텍스트 or -> DB의 흐름으로 요청이 전달되고,  
그 역순으로 응답이 수행된다. 이때 처음 컨트롤러에 요청이 들어오면서 세션이 시작되고, 한바퀴 돌아 서비스에 컨트롤러로 응답이 전해지면서 세션이 종료된다.  
여기서 문제는 LAZY로딩일때 이미 종료된 세션 때문에 컨트롤러에서 세션정보가 필요한 모든 데이터에 접근이 불가능 하다.  

- EAGER를 사용하면 바로 해결되긴 하지만 LAZY가 필요한 경우가 반드시 있으므로  
JPA옵션에서 Open in View를 true로 해줘야힌다.  
이렇게하면 컨트롤러를 넘어서 디스패쳐로 응답이 전송될때 세션이 종료되어 문제없이 데이터 사용이 가능해진다.  

## 프로필 페이지 디테일 수정  
> 현재 프로필 페이지는 누구의 프로필인지와 상관없이 글 등록, 구독하기가 뜨고, 게시글 갯수도 연동이 되어있지않다. 이것을 수정하자.

1. 새 글 작성/ 구독하기 나누어 뜨게하기  
세션정보에서 id를 가져온 뒤 해당 페이지 id와 비교하는 방법으로 페이지의 주인인지를 알 수 있다.  
```java
@GetMapping("/user/{pageUserId}")
public String profile(@PathVariable int pageUserId, Model model,@AuthenticationPrincipal PrincipalDetails principalDetails){
    UserProfileDto dto = userService.회원프로필(pageUserId,principalDetails.getUser().getUserId());
    model.addAttribute("dto",dto);
    return "user/profile";
}
```
프로필을 불러올때 PrincipalDetails에서 세션 정보에 있는 userId를 같이 넘겨준다.   
이때 프로필 정보를 넘기기 위한 Dto를 작성하면 더 깔끔하다.  
```java
public class UserProfileDto {

    private boolean pageOwner;
    private User user;
    private int imageCount;
}
```
UserProfileDto는 페이지 주인여부를 나타내는 pageOwner, 유저객체, 이미지 갯수 정보가 담겨있으며 추후 구독 정보역시 담겨야한다.  

```java
@Transactional(readOnly = true)
public UserProfileDto 회원프로필(int pageUserId, int principalId){
    UserProfileDto dto = new UserProfileDto();
    User userEntity=userRepository.findById((long) pageUserId).orElseThrow(()->{
        throw new CustomException("유저를 찾을 수 없습니다.");
    });
    dto.setUser(userEntity);
    dto.setPageOwner(pageUserId==principalId);
    dto.setImageCount(userEntity.getImages().size());
    return dto;
    }
```
이후 UserService의 프로필 등록 메서드가 dto를 반환하게 하고 각 값을 세팅해주면 된다.  
JSP내에 자바코드를 사용해서(image.size())쉽게 나타낼 수도 있지만 view내에서 연산해야하는 코드를 작성하는 것은 좋은 코드는 아니다.  

---
### ***무한참조***  
- Getter사용시  
@Data어노테이션은 getter,setter를 자동으로 생성해줄 뿐만 아니라,  
toString 메서드도 보기좋게 하나 생성해주는데  
만약 양방향 매핑관계에서 해당 객체를 출력하려하면 매핑된 객체간의 무한참조가 일어나 오류가 발생하게된다.  
따라서 JPA에서 객체 출력은 매우 조심해야하고, 애초에 다른 기능이 필요 없다면  
@Getter, @Setter 어노테이션만 사용하는게 좋다.

- 또한 ApiController에서 객체를 JSON으로 파싱할때 역시 Getter를 통한 무한 참조가 발생한다.  
이 경우에는 무한참조의 원인이 되는 객체에 **@JsonIgnoreProperties({"객체명"})** 이라는 어노테이션을 달아주면 된다.  
이렇게하면 해당 객체 내에 있는 '객체명'에 해당하는 객체는 더이상 불러오지않는다.  


