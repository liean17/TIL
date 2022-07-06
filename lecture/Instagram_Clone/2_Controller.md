# Controller

## FrontController and Dispatcher

웹어플리케이션을 만든다고 할 때  
로그인, 회원가입, 게시글 쓰기 등 각 기능에 따라 각각의 로직이 담긴 자바 파일을 불러와야하는데  
이러한 요청들을 받아서 각 기능에 따라 처리할 수 있도록 분류하는 것이 FrontController다  

그러나 요청이 많아질수록 하나의 컨트롤러에서 처리하는 것이 부담이 되므로  
도메인 별로 구분하는 것이 기본이다.  

그리고 각 요청을 어떤 도메인에 보내야할지 분기를 담당하는 것이 Dispatcher다.

---

## HTTP 요청 방식

1. GET - 데이터 요청
2. POST - 데이터 전송(http Body 필요)
3. PUT - 데이터 갱신(http Body 필요)
4. DELETE - 데이터 삭제

## http 쿼리스트링, 주소 변수 매핑

쿼리스트링 : naver.com/search?q=바보  
주소변수매핑(권장) : naver.com/search/바보

## Content_Type

컨텐츠 타입의 예
1. x-www-form-urlencoded  
: Key = Value , 단순히 매개변수로 전달
2. plain/text  
: text, @ResponseBody + 매개변수로 전달
3. application/json  
: {"username" : "Gil Dong"}, 객체로 전달  
원리 - MessageConverter가 자동으로 객체를 Json으로 변경해준다.

## Redirection  

존재하는 다른 주소로 다시 요청하는 것  
예약어 : "redirect:"  
파일을 응답하는 @Controller 만 사용 가능하다.

---

@RestController : 데이터를 응답하는 컨트롤러(ex - JSON)  
@Controller : 파일을 응답하는 컨트롤러(ex - .html)