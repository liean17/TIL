# Pacemaker 이중화

## Pacemaker 서비스 이중화(tomcat 서비스)
- 사용 이유  
서비스 사용중 문제가 생겼을때 지연없이 곧바로 연결된 다른 서버를 사용해서 가용성을 증가시킨다.  

- pcs pacemaker apache2 tomcat 필요

- 과정
1. 각 서버에 tomcat 설치(1 - 마스터, 2 - 슬레이브)
2. /var/lib/tomcat9/webapps/ROOT/index.html 수정하여 구분
3. [대참고사이트](https://securet.tistory.com/34)
4. tomcat 사이트 확인시에는 방화벽 해제 필수
5. 확인은 vip로 확인


## Pacemaker 데이터 이중화(tomcat 로그)

- 톰캣 로그  
/var/log/tomcat9 혹은 /var/lib/tomcat9/logs  

- drbd 유틸, mariadb 추가 설치 필요

- 과정
1. 서비스 이중화와 마찬가지로 hostname 설정, 호스트 파일 설정
2. drbd-utils 설치
3. 방화벽 설정
4. drbd 모듈 확인
5. drbd 설정 
```res
resource "clustertest" {

protocol C;

startup {

wfc-timeout           0;

degr-wfc-timeout  120;   

      }

     disk {

        on-io-error detach;

        resync-rate 300M;

     }

    net {

timeout              60;    

connect-int        10;    

ping-int              10;

max-buffers               20000;

unplug-watermark     20000;

max-epoch-size         20000;

    }

on node1 {

        device /dev/drbd0;

        disk /dev/sdb1;

        address 172.10.10.151:7789;

        meta-disk internal;

    }

on node2 {

        device /dev/drbd0;

        disk /dev/sdb1;

        address 172.10.10.151:7789;

        meta-disk internal;

    }

}
```
6. 각 호스트에 복사
7. 파티션 설정(https://www.sharedit.co.kr/posts/10092#)
8. 메타데이터 저장소 초기화
9. drbd데몬 시작
10. 기본이 되는 호스트에 다음 입력
`sudo drbdadm -- --overwrite-data-of-peer primary all`
11. 보조 호스트와 동기화 시작
12. 파일 시스템 추가 후 DRBD 마운트

---
## 테스트
1. tomcat 로그 파일을 특정 폴더로 복사
2. 기본 호스트에서 마운트 해제
3. 기본 서버를 보조로, 보조를 기본으로 변경
4. 보조에서 파티션 마운트
5. 보조에 파일이 복사되어있다
-> 즉 보조가 기본이 되었을때 해당 폴더의 내용을 그대로 가져온것

---
### 용어
- fail-over : 장애극복
- HA : 고가용성
- DRBD : Distributed Replicated Block Device  
블록 단위로 분산 복제하는 장치  

---

### 보충
1. Pacemaker와 DRBD의 조합?
2. DRBD는 가상 장치로써 각각 마운트해서 사용해야하는지 아니면 Pacemaker를 사용하면 즉각적으로 전환이 가능한건지