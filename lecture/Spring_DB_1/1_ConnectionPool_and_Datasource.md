# 커넥션 풀과 데이터소스의 이해

## 커넥션 풀
데이터 연결마다 커넥션을 생성하지 않고 미리 생성된 커넥션을 저장해두고 사용하는 방법
>커넥션 생성과정  
1. 애플리케이션 로직이 DB 드라이버를 통해 커넥션을 조회 
2. DB드라이버가 DB와 TCP/IP 커넥션을 연결
3. TCP/IP 커넥션이 연결되면 DB드라이버는 ID, PW등 정보를 DB 전달
4. DB는 정보를 통해 내부 인증을 완료하고, 내부 세션 생성
5. DB가 커넥션 생성 완료를 알림
6. DB 드라이버가 커넥션 객체를 생성해서 클라이언트에 반환

따라서 애플리케이션 로직은 DB 드라이버가 아닌 커넥션 풀에 이미 생성되어있는 커넥션을 요청하면 된다.  
반환시에는 커넥션을 종료하지 않고 살아있는 채 반환해야한다.

## 데이터 소스  
> DB 드라이버로 커넥션을 조회하다 커넥션 풀로 변경할때  
커넥션을 획득하는 곳이 다르기때문에 획득하는 애플리케이션 코드도 함께 변경해야한다 

DataSource는 **커넥션을 획득하는 방법을 추상화** 하는 인터페이스이다.  
DriverManager는 DataSource 인터페이스를 사용하지 않기 때문에  
DriverManagerDataSource를 사용해야한다.  

장점  
1. 설정과 사용의 분리