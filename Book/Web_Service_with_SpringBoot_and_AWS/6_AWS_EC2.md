# AWS 서버 환경을 만들어보자

> **24시간 작동하는 서버 구하기**
1. 개인 PC를 24시간 켜놓는다.
2. 호스팅 서비스(Cafe24, 코리아 호스팅)를 이용한다.
3. 클라우드 서비스(AWS, AZURE, GCP)를 이용한다.  
    - 단순히 물리 장비를 대여하는 것만 아니라 로그 관리, 모니터링, 하드웨어 교체, 네크워크 관리 등을 지원
        - IaaS : 기존 물리 장비를 미들웨어와 함께 묶어둔 추상화 서비스. AWSDML EC2, S3 등
        - PaaS : IaaS를 한번 더 추상화해서 많은 기능을 자동화 했다. AWS의 Beanstalk, Heroku 등
        - SaaS : 소프트웨어 서비스. 구글 드라이브, 드랍박스 등

### AMI  
Amazon Machine Image  
: EC2 인스턴스를 시작하는 데 필요한 정보를 이미지로 만들어 둔 것.  

> **요약**  
아마존에 가입해서 인스턴스를 만들고, 확인했다  
이번 장은 코드작성 없이 책을보고 따라해야했다.