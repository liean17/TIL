# AWS배포

## 목표
- public, private 서브넷을 분리한 배포
- CodeDeploy를 사용한 배포자동화
- HTTPS 배포

---
### 22.10.27
- 기본 배포
    1. vpc 생성, public-private 서브넷 분리
    2. 라우팅테이블로 서브넷 연결, 인터넷 게이트웨이 설정
    3. bastion 인스턴스 생성 - nat 인스턴스로 생성 -> 탄력적 IP 할당
    4. private 라우팅 테이블 설정
    5. private 인스턴스 생성
    6. 로드밸런서 설정
    > NAT  
    https://docs.aws.amazon.com/ko_kr/vpc/latest/userguide/VPC_NAT_Instance.html
    7. 보안그룹 설정
        - *아웃 바운드는 모두 열어주고 인바운드로 통제한다.*
        - bastion host
            - 80 : http웹 요청
            - 443 : https 요청
            - 22 : SSH요청 - 본인 IP에서만.
        - private server
            - 22
            - 8080 : 서버를 실행시켜야한다.
        - 로드밸런서 : 모두 열어준다

- 배포 자동화
    1. RDS, S3 생성(h2사용시 S3만 있어도 된다.)
    2. AWS IAM 사용자 계정 생성, 역할 생성 및 사용자 계정과 연결
    3. Gradle.yml 작성 - 배포 설정
    4. appspec.yml 작성 - 배포 권한, 경로설정
    5. deploy.sh - 배포 로그관련 설정
    - RDS 보안그룹 설정  
        - 내 IP - 로컬
        - server
        - (선택) bastion - workbench 확인용





- 트러블슈팅
    - bastion 인스턴스 접속 불가  
    : SSH로 인스턴스에 접근하려하니 시간초과가 발생했다  
    -> 탄력적 IP를 할당하지 않고 private 주소로 요청해서 그랬다.  

    - 배포도 성공적이고 로드밸런서 보안그룹도 정상적인데 배포 URL로 접속이 안되는 문제  
    : 가용영역을 private로 설정했다. 당연하게도 public으로 설정해야 접근이 된다.  

    - 배포자동화 배포 단계에서 오류  
    : IAM 사용자 설정, 역할 설정을 완료하고 서로 연결을 해두지 않았다.  
    그리고 IAM설정을 하면 codedeploy를 재시작해줘야 한다.