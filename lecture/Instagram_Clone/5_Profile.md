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
