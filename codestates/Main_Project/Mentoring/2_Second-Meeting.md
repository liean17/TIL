> 지난숙제   
VPC 생성하고, public과 private 서블릿을 생성한다.  
그리고 private에 ec2를 연결해야하는데 아마 로컬에서 연결이 안될거다.  
로드밸런서를 사용해서 로컬에서 접근할 수 있도록 만들자.

> **숙제풀이** :  

- VPC : aws에서 제공하는 격리된 가상 네트워크  
- 서브넷 : vpc주소를 쪼갠 네트워크 주소
    - public
    - private : 외부와 차단되어있으며 다른 서브넷과 연결만 가능하다.  
    이번에 내가 한 작업은 NAT(Network Address Tranfer) 인스턴스를 public 서브넷에 생성해서 private내에 있는 인터넷이 가능하게 한 작업이다.  
    `이렇게 하는 이유` : 행위별로 IP를 분리해서 효율적으로 운영하기 위함, 외부에서 노출되면 곤란한 서비스를 별도로 관리하기 위함.  
- 라우팅 : 네트워크 트래픽이 전달되는 경로를 제어하는 것.  
- 인터넷 게이트웨이 : 격리된 vpc에 생성된 인스턴스에서 인터넷을 사용하기 위해 설정하는 것.  
- 보안그룹 : 인스턴스에 대한 인바운드, 아웃바운드 트래픽을 제어하는 가상 방화벽  
    - 인바운드 : 접근하려는 대상에 대한것
    - 아웃바운드 : 나가려고하는 대상에 대한 것

> 참고  
[왕초보 탈출 AWS](https://minjii-ya.tistory.com/32?category=946161)  
[public->private](https://galid1.tistory.com/367)  
[VPC공식문서](https://docs.aws.amazon.com/ko_kr/vpc/latest/userguide/what-is-amazon-vpc.html)  
[VPC란](https://memory-hub.tistory.com/11)
---


- 사진 여러개 받기.  
