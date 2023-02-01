# Pacemaker 이중화

## Pacemaker 서비스 이중화(tomcat 서비스)
- 사용 이유  
서비스 사용중 문제가 생겼을때 지연없이 곧바로 연결된 다른 서버를 사용해서 가용성을 증가시킨다.  

- pcs pacemaker tomcat 필요
    - pcs : 이중화 관련 데몬(pacemaker, corosync)제어 도구
    - pacemaker : 클러스터를 통해 이중화를 구현하는 클러스터 리소스 관리도구 

- 방화벽 해제 필요

- 과정
1. 각 서버에 tomcat 설치(1 - 마스터, 2 - 슬레이브) - 다른 패키지도 설치
2. /var/lib/tomcat9/webapps/ROOT/index.html 수정하여 구분
3. 호스트 네임 설정, 호스트 파일 설정(이후 설정시 편리하기 위함)
4. hacluster 계정 설정 - 인증, 클러스터 설정
    - 계정 비밀번호는 노드간 동일해야한다.
    - 클러스터 설정 후 실행 필요
    - 확인은 pcs(crm) status
    - Online[ node1 node2 ]로 묶여있는 것이 확인되면 성공
5. vip 등록하여 서비스 확인
    - heartbeat의 IPadd2 를 사용해서 VIP 등록
    - VIP 주소로 페이지에 접속해서 node1을 재시작했을때 node2의 서비스가 실행되면 성공


## Pacemaker 데이터 이중화(tomcat 로그)

- 톰캣 로그  
/var/log/tomcat9 혹은 /var/lib/tomcat9/logs  

- drbd-utils, corosync, heartbeat 설치 필요

- 과정
1. [양쪽]hostname 설정, 호스트 파일 설정
2. [양쪽]drbd-utils 설치
3. [양쪽](선택)디스크 추가
4. [양쪽]파티션 설정
5. [양쪽]drbd 리소스 설정
```res
resource "clustertest" {

protocol C;

on node1 {
        device /dev/drbd0; #drbd의 논리적 위치
        disk /dev/sdb1; #사용되는 디스크의 물리적 위치
        address 172.10.10.151:7789; # 호스트 IP
        meta-disk internal; #메타데이터 저장설정
    }
on node2 {
        device /dev/drbd0;
        disk /dev/sdb1;
        address 172.10.10.151:7789;
        meta-disk internal;
    }
}
```
6. [양쪽]drbd 메타데이터 저장소 생성(create-md)
7. [양쪽]drbd 서비스 실행
8. [마스터]primary 서버 설정
9. 동기화 대기 - /proc/drbd를 통해 확인 가능
10. 동기화 확인 - drbdamd status
11. [마스터] DRBD 리소스 생성
    - Data 리소스
    - Clone 리소스 : 승격 가능한 클론 생성
    - 파일 시스템 리소스 : drbd 마운트 포인트 설정
12. (선택)다른 리소스가 존재하는 경우, 순서 설정

---
## 테스트
1. tomcat 로그 파일이 저장된 폴더를 drbd장치에 마운트
2. primary인 node1을 standby로 설정
3. pcs status로 standby여부, promoted여부 확인
4. node2의 마운트 폴더에서 로그파일 확인

---
### 용어
- fail-over : 장애극복
- HA : 고가용성
- DRBD : Distributed Replicated Block Device  
블록 단위로 분산 복제하는 장치  

---

### 참고
- RAID 1 vs DRBD
    - RAID 1은 한개 서버 아래에서 디스크를 미러링 하는 것  
    한 디스크에 문제가 생기면 미러링된 다른 디스크를 통해 데이터 사용 가능
    - DRBD는 서버간 디스크를 네트워크를 통해 동기화하는 것  
    디스크 혹은 서버에 문제가 발생하면 해당 서비스를 종료하고 standby 상태인 다른 서버로 서비스를 제공
- 로그 확인
    - corosync.log : 동기화 관련 
    - pcsd.log : 노드의 연결, 접속관련 로그
    - pacemaker.log : pacemaker 서비스와 관련한 모든 로그
    - messages : 클러스터 로그 확인 가능

- 트러블 슈팅
    - 각 노드는 온라인이 확인 되었으나 상대편 노드가 UNCLEARED offline이 뜨는 경우  
    : 호스트 이름 변경 후 reboot을 하니 제대로 적용
    - 파일 시스템 리소스 구성에서 node1 서버(MASTER)에는 문제가 없으나 node2 서버에서 'no such file or directory' 라는 오류와 함께 공유가 안되는 문제  
    : node2 에 heartbeat를 설치하니 해결
