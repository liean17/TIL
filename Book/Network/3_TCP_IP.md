# TCP/IP

## TCP/IP의 4계층

### 1. 애플리케이션 층
- HTTP, SMTP, POP3, DNS 등
    - DNS : 호스트 이름(ex naver.com)에 대응하는 IP 주소를 자동으로 가져오는 역할
    - DNS를 이용하려면 DNS 서버에 미리 호스트명과 IP주소의 대응 관계를 등록해야한다.
- 애플리케이션의 기능을 실행하기 위해, 데이터 형식과 절차를 결정한다.
    - 단순한 전기적 신호를 문자와 이미지 등 인간이 인식할 수 있도록 데이터를 표현한다.
- 데이터를 부르는 방법 : 메시지


### 2. 트랜스포트 층
- TCP, UDP
    - TCP : 신뢰성 있는 애플리케이션 간의 데이터 전송을 위한 프로토콜
        - TCP에 의한 데이터 전송 절차  
        1. 3way 핸드쉐이크를 통해 통신 확인
        2. 전송 데이터에 TCP헤더 추가
        3. ACK를 통해 데이터 수신 확인 - 일부 데이터가 제대로 도착하지 않았다면, 재전송
        4. TCP 커넥션 끊기
    - UDP : PC나 서버등에 도달한 데이터를 적절한 애플리케이션에 배분하는 프로토콜
        - TCP와 같은 확인처리를 하지 않아서 전송 효율이 좋다. 하지만 신뢰성은 그만큼 낮다.
- 다양한 타이밍에, 여러 애플리케이션으로 들어오는 데이터를 적절하게 배분한다.
- 데이터를 부르는 방법 : 세그먼트 혹은 데이터그램

### 3. 인터넷 층
- IP, ICMP, ARP
    - IP의 역할 : 엔드투엔드 통신 즉 네트워크 상의 어떤 PC에서 다른 PC로 데이터를 전송하는 것.
    - TCP/IP 통신에서는 반드시 IP주소를 지정해야한다.
    - IP주소는 '네트워크 부'와 '호스트 부'로 구성되어있으며 '서브넷 마스크'로 구분된다.
    - IP는 중복되지 않는 공인 IP와 사설 네트워크에서 사용되는 사설 IP가 있다.  
        - 사설 네트워크에서 인터넷 요청을 보내는 것은 정상적으로 요청이된다.
        - 하지만 인터넷에서 사설 IP로 응답할때는 문제가있다. 사설 IP는 특정할 수 없기때문이다.  
        따라서 NAT를 활용한다. 
            1. 사설 네트워크에서 인터넷으로 요청을 전송할 때, 출발지 IP를 변환
            2. 라우터는 변환된 주소의 대응을 NAT 테이블에 보존
            3. 응답이 돌아오면 위 테이블을 사용해서 목적지 IP 주소를 변환.
    - ICMP  
        - IP요청은 성공 여부를 알 수 없다. 따라서 엔드투엔드 통신이 정상적으로 이루어졌는지를 확인하는 프로토콜인 ICMP가 개발됐다.
        - 요청이 정상적으로 처리되지않고 폐기되었다면 폐기한 기기가 그에 대한 정보를 출발지로 전송한다. 
    - ARP
        - TCP/IP의 IP 주소와 인터페이스를 식별하기 위한 MAC 주소를 대응시키는 역할      
- 라우터로 연결된 많은 네트워크 사이에서 데이터를 전송한다.
- 엔드투엔드 통신을 한다.
- 데이터를 부르는 방법 : 패킷 또는 데이터그램

### 4. 네트워크 인터페이스층
- 유선 이더넷, 무선 LAN(Wi-Fi)
- 물리적인 영역
- 같은 네트워크 안에서 데이터를 전송한다.
    - 같은 네트워크 :  라우터와 레이어3 스위치로 구획되는 범위, 또는 레이어2 스위치로 구성되는 범위
- 프로토콜은 통신 상대와 상관없이 자유롭게 선택할 수 있다. 
- 데이터를 부르는 방법 : 프레임

---

### 데이터 송수신 규칙
- Header  
위 네개의 프로토콜의 기능을 사용하기 위해서는 제어 정보(헤더)가 필요하다.  
데이터를 전송하는 경우, 출발지와 목적지에 대한 정보가 헤더에 담겨야할 것이다.  
헤더를 추가하는 과정을 캡슐화라고 하며 반대로 데이터를 받았을 때 헤더를 바탕으로 데이터를 처리하는 것을 역캡슐화 혹은 비캡슐화 라고 한다.  
헤더는 프로토콜마다 추가된다.  

- 목적지  
단 한 곳으로 데이터를 전송하는 것을 유니캐스트  
같은 네트워크 상의 모든 호스트에 같은 데이터를 전송하는 것을 브로드캐스트  
같은 애플리케이션이 동작하는 등, 특정 그룹에 포함되는 호스트에 같은 데이터를 전송하는 것을 멀티캐스트 라고 한다.  

- DHCP  
TCP/IP 설정을 자동화 하는 방법  

