# SanCheck
>반려견 산책 모임 서비스
- https://sancheck.vercel.app/
- https://github.com/project-sancheck/sancheck

</br>

## 1. 제작 기간 & 참여 인원
- 2022년 9월 7일 ~ 10월 12일
- 팀 프로젝트(4인)

</br>

## 2. 사용 기술
#### `Back-end`
  - Java 17
  - Spring Boot 5.3
  - Gradle
  - Spring Data JPA
  - QueryDSL
  - H2
  - MySQL 8.0.3
  - Spring Security

</br>

## 3. ERD 설계
[ERD 링크](https://www.erdcloud.com/p/vN9YZs4STCaQ8nkqK)

</br>

## 4. 핵심 기능
이 서비스의 핵심 기능은 모임 생성과 참여입니다.  
서비스에 가입한 사용자는 자신의 반려견정보를 등록한 뒤, 모임을 만들거나 이미 만들어진 모임에 반려견과 함께 참여할 수 있습니다.  

</br>

<details>
<summary><b>핵심 기능 설명 펼치기</b></summary>
<div markdown="1">

### 4.0. 주요 기능 정리

### 4.1. Community
- **요일데이터 컨버터** : [코드 확인](https://github.com/project-sancheck/sancheck/blob/main/server/src/main/java/com/main026/walking/util/converter/StringArrayConverter.java). 
  - 클라이언트에서 List형태로 넘어온 요일정보를 설정한 컨버터를 통해 문자열의 형태로 저장합니다.
  - 코드작성에서는 다시 List로 컨버팅된 콜렉션을 사용하게 됩니다.  

- **모임 참여**  : [코드 확인](https://github.com/project-sancheck/sancheck/blob/2811a38a348b5ce6f5c1a76ea542bbd56084c1d2/server/src/main/java/com/main026/walking/community/service/CommunityService.java#L108)
  - 참여를 원하는 반려동물의 id값을 받아서 CommunityPet이라는 중간 테이블을 활용해서 모임에 가입시킵니다.
  - 참여인원과 수용인원을 비교하여 수용인원을 넘은 요청이 들어온 경우에는 예외를 발생시킵니다.
  - 이미 참여신청이 된 반려동물을 다시 참여요청을 하는지 중복 여부를 검사합니다.

### 4.2. Pet

- **나이 계산** : [코드 확인](https://github.com/project-sancheck/sancheck/blob/2811a38a348b5ce6f5c1a76ea542bbd56084c1d2/server/src/main/java/com/main026/walking/pet/service/PetService.java#L89)
  - 반려동물의 생년월을 저장하여, 정보 조회시에는 '~살 ~개월'의 형태로 출력되도록 알고리즘을 작성했습니다.

- **기본 이미지 등록**  : [코드 확인](https://github.com/project-sancheck/sancheck/blob/2811a38a348b5ce6f5c1a76ea542bbd56084c1d2/server/src/main/java/com/main026/walking/pet/service/PetService.java#L34)
  - 반려동물의 이미지를 선택하지 않고 등록한 경우, 서버에 기본으로 저장된 강아지의 프로필 이미지를 등록합니다.  

### 4.3. Member

- **유효성 검사**  : [코드 확인](https://github.com/project-sancheck/sancheck/blob/main/server/src/main/java/com/main026/walking/util/annotation/Password.java)
  - '영문과 숫자를 포함한 8에서 16글자의 문자'라는 조건을 충족하는 비밀번호만 입력되는 어노테이션을 생성했습니다.

- **JWT 인증** : [코드 확인](https://github.com/project-sancheck/sancheck/blob/main/server/src/main/java/com/main026/walking/auth/filter/JwtAuthenticationFilter.java)
  - 로그인 성공시 생성되는 인증토큰,리프레시 토큰을 사용해서 인증을 수행합니다.

### 4.4. Mail
- **비밀번호 찾기** : [코드 확인](https://github.com/project-sancheck/sancheck/tree/main/server/src/main/java/com/main026/walking/email)
  - 가입시 입력한 이메일을 입력하면, 해당 이메일로 임시 비밀번호를 발급하는 기능입니다.

</div>
</details>

</br>

## 5. 핵심 트러블 슈팅
### 5.1. 인증,인가 단계의 예외
- 토큰을 사용한 인증,인가를 구현하면서 해당 과정 중에 발생한 예외를 출력할 필요가 있었습니다.  
다른 예외처리 로직처럼 [Enum예외코드](server/src/main/java/com/main026/walking/exception/ExceptionCode.java)를 사용해서 토큰과 관련된 오류를 지정했는데 콘솔에서는 메세지가 출력되었지만 실제 응답은 설정하지 않은 500이나 403오류가 발생했습니다.  

- 검색 결과 인증,인가 단계는 DispatcherServlet보다도 바깥에서 실행되기 때문에 서비스 단에서 사용하는 방식으로는 예외처리가 불가능한것이었습니다.  

- 따라서 인증,인가 단계에서 예외를 제대로 출력하기 위해서는 해당 단계 이전에 예외를 발생시킬 수 있는 또 다른 필터를 추가해야 했습니다.  

<details>
<summary><b>추가한 코드</b></summary>
<div markdown="1">

발생한 예외를 감지해서 출력하는 필터

~~~java
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request,response);
        }catch (BusinessLogicException ex){
            setErrorResponse(request,response,ex);
        }
    }

    public void setErrorResponse(HttpServletRequest request, HttpServletResponse response,BusinessLogicException ex) throws IOException{
        response.setStatus(ex.getExceptionCode().getStatus());
        response.setContentType("application/json; charset=UTF-8");

        response.getWriter().write(new ErrorResponse(ex.getExceptionCode()).convertToJson());
    }
}
~~~

예외 정보를 직접 JSON 형식으로 파싱해서 응답하는 예외 정보

~~~java
@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String error;

    public ErrorResponse(ExceptionCode exceptionCode) {
        this.status = exceptionCode.getStatus();
        this.error = exceptionCode.getError();
    }
    public String convertToJson() throws JsonProcessingException {
        //Todo
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
}
~~~

</div>
</details>

</br>

### 5.2. 이미지 업로드
- 회원,반려동물,모임 모두 이미지를 등록할 수 있었기에 요청데이터에 각종 정보와 함께 MultiPartFile형태인 이미지 역시 함께 업로드 하도록 구현했습니다.  

- 업로드의 경우, 서버에서는 단순한 문제였지만 클라이언트에서는 복잡하고도 까다로운 문제가 발생하며, 이미지를 관리하는 것도 쉽지 않다는 것을 알게되었습니다.  

- 그래서 이미지를 업로드에 대한 경로를 매핑해서 이미지 등록요청이 들어오면 바로 이미지를 저장한 뒤 경로를 문자열로 반환하도록 변경했습니다.  

<details>
<summary><b>기존 코드</b></summary>
<div markdown="1">

요청에서 이미지 파일을 가져오던 기존 방식

~~~java
public Community createCommunity(CommunityDto.Post postDto) throws IOException {
    Community community = communityMapper.postDtoToEntity(postDto);

    String[] dayInfo = postDto.getDayInfo();

    List<String> dayList = new ArrayList<>();
    for (String day : dayInfo) {
      dayList.add(day);
    }

    community.setDays(dayList);

    community.setRepresentMember(testMember());
    community.setAddress(postDto.getSi(), postDto.getGu(), postDto.getDong());

    //이미지 세팅
    Community savedCommunity = communityRepository.save(community);
    List<MultipartFile> attachFiles = postDto.getImages();
    for (MultipartFile attachFile : attachFiles) {
      String storeFile = fileStore.storeFile(attachFile);
      Image image = Image.builder()
              .storeFilename(storeFile)
              .community(savedCommunity)
              .build();
      imageRepository.save(image);
    }
    return communityRepository.save(community);
  }
~~~
</div>
</details>

</br>

<details>
<summary><b>개선 코드</b></summary>
<div markdown="1">

이미지를 등록하는 경로를 따로 추가해서 이미지에 경로를 반환

~~~java
@PostMapping("/post/image")
public List<String> postImages(@RequestPart List<MultipartFile> imgFile){
    return imgFile.stream().map(awsS3Service::uploadImage).collect(Collectors.toList());
}
~~~

이미지를 직접 받던 기존 코드와 달리 이미지 경로라는 문자열을 가진 dto

~~~java
public CommunityDto.Response createCommunity(CommunityDto.Post postDto,PrincipalDetails principalDetails) {
    
    /*코드 생략*/
    
    Community savedCommunity = communityRepository.save(community);
    List<String> imagePaths = postDto.getImgUrls( 
    for (String imagePath : imagePaths) {
        Image image = Image.builder()
                .storeFilename(imagePath)
                .community(savedCommunity)
                .build();
        imageRepository.save(image);
    }

    /*코드 생략*/
    }
~~~
</div>
</details>

</br>

## 6. 그 외 트러블 슈팅
<details>
<summary>View 관점의 설계</summary>
<div markdown="1">

- 사용자의 관점에서 봤을때 사용자 본인이, 강아지를 데리고 모임에 참여하는 것으로 생각하게 됩니다.  
실제로는 강아지를 중심으로 참여하지만 회원에 대한 정보도 가지고있어야할 것 같다는 생각에 Community와 Member간의 연관관계를 추가했습니다.  

- 그러나 이미 반려동물과 회원간 연관관계매핑으로 강아지를 통해서 회원 정보도 가져올 수 있기 때문에  
단순히 회원 정보를 가져오기 위해서는 불필요한 연관관계라는 것을 알게되어 삭제했습니다.

- 또 보여지고, 사용되는 측면에서만 생각하다보니 Community 엔티티에 '참여인원'이라는 데이터가 들어가게 되었습니다.  
하지만 이는 중간 테이블을 카운트 하는 방식으로 응답할 수 있었습니다. 이를 깨닫고 단순 응답에만 필요한 데이터들은 엔티티에 저장하지 않아도 되도록 수정했습니다.  

</div>
</details>

</br>

<details>
<summary>""와 Null</summary>
<div markdown="1">

- 모임 생성시 요일,혹은 날짜를 받게 되는데 날짜를 받을때는 요일 List안에 아무것도 없어야 함에도 빈 문자열이 추가되어 조회시 오류가 났습니다.  

- 찾아보니 컨버팅 과정에서 요일 값이 없는 경우 null 혹은 비어있는 list가 추가되어야하는데 ""라는 문자열을 추가하는 코드가 있었습니다.  

~~~java
@Converter
public class StringArrayConverter implements AttributeConverter<List<String>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if(attribute==null){
            //return "";
            return null;
        }

        return attribute.stream().map(String::valueOf).collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if(dbData==null){
            //return new ArrayList<>();
            return null;
        }
        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .collect(Collectors.toList());

    }
}
~~~

</div>
</details>

</br>

<details>
<summary>메일 전송 불가</summary>
<div markdown="1">

- 메일 전송 코드를 작성하고 테스트 역시 성공했으나 배포서버에서는 메일 전송이 되지 않았습니다.
- 이유는 배포 서버에서 메일 포트를 열어주지 않았기 때문이었습니다. 구글 메일 서버 포트인 587을 열어주니 정상적으로 메일을 전송할 수 있었습니다.

</div>
</details>

</br>

## 6. 회고 / 느낀점
>프로젝트 개발 회고 : https://github.com/liean17/TIL/blob/main/codestates/Main_Project/Retrospect/Retrospect.md