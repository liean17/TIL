# AWS배포

## 목표
- public, private 서브넷을 분리한 배포
- CodeDeploy를 사용한 배포자동화
- HTTPS 배포
- 퍼블릭 엑세스 차단 후 SDK를 사용한 이미지 관리

---
### 22.10.27
- 기본 배포
    1. vpc 생성, public-private 서브넷 분리
    2. 라우팅테이블로 서브넷 연결, 인터넷 게이트웨이 설정
    3. bastion 인스턴스 생성 - nat 인스턴스로 생성 -> 탄력적 IP 할당
    4. private 라우팅 테이블 설정
    5. private 인스턴스 생성
    6. 로드밸런서 설정

    > NAT      https://docs.aws.amazon.com/ko_kr/vpc/latest/userguide/VPC_NAT_Instance.html
    
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
        - RDS 보안그룹 설정  
            - 내 IP - 로컬
            - server
            - (선택) bastion - workbench 확인용
        - RDS 생성에서 Mysql WorkBench 연결 테스트까지  
        [참고](https://aws.amazon.com/ko/premiumsupport/knowledge-center/rds-connect-ec2-bastion-host/)  
        vpc 설정하고 서브넷 그룹(private)을 생성해서 설정해준다.  
        rds는 프라이빗 서버와만 통신하고 보안그룹을 통해 bastion에서 사용할 수 있게 된다.  

    2. AWS IAM 사용자 계정 생성, 역할 생성 및 사용자 계정과 연결
    3. Gradle.yml 작성 - 배포 설정  
        - github에서 생성할때는 기본적으로 main 브랜치에서 배포하려하므로 다른 브랜치에서 사용하려면 IDE로 따로 만들어주거나 해야한다.  
    4. appspec.yml 작성 - 배포 권한, 경로설정
    5. deploy.sh - 배포 로그관련 설정
        - 위 두 파일은 commit되어야한다. 배포에 사용되니까
        - application.yml파일이 필요하다. 비어있는 파일이라도 있어야한다.
    6. AWS CodeDeploy에서 애플리케이션과 배포 그룹을 생성해야한다.  
    

---
### 22.10.28
- HTTPS 배포
    - 준비물
        - Route 53
        - 도메인(ex 가비아)
        - 인증서
    - 과정
        1. 도메인을 구매한다.
        2. Route 53에 호스팅 영역 설정.
        3. NS(NameServer)에 있는 값을 도메인을 구매한 곳에서 설정한다.
        4. ACM(aws 인증 매니저)에서 인증서 등록
            - 주의 : 도메인 이름을 그냥 설정해도 되지만, 와일드카드(*)를 사용해서 서브도메인을 한번에 등록하면 좋다.
        5. 로드밸런서에서 443(https)설정, Forward에서 타겟그룹을 설정하고, ACM 설정
            - 80포트는 443으로 리다이렉트 설정해주면 더 깔끔하다.
        6. Route 53에 설정한 호스팅 영역에 레코드를 추가해서 ALB를 설정해준다.

- S3에 이미지 업로드
    - 과정
        1. aws관련 의존성 추가
        2. aws 접근을 위한 AwsConfig생성
        ```java
        @Configuration
        public class AwsConfig {
            @Value("${cloud.aws.credentials.access-key}")
            private String iamAccessKey;
            @Value("${cloud.aws.credentials.secret-key}")
            private String iamSecretKey;
            @Value("${cloud.aws.s3.region.stack}")
            private String region = "ap-northeast-2";

            @Bean
            public AmazonS3Client amazonS3Client(){
                BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(iamAccessKey,iamSecretKey);
                return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                        .withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                        .build();
            }
        }
        ```
        3. amazonS3Client를 이용해서 이미지 저장 메서드 추가
        ```java
            String storeFilename = fileStore.storeFile(file);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            amazonS3Client.putObject(
                    new PutObjectRequest(S3Bucket,storeFilename,file.getInputStream(),objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );

            String imagePath = amazonS3Client.getUrl(S3Bucket, storeFilename).toString();
        ```

        4. 버킷 권한 설정
            - 퍼블릭 엑세스 차단(지금 방법은 ACL 퍼블릭 엑세스를 풀어줘야한다)
            - 버킷 정책 설정  
            ```json
            {
                "Version": "2012-10-17",
                "Id": "Policy1666940934219",
                "Statement": [
                    {
                        "Sid": "Stmt1666940930169",
                        "Effect": "Allow",
                        "Principal": "*",
                        "Action": [
                            "s3:DeleteObject",
                            "s3:GetObject",
                            "s3:ListBucket",
                            "s3:PutObject",
                            "s3:PutObjectAcl"
                        ],
                        "Resource": [
                            "arn:aws:s3:::kdk-first-s3",
                            "arn:aws:s3:::kdk-first-s3/*"
                        ]
                    }
                ]
            }
            ```
        5. 객체 소유권 편집
            - ACL활성화
        - CORS 설정  

        > 퍼블릭 엑세스 차단  
        현재 S3는 퍼블릭 엑세스가 일부 차단되어있지않다. 따라서 외부에서 데이터 접근이 가능하다.  
        외부에서 접근할 수 없게 퍼블릭 엑세스를 차단하는 것이 원칙이다.  
        대표적인 방법으로는 presignedUrl를 받아와서 제한을 두어 자료를 가져오는 방법이 있다.



- 트러블슈팅 : 221027
    - bastion 인스턴스 접속 불가  
    : SSH로 인스턴스에 접근하려하니 시간초과가 발생했다  
    -> 탄력적 IP를 할당하지 않고 private 주소로 요청해서 그랬다.  

    - 배포도 성공적이고 로드밸런서 보안그룹도 정상적인데 배포 URL로 접속이 안되는 문제  
    : 가용영역을 private로 설정했다. 당연하게도 public으로 설정해야 접근이 된다.  

    - 배포자동화 배포 단계에서 오류  
    : IAM 사용자 설정, 역할 설정을 완료하고 서로 연결을 해두지 않았다.  
    그리고 IAM설정을 하면 codedeploy를 재시작해줘야 한다.

- 트러블슈팅 : 221028
    - ACL 관련 오류
        - 배포는 되었는데 이미지 업로드시 ACL관련 오류가 발생했다.  
        퍼블릭 엑세스 관련 문제인 것 같아서 수정했지만 그대로였다.  
        알고보니 객체 소유권 편집을 해줘야했다.
    - 시큐리티에서 "//"를 받지 못하는 문제
        - 기존에는 이미지를 파일이름만 저장해서 수정하는 과정에서 발생한 문제였다.  
        전체 링크를 저장하도록 해서, 템플릿에도 전체 링크를 받도록 수정했다.

- 트러블슈팅 : 221126
    - bastion서버, private서버 접근 불가  
        - bastion에서 private에 접근하려하니 시간초과도 아닌 계속 입력을 대기하는 문제가 발생했다.  
        그러다가 나중에는 bastion서버 접근도 동일한 오류가 났다.  
        알고보니 그냥 오타였다. 맥의 Page앱에서 따옴표를 다른 모양으로 인식해서 닫히지않은 따옴표를 계속 대기한것이었다.

- 트러블슈팅 : 221202  
    - 생성한 rds와 연결 실패(Mysql WorkBench)  
    Mysql WorkBench를 사용해서 연결테스트를 하는데 계속 실패했다.  
    인바운드도 열려있고 서브넷도 제대로 설정한것같은데 실패하길래 rds생성을 잘못했나 싶어 여러번 깔았다 지웠다.  
    - 해결 : connection method를 잘못설정했다.  
    Standard TCP/IP가 아니라 Standard TCP/TP over SSH를 선택해야했다.  

- 트러블(슈팅x) : 221203
    - 배포자동화 첫단계에서 계속 실패한다.  
    codedeploy 로그를 살펴보니 iam 역할관련 문제인것같은데  
    예전에 사용한것을 그대로 사용하는 만큼 역할은 제대로 설정되있을텐데 잘 되지않는다.  
    어제도그렇고 기존에 사용하던것을 사용하다보니 여러 문제가 있었다.  
    처음부터 다시하는게 좋아보인다.
    

---
### local -> remote 파일전달
```
scp -i '전달할때 사용될 key' '전달할 파일' ec2-user@address:/home/ec2-user

```
