1. NFS 특징과 대체  
NFS는 내부 연결로는 간단해서 좋지만  
별 다른 인증없이 파일에 접근할 수 있다.  
보안을 신경쓴다면 SFTP를 사용하면 된다.  
- sftp : ssh프로토콜을 이용해서 파일을 전송하는 방식
    - ssh : 공개키 및 배포키를 사용해서 암호화하는 방식  
    다른 방식에 비해 유효성 검사 및 관리가 복잡한 만큼 보안성은 더 좋다.  
    - 방법 : [방법](https://daanv.tistory.com/103)(https://webdir.tistory.com/205)
    - 키 혹은 비밀번호를 사용한 로그인으로 접근을 제어할 수 있다.
- ftps와의 차이 
    - ftp에 보안을 추가한 방식으로 여러 포트를 사용한다.  
    또 FTPS는 명령과 데이터 채널 또는 데이터 채널만 암호화 하는 반면에 SFTP는 하나의 연결만 암호화 해서 인증정보와 데이터 모두를 암호화할 수 있다.  
    - [방법](https://jjeongil.tistory.com/1390)
    - [방법2](https://webnautes.tistory.com/1678)
2. NAS와 좋은 조합의 프로토콜  
- 내부로는 운영체제 호환성이 좋은 SMB사용
- 외부로는 사용이 편한 webdav나 보안이 좋은 sftp
    - webdav : HTTP, HTTPS를 사용해서 접근 가능  
    본인 외에도 접근할 가능성이 높아서 복잡한 암호설정, http 비사용등 추가적으로 보안에 많은 신경을 써야한다.
    - sftp 

3. lvmdump  
    - lvmdump가 포함하고 있는 정보
        - lvm 메타데이터
        - dm(device-mapper)setup 정보
        - 실행중인 프로세스에 대한 정보
        - /var/log/messages
        - lvm 구성 정보, 캐시 정보
        - /dev에서 제공하는 device node 정보
    - lvm 진단에 사용할 수 있는 데이터를 제공한다.
4. pv고장 대책
    - [방법](https://hyuksssss.tistory.com/entry/%EC%A0%80%EC%9E%A5%EC%86%8C%EA%B4%80%EB%A6%ACLVMRAID)
    - 메타데이터복구
        1. lvm메타데이터는 VG, LV에 대한 구성이 변경될때마다 자도으로 생성된다.
            - 백업이 가능한 데이터 리스트 확인 `vgcfgrestore --list`
        2. 메타데이터는 /etc/lvm/backup에  
        아카이브는 /etc/lvm/archive 파일에 저장된다
        3. vgcfbackup 명령을 사용해서 lvm 메타 데이터를 복원할 수 있다. 
    - PV 복구
        1. /etc/lvm/backup/test_vg에서 오류가 발생한 PV의 UUID 확인
        2. pvcreate --uuid "[UUID]"를 사용해서 이전과 동일한 물리볼륨 생성.  
    - VG 복구
        1. `vgcfgrestore -f /etc/lvm/backup/test_vg test_vg`명령을 통해서 VG복원
        2. vgchange -ay 명령을 통해 vg 활성화
    - 가장 좋은 것은 raid를 구성하는 것이고 그중에서 하드웨어를 통한 raid 구성이 가장 안전하다