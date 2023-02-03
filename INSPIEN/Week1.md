# Week 1 : OS(Ubuntu) 설정 - 1  

## 1. 파일 열기 갯수 제한 변경
- 방법  
    1. 최대 오픈 파일 수 확인  
    `cat /proc/sys/fs/file-max` 혹은  
    `ulimit -a` / `ulimit -aH`
    2. 수정  
    `/etc/security/limits.conf` 실행 후  
    <유저명> <soft/hard> <타입> <값> 형식으로 설정 후 저장
    3. 최대 오픈 파일 수 변경  
    `/etc/sysctl.conf`에 `fs.nr_open=5242880` 와 같이 설정
- 이유  
    : 리눅스에서는 한번에 실행할 수 있는 파일의 갯수가 제한되어있다.  
    따라서 설정된 파일 갯수를 초과해서 파일을 열 수 없기때문에 시스템에 문제가 발생할 수 있다.  
    + 적절한 설정값과 어떤 경우 많은 파일을 실행하게 되는지
        - 리눅스에서는 모든것이 파일이다.  
        장치를 연결하는데 있어서도 파일이 실행되는 것과 같고  
        프로그램을 사용하면 그와 관련된 라이브러리 역시 함께 로딩된다.  
- 기타  
    - soft limit , hard limit  
    hard limit는 soft limit의 최대 임계치이며 이것을 넘으면 곧바로 문제가 된다.  
    soft limit는 넘어도 문제가 되지는 않지만 조절을 위한 경고 등의 조치를 설정할 수 있다.  

## 2. 서버 타임존 설정(UTC+9)
- 방법
    - 시간 상세 조회  
    : `timedatectl`
    - 타임존 확인  
    : `timedatectl list-timezones`
    - 설정  
    : `sudo timedatectl set-timezone Asia/Seoul`
    - 심볼릭 링크 방법  
    : timedatectl 유틸리티가 없는 경우  
    : `ls -l /etc/localtime`으로 확인 후 해당 파일 삭제 혹은 덮어쓰기  
    : 이후 새롭게 심볼릭 링크 파일을 생성 `sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime`

- 이유
    - 로그를 올바르게 찍기 위해?

## 3. 서버 NTP 설정(google)
- 방법
    - ntp 서비스 설치  
    : apt-get install -y ntp
    - /etc/ntp.conf 설정  
    : 기존 ntp설정을 삭제하고 설정하려는 ntp서버에 따라 값 입력  
    구글의 경우 : `server time1.google.com iburst`
    - ntp 재시작  
    `sudo service ntp reload`  
- 이유
    - 여러 서버를 구성하는 경우, 각 서버마다 여러가지 이유로 시간이 다르게 설정될 수 있다.(설치 시간이 다른경우, 중력)  
    시간의 차이가 발생하면 로그를 정확하게 확인할 수 없고, 프로그램 실행에 있어서도 문제가 발생할 수 있다.  
    따라서 여러 서버의 시간을 동일하게 유지할 수 있는것이 NTP 서버이다.  

## 4. systemd-networkd-wait-online.service 비활성화
- 설명  
    : 네트워크가 구성되기를 기다리는 시스템 서비스.  
    systemd-network.service가 인식하고 관리하는 연결이 모두 configured되거나 실패하고, 하나라도 연결될때 까지 기다린다.
- 방법  
    : `systemctl disable systemd-networkd-wait-online-service`
- 이유
    - 네트워크 구성을 위해서 다른 서비스의 실행(ex. 부팅)이 미뤄지게된다.  

## 5. 커널 자동 업데이트 비활성
- 방법  
    - /etc/apt/apt.conf.d 의 10periodic, 20auto-upgrades 수정  
    ```
    # From
    APT::Periodic::Update-Package-Lists "1";
    APT::Periodic::Download-Upgradeable-Packages "0";
    APT::Periodic::AutocleanInterval "0";

    # To
    APT::Periodic::Update-Package-Lists "0";
    APT::Periodic::Download-Upgradeable-Packages "0";
    APT::Periodic::AutocleanInterval "0";
    APT::Periodic::Unattended-Upgrade "0";

    ```  
    혹은
    ```

    # From
    APT::Periodic::Update-Package-Lists "1";
    APT::Periodic::Unattended-Upgrade "1";

    # To
    APT::Periodic::Update-Package-Lists "0";
    APT::Periodic::Unattended-Upgrade "0";

    ```
- 이유  
    - 업데이트 되는 동안 패키지를 설치할 수 없거나 실행중인 프로그램이 종료되는 경우가 발생한다.  
    - 단점 : 필수적인 업데이트를 바로 적용할 수 없다.
- 기타  
    - 10periodic과 20auto-upgrades의 차이  
    : 오름차순으로 구문이 분석되어 적용된다.  
    또 20auto-upgrades에는 unattended-upgrades옵션이 있는데 최신 보안 패치 및 기타 업데이트를 자동으로 수행하고 시스템을 유지, 관리하는 서비스다.


---
### 보완할 것
- hard limit 최대 수치의 이유, 초과해서 설정하는 방법

$ sudo apt update
// 업데이트 확인

$ sudo apt upgrade -y
// 가능한 업데이트 실행

$ sudo reboot


$ sudo passwd root
- root활성화

$ su
- root 접근

$ whoami
- 사용자확인

---

# 추가사항

## 인터넷이 안되는 경우의 서버설정(수동업데이트 하는 법)
 - 1
 - NTP 설정과 타임존 설정
 - 로컬 레포지토리  
 : 인터넷이 연결된 상횡에서는 apt명령을 사용하면 인터넷에서  
 파일을 설치하거나 업데이트를 할 수 있는데  
 폐쇄환경에서는 Local Repository를 구성해야 할 필요가 있다.  

## 설정 이유에 대한 보충
- NTP 구성  
1. 시간에 의존하는 애플리케이션 동작 오류
2. 로그에 기록된 시간을 신뢰할 수 없음
3. 다른 서버와 함께하는 작업이 있을때 서로 기대한 작업시간이 달라서 올바르게 작동하지않는다.

- system network wait online service  
부팅 프로세스에서 네트워크가 연결될때까지 기다리는 서비스  
네트워크가 필요없거나 연결되어있지 않은 경우에도 불필요하게 대기해야한다.

- 커널 업데이트 비활성화  
커널 자동 업데이트는 최신 패치를 즉시 적용하면서 안정성을 얻게되지만 자동 업데이트로 인한 비정상 종료로 동작의 안정성은 오히려 감소할 수 있다.

- Hard Limit 기본값 이상으로 설정  
    - /etc/sysctl.conf 에서 `fs.nr_open=5242880` 처럼 설정
    - 이후 ulimit 명령으로 변경 가능

    - 한계초과를 확인하는 방법(/var/log/messages)
        - 우분투 10.x 버전 이후부터 rsyslogd 데몬에 무게를 실어서 val log messages는 주석처리가 되어있다.  
        주석을 해제하면 생성된다.