# MyBatis

### 소개
MyBatis는 xml파일을 사용해서 설정한다.  
sql문을 직접 작성하지않기 때문에 오타로인한 오류방지가 가능하다.  
또한 동적쿼리역시 JDBC Template에 비하면 설정이 쉬운 편이다.  

### 설정
```properties
#MyBatis

#마이바티스에서 반드시 적어야하는 타입 앞의 패키지를 생략할 수 있다.
mybatis.type-aliases-package=hello.itemservice.domain
#언더바 에서 카멜케이스 문법으로 자동 변경해준다
mybatis.configuration.map-underscore-to-camel-case=true
```