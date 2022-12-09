# 기술 중심 메인 프로젝트 회고  

- [패키지 별 회고](#패키지_별_회고)
  * [Community - 모임 도메인](#Community_-_모임_도메인)
    + [- 요일데이터 컨버터](#-_요일데이터_컨버터)
    + [- N:M 매핑](#-_NM_매핑)
    + [- 임베디드](#-_임베디드)
  * [Member - 회원 도메인](#Member_-_회원_도메인)
    + [유효성검사](#유효성검사)
  * [Pet - 반려동물 도메인](https://github.com/liean17/TIL/blob/main/codestates/Main_Project/Retrospect/Retrospect.md#pet---%EB%B0%98%EB%A0%A4%EB%8F%99%EB%AC%BC-%EB%8F%84%EB%A9%94%EC%9D%B8)
    + [나이 계산](#나이_계산)
    + [데이터베이스를 고려한 설계](#데이터베이스를_고려한_설계)
  * [Comment - 댓글 도메인](#Comment_-_댓글_도메인)
  * [Notice - 공지 도메인](#Notice_-_공지_도메인)
    + [불필요한 엔티티](#불필요한_엔티티)
    + [이미지 갯수 제한하기 vs 제한하지 않기](#이미지_갯수_제한하기_vs_제한하지_않기)
    + [OAuth2](#oauth2)
  * [Exception - 예외](#exception_-_예외)
    + [인증,인가 단계의 예외](#인증,인가_단계의_예외)
    + [트러블 슈팅](#트러블슈팅)
    + [아쉬웠던 점](#아쉬웠던_점)
    + [잘했다고 느끼는 점](#잘했다고_느끼는_점)
    + [정리](#정리)
    + [앞으로의 계획](#앞으로의_계획)


## 패키지 별 회고
---
> ## Community - 모임 도메인  
- 연관관계나 기능이나 가장 복잡했던, 프로젝트의 중심이 되는 도메인
### - 요일데이터 컨버터  
: 우리 서비스에서 날짜 정보는 두가지 형태로 받게된다.  
첫번째는 구체적인 날짜를 지정하는 방법이고, 두번째는 요일을 지정해서 정기적인 모임을 만드는 방법이다.  
첫번째의 경우에는 날짜를 그대로 저장해서 출력하면 되지만 요일의 경우에는 입출력 형식을 고민해야했다.  
    
~~~java
//날짜로 받는 경우
private String date;
//요일로 받는 경우
private String[] dates = null;
~~~

우선 입력은 요일을 나타내는 알파벳의 앞 3개를 저장하는 List형태(MON,WED,SUN)로 받도록했다.  
일단 여기서 문제가 있는데 RDB에서는 Column에 컬렉션타입을 그대로 저장할 수 없다.  
따라서 어떻게 가공을 하던 테이블을 따로 만들던 해야했는데 단순히 날짜만 저장하는 형태라면 컨버터를 사용하는게 좋을 것 같다는 생각이 들었다.  
    
```java
    @Converter
    public class StringArrayConverter implements AttributeConverter<List<String>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if(attribute==null){
            return null;
        }

        return attribute.stream().map(String::valueOf).collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if(dbData==null){
            return null;
        }
        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .collect(Collectors.toList());
        }
    }
```

```java
//엔티티 코드
@Convert(converter = StringArrayConverter.class)
private List<String> dates;
```

DB에 저장될때는 " , "로 연결된 하나의 문자열로 변환된다.  
그리고 엔티티에서 데이터를 사용할때는 " , "를 기준으로 분리된 list가 출력된다.  
이렇게 분리된 list형태의 데이터를 사용해서 enum타입을 이용해 한글로 변환하여 응답데이터에 출력했다.  

> 회고를 하면서 든 생각  

- 요일을 처음부터 한글로(월요일,화요일)로 받는것은 숫자, 알파벳으로 받는 것에 비해 어떤 트레이드 오프가 있을까?  
    숫자로 받는 것에 비하면 많은 바이트를 차지하긴 하지만 그 차이가 유의미한 것일지 모르겠다. 복잡하지않다면 데이터를 적게 받는게 더 좋긴하겠지만
- 요일,날짜를 나눠서 받기보다 임베디드로 합쳐서 관리했다면 더 보기좋고 사용하기도 편하지않을까
- 요일을 그냥 저장하는것이 아니라 스케쥴러를 사용하면 더 확장성있게 할 수 있다고 한다. 적용시켜봐야겠다.

### - N:M 매핑  
: Community와 모임 가입 주체인 Pet은 다대다 연관관계다.  
모임에 가입한 반려동물의 수와 같은 데이터를 활용하기 위해서 CommunityPet이라는 중간 테이블을 생성했다.  
모임에는 최대 참여인원을 지정할 수 있는데 CommunityPet를 카운트해서 참여요청을 받지 못하도록 설정했다.  

### - 임베디드
: 모임의 주소를 저장해야하는데 서비스에서 시,구,동으로 조회,검색이 가능해야했기 때문에 시,구,동 데이터를 따로 입력받았다.  
그런데 그대로 엔티티에 저장하려하니 데이터를 다루는 것이 너무 복잡했다. 결국 셋 다 같은 주소데이터인데 각각 조회해야했기 때문이다.  
이는 임베디드타입을 사용해서 따로 DB에는 따로 저장되지만 객체로는 한번에 조회할 수 있도록 했다.  
```java  
@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class Address {
    private String si;
    private String gu;
    private String dong;

    public Address(String si, String gu, String dong) {
        this.si = si;
        this.gu = gu;
        this.dong = dong;
    }
}
```
> 회고를 하면서 든 생각   

- 시,구,동을 발음 그대로 [si,gu,dong]으로 받았다.  
    city, district 이런 식으로 영어를 사용하려했는데 우리나라의 시,구,동과 완벽하게 맞는 단어는 아니라는 생각이 들어서였다.  
    일반적으로는 영단어로 쓰는 것이 맞아보인다. 그런데 주소 이외에도 한글이 훨씬 와닿는 단어가 있다면 어떤식으로 변수명을 정해야할지 모르겠다.
- 흔한일은 아니지만 주소가 변경하는 일이 한번씩 있다고한다. 지금은 주소를 한글 그대로 받는데 API를 사용해서 주소를 저장하는 경우, PK값을 활용하면 불변하는 위치 데이터를 얻을 수 있다고한다.  

---

---
> ## Member - 회원 도메인
---
### 유효성검사
: 가입을 위한 입력을 받을때 Validation 어노테이션을 사용해서 유효성 검사를 했다.  
비밀번호의 경우 '8에서 16글자의 영문,숫자혼합'이라는 조건이어서 정규표현식을 사용했다.  
```^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z]{8,16}```  
그렇게 복잡하진않지만 한눈에 보기 쉽지도 않다고 생각해서 비밀번호 검사용 어노테이션을 따로 만들기로 했다.  
유효성 검사 어노테이션을 만들기 위해서는 두개의 클래스가 필요했다.  
첫번째는 틀에 해당하는 어노테이션 인터페이스고, 두번째는 실질적으로 유효성검사를 실행하는 클래스였다.  
```java
@Constraint(validatedBy = {PasswordValidator.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface Password {

    String count = "[8-16]";

    String message() default "영문과 숫자의 조합 혹은 갯수가 " + count + "이 아닙니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
```
```java
public class PasswordValidator implements ConstraintValidator<Password,String> {

    @Override
    public void initialize(Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        String pattern = "^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z]{8,16}";
        boolean matches = Pattern.matches(pattern,value);
        if(!matches){
            return false;
        }
        return true;
    }
}
```

isValid()메서드가 실질적인 유효성 검사를 담당한다.  

> 회고를 하면서 든 생각  
- 유효성 검사 어노테이션을 사용하면 정말 간단하다. 하지만 최근 책을 읽으니 매우 당연하게도 직접 유효성검사 메서드를 만드는 것을 보았다.  
어노테이션을 사용하면 확실히 편리하긴 하지만 특정 기능에 종속된다는 단점이 존재한다. 자바,스프링으로만 평생 개발할 것이 아니라면 조금 불편하더라도 확장성을 고려하는것도 좋을 것 같다.  


---
>## Pet - 반려동물 도메인
---
### 나이 계산  
: 보통 강아지의 나이는 '~살 ~개월'로 나타낸다. 하지만 나이를 직접 입력 받으면 곤란하다  
나이는 시간이 지날수록 변하는데 데이터를 그대로 저장해버리면 시간이 지나도 나이는 그대로라 사용자가 직접 수정해줘야하기 때문이다.  
그래서 강아지의 생년월 을 받아서 저장하고, 조회할때 계산하는 알고리즘을 추가했다.  
```java
private void parseAge(String birthDay, Pet pet) {
    LocalDate parsedDate = LocalDate.parse(birthDay);
    LocalDateTime now = LocalDateTime.now();
    Integer year,month;
    if(now.getMonthValue() - parsedDate.getMonthValue()<0){
        year = now.getYear()-parsedDate.getYear()-1;
        month = now.getMonthValue() + 12 - parsedDate.getMonthValue();
    }else{
        year = now.getYear()-parsedDate.getYear();
        month = now.getMonthValue() - parsedDate.getMonthValue();
    }
    PetAge petAge = new PetAge(year,month,birthDay);
    pet.setPetAges(petAge);
}
```
개선의 여지가 있는 알고리즘이지만 작동은 제대로 했다.  
엔티티에서 응답Dto로 매핑될때 위 메서드를 사용해서 '~살 ~개월'로 표현될 수 있도록 했다.  

### 데이터베이스를 고려한 설계  
: ERD설계단계에서 멘토분에게 가장 많은 질문을 받은 엔티티였다.  
처음 설계에서는 사용자입장에서 생각한 결과 반려동물의 정보가 모임에 저장되지만 반려동물 주인의 정보도 함께 저장되어야한다고 생각했다.  
그래서 모임에 반려동물(Pet)과 그 주인(Member)까지 연관관계 매핑을 했는데 멘토분에게 '반려동물을 통해서 회원 정보를 알 수 있는데 굳이 회원 정보를 알아야하는가'라는 질문을 받았다.  
거기에 대해 명확한 대답을 할 수 없었다. 그냥 필요해보여서 매핑한것이었기 때문이다.  
코드만 보면 모임에서 참여한 회원도 한번에 조회할 수 있으니 편하지만 데이터베이스 입장에서는 같은 정보를 두개나 가지고있게 된다.  
데이터베이스를 전혀 고려하지 않은 결과였다.  
그리고 애초에 Pet이 엔티티가 될 필요가 있는지에 대한 질문도 받았다.  
Pet은 Member 안에서만 존재하며 Member가 없이 독립적으로 존재할 수도, 그럴 필요도 없다.  
여기에 대한 대답도 바로 할 수 없었다. 엔티티가 되어야하는 이유에 대해서도 깊이 생각해보지 않았기 때문이다.  
그 뒤로 곰곰히 생각해보고 엔티티가 되어야하는 조건에 대해서도 검색한 결과 Pet은 엔티티가 되어야한다는 결론을 내렸다.  
우선 모임 가입의 주체가 Pet 이기도하며 이렇게되면 Pet은 모임과 회원 두가지에 대해 관계를 가지기 때문이다.  
또 강아지는 유일하기때문에 유일한 식별자로 구분이 가능해야한다.  

> 회고를 하면서 든 생각
- View의 관점으로만 생각하고있지 않은지, 불필요한 쿼리가 생성되는것은 아닌지 등  
클래스 하나에도 필요한 것인지에 대한 충분한 고민이 필요하다.

---
> ## Comment - 댓글 도메인
---
- 공지와 함께 가장 단순한 도메인이다.  
CommunityId를 통해 등록되고, 이후 생성된 CommentId로 수정,삭제할 수 있다.  

---
> ## Notice - 공지 도메인
---
- 댓글보다도 단순한 도메인이지만 애초에 엔티티일 필요가 없었다.

### 불필요한 엔티티  
: 내가 생각한 공지는 모임장의 게시판의 역할이었다.  
성격이 다른 한개 이상의 공지를 등록할 수 있는 모임장 전용 게시판으로 생각했는데  
프론트에서는 단순 공지의 역할로만 생각하고 하나의 공지를 내용만 공유하도록 만들었다.  
따라서 식별자로 구분될 필요가 없이, 객체도 어찌보면 필요없이 문자열만 저장되면 되는 문제였다.  
매일 정기회의를 가지면서 충분히 소통을 했다고 생각했는데 뜻밖의 부분에서 어이없는 실수가 발생했다. 

> 회고를 하면서 든 생각
- 한편으로는 공지가 단순한 문자열로 취급되어서 쉽게 삭제되어도 되는지에 대한 고민도 든다.  
이 부분에대해서는 설계단계에서 충분히 대화를 나누어야했다. 서로 생각하는 그림에 대해서 충분한 공유가 필요하다고 느꼈다.

---
>Image - 이미지 도메인
---
- 모임에서 여러개의 이미지를 가질 수 있게하려고 만든 엔티티
### 이미지 갯수 제한하기 vs 제한하지 않기  
: 이미지는 경로정보를 문자열로 가진다. 이미지 갯수가 지정되어있다면 괜찮지만  
엔티티는 컬렉션을 저장할 수 없기에 가변적으로 이미지를 받기 어렵다.  
이것은 연관관계를 통해 엔티티화 하는 것으로 해결했다.  

> 회고를 하면서 든 생각
- 여러개의 이미지를 가지는 모임은 엔티티를 사용했지만, Pet과 Member는 프로필 사진 단 한개를 가져서 간단하게 문자열로 파일명만 저장하게 했다.  
그래서 이미지 수정과 삭제가 간단하게 이루어진다. 그런데 이미지를 그렇게 단순하게 저장해도 되는지에 대한 의문도 생긴다.  

---
>Auth - 인증,인가
---
- 당시 공부가 부족해서 가장 시간을 많이 쓴 부분이었다.  
공부를 해가면서 하나하나 구현해서 그런지 오히려 오류는 가장 없었다.  

### OAuth2  
: 좋은 자료가 많아서 어렵지않게 구글 OAuth2를 구현하고 카카오 로그인을 구현하려했는데  
프론트에서 이를 활용하는것이 곤란하다고 했다. 프론트에대한 이해가 없어서 몰랐는데 요청이 있어야 응답을 할 수 있는데  
내가 구현한 것은 링크만 접속하면 리다이렉트를 통해서 토큰을 가져오는 방식이어서 제대로 토큰을 받아올 수 없다는 것이었다.  
단순히 '이렇게 하면 되더라'라고만 생각해서 발생한 문제였다.  
OAuth2에 대한 조금 더 깊은 이해가 필요했다.  

---
> ## Exception - 예외
---
### 인증,인가 단계의 예외  
: 스프링 시큐리티와 토큰을 적용하면서 토큰과 관련된 예외를 출력할 필요가 있었다.  
다른 서비스로직 처럼 예외를 생성해서 예외가 발생하도록 설정했는데 문제가 발생했는데도 전혀 예외를 확인할 수 없었다.
정확하게는 콘솔에서는 예외메세지가 제대로 출력되지만 응답은 500이나 403등 다른 오류가 났다.
알고보니 인증,인가는 기존의 예외 출력 로직이 감지할 수 없는 바깥에 존재하기때문이었다.  
방법을 알아본 결과 예외를 감지해서 출력하는 필터를 직접 구현해야했다.  
```java
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
```
처음 스프링을 배울때, 코드 실행 프로세스를 배웠었는데 그렇구나 하고 넘겼었다.  
과정은 중요하다 생각했지만 필요성에 대해 느끼진 못했는데 제대로 와닿았던 경험이었다.  

---
>Email - 이메일
---
### 트러블 슈팅  
잘 짜여진 라이브러리라서 로컬서버에서는 오류가 발생하지 않았었는데 배포 서버에서 두가지 오류를 만났다.  
1. 계정에 로그인 불가 : 구글 메일서버를 사용했는데 계정에 POP, IMAP 설정을 하지 않아서 계정에 접근할 수 없었다. 설정을 허용함으로 변경하니 계정에 접근할 수 있었다.  
2. 메일 전송 불가 : bastion서버에서 메일 전송에 사용되는 포트를 열어주지 않은 탓에 발생한 문제였다.  

---
>Util - 기타
---

### 아쉬웠던 점
- 새로운 것을 거의 해보지 못한 점

- 기본 지식의 부족

- 역할 전가

### 잘했다고 느끼는 점
- 빠른 분업
- 트러블 슈팅 기록
- 매일 회의

---

### 정리

1. 테이블 설계를 많은 고민을 거쳐 해야한다.  
엔티티여야 하는 것, 연관관계 등을 다양하게 고민하고 설계해야 나중에 복잡한 일이 적다.  
2. aws 배포를 빠르게 해야한다. 
3. 로그 습관화
4. 유닛 테스트를 바탕으로 코드를 짜야한다.  
어느순간 오류가나면 나는 예외메세지를 보고 퀴즈를 풀어야했다  
어디에서 문제가 발생했는지, 왜 이렇게 된건지, 변수를 다르게 주면 어떤 결과가 나오는지.  
하지만 테스트를 바탕으로 했었다면 쉽게 원인을 찾을 수 있었고 해결도 빨랐을 것이다.  
5. JPA, 트랜잭션의 사용법 뿐만 아니라 개념에 대한 이해가 필요하다는 것을 느꼈다.  
내 생각에는 될것같은데 오류가 난것의 대부분은 기초개념이 없어서였다.  
6. aws 얼른 떼야된다  
적어도 흐름+개념은 머릿속에서 그릴 수 있어야한다.  


---

### 앞으로의 계획

- JPA와 트랜잭션  
이번 프로젝트를 하면서 가장 부족함을 느꼈던 부분이었다.  
코틀린으로 넘어간들, 아예 다른 코드를 배우던  
이 부분은 반드시 제대로 학습하고 넘어가야한다.

- AWS  
이번 프로젝트에서 직접적으로 마주할 일이 별로 없었다.  
스스로 배포 전 과정을 머릿속으로 그릴 수 있을만큼 숙련되어야한다.

- 알고리즘  
더이상 미룰 수도 없다. 백준 실버에서 골드, 프로그래머스 2는 시간만 들이면 풀 수 있는 수준이되어야한다.

- CS  
프로젝트를 하면서 부족함을 느꼈던 두번째  
사실상 모든것의 기초이기에 가장 먼저 학습되어야하는 부분이라 생각한다.  
알고리즘과 함께 틈틈히 매일 공부해야한다.

- 코틀린  
취업에 유리하기도하고 개인적으로 흥미도있다.  
자바로 구현한 간단한 게시판을 코틀린으로 변경하면서 공부하면 어떨까 싶다.  
