# IP

### 네트워크/호스트  
IP는 네트워크 부분/ 호스트 부분으로 나뉜다.  
라우터를 기준으로, 같은 네트워크에 있는 주소들은 네트워크 부분이 동일하고  
다른 네트워크는 네트워크 부분이 다르다.

- 클래스 A  
0xxx xxxx.xxxx xxxx.xxxx xxxx.xxxx xxxx 범위  
1.0.0.0 to 126.0.0.0 (0.0.0.0제외)  
앞 8비트(한자리)만 네트워크 부분으로 사용한다.  
따라서 네트워크는 126개  
호스트는 2의 24승 - 2개(모두 1 혹은 0인것은 제외) 개  
- 클래스 B  
10xx xxxx.xxxx xxxx.xxxx xxxx.xxxx xxxx 범위  
128.0.0 to 191.255.0.0 까지  
네트워크는 128.1 에서 191.255까지 이며  
호스트 부분은 나머지인 2의 16승 - 2개
- 클래스 C  
110x xxxx.xxxx xxxx.xxxx xxxx.xxxx xxxx 범위  
192.0.0.0 to 223.255.255.0 까지  
네트워크는 192.0.0 ~ 233.255.255까지 이며  
호스트는 끝의 254개만 가진다.
- 클래스 D,E  
잘안쓰임  

### 게이트웨이  
내부 네트워크는 라우터 없이도 통신이 가능하며  
내부에 찾지 못한경우 게이트웨이를 통해 밖으로 나가게 된다.  
게이트웨이는 라우터 인터페이스의 주소가 된다.  

### 서브넷 마스크  
- 용도 : 브로드 캐스트 영역 나누기/ IP주소 아끼기
- 사용 : 클래스 B를 사용하는 경우, 호스트영역이 6만개가 넘는다.  
이것을 클래스 C 처럼 나누기 위해서 255.255.255.0이라는 서브넷 마스크를 사용하면  
세번째(x.x.0.x)영역이 그대로 내려와 클래스 C를 사용하는 것과 같게 된다.  
- 규칙 : 이진수가 연속해서 1이 나와야한다.  
즉 1111 1111.1111 1111.1111 1111.0000 0000(255.255.255.0)은 가능하나  
1111 1111.1111 1111.1111 1111.0000 1010(255.255.255.10)은 불가능하다  
하지만 1111 1111.1111 1111.1111 1111.1111 1100(255.255.255.252)은 가능하다.  
- 또 네트워크가 나우어지게 되면 서로 통신하는 방법은 라우터 뿐이다.  
따라서 서브넷 마스크를 이용해서 나눈 다음에도 두 네트워크 사이에는 라우터가 있어야한다.  
