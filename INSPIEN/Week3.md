# Week 3

## DISK 종류
- ### 무엇인가?
    DISK는 저장장치  
    SSD, HDD로 나뉘며 연결용 인터페이스는 SATA SAS PATA PCIe-SSD가 있다.  
- ### 왜 알아야 하는가?
    - 나의 생각  
    디스크의 입출력이 성능에 큰 영향을 미치기 때문에?  

- ### 정리
    - HDD : 물리적으로 회전하는 디스크를 사용하는 저장장치  
        - 장점 :  
        저렴한 가격으로 RAID구성을 통해 안정성확보  
        장애 복구가 비교적 쉽다  
        
    - SSD : 플래시 메모리를 사용하는 저장장치  
        - 장점 :  
        빠른 엑세스 속도  
        엑세스 속도가 빠른 만큼 성능을 위한 비용(RAM 등)이 줄어든다?
        하드디스크에 비해 물리적으로 안정적이다.  
    
    - PATA(Parallel)(IDE) 병렬 연결 방식  
    전송 속도가 느렸던 과거에 사용된 방식이다.  
    선이 여러개라 두껍고 신호 간섭이 발생하는 문제도 있었다.  

    - SATA 직렬 연결 방식  

    - SAS(SCSI)  
        - SCSI  
        SCSI 컨트롤러를 통해서 장치들을 제어하며  
        컨트롤러 자체에서 장치를 관리하기 때문에 CPU에 부하를 주지 않아 처리가 빠르고 안정적이다.  
        - SAS  
        직렬 방식의 SCSI  
    - PCIe
        - CPU에서 lane을 할당받아 데이터를 주고받는 인터페이스  
        - 흔히 그래픽카드를 장착하는 슬롯으로 사용되는데 저장장치를 연결하면 sata3에 비해 pcie3.0의 속도가 5배정도 차이나기도 한다.  
        - nvme m.2가 대표적이다.  


## RAID의 의미와 종류
- ### 무엇인가?
    복원을 위해 중복되는 데이터를 만들고, 전체 데이터들을 나누어 각 독립적인 디스크들에 배열하는 기술
- ### 왜 알아야 하는가?
    - 나의 생각  
    저장공간의 안정적인(성능도?) 관리를 위해서 필요하다.  
- ### 정리
    - 목적  
        - 무정지(안정성) 구현
        - 고성능 구현
    - 구현 방법  
        - 하드웨어 : RAID 카드에 있는 별도의 컨트롤러에서 RAID를 관리해서 성능이 매우 좋다.  
        - 펌웨어(드라이버) : 부팅 이전 바이오스에서 설정  
        운영체제와 관계없이 작동해서 소프트웨어 방식에 비해서는 성능이 좋다.
        - 소프트웨어 : 부팅 이후 운영체제 단에서 구현하는 방법   
        다른 프로그램들과 리소스를 함께 사용해서 성능이 가장떨어진다.
    - 종류
        - 0  
        데이터를 여러디스크에 저장하고, 읽어올때도 여러 디스크에서 읽어들이는 기술.  
        하나의 디스크 처럼 사용하지만 각 디스크는 독립적으로 구동하기 때문에 디스크를 추가하는 만큼 읽기,쓰기 성능이 상승한다.  
        단 하나의 디스크만 고장나도 전체 데이터를 읽을 수 없게된다.  
          
        - 1  
        미러링이라고도 하며 각 디스크에 데이터를 똑같이 복제하는 방식  
        디스크가 늘어나도 용량은 동일하지만 어느 하나의 디스크가 고장나더라도 문제없이 사용할 수 있어서 안정성과 지속성이 높다.
        - 01  
        전체 디스크의 절반을 0 방식으로 구성하고 나머지 절반을 1 방식처럼 미러링 하는 방식  
        - 10  
        1 방식으로 각각 미러링된 디스크를 0 방식으로 구성하는 방식  
        총 디스크가 6개라고 가정했을때, 01과 달리 복구용 디스크도 많고 한 레이드에 엮인 디스크도 적어서 01보다 선호되는 방식이다.
        - 2,3,4(**추가차이도 알아두기**)  
            - 해밍코드 :  
            기존 데이터에 패리티 데이터를 추가하여 오류가 발생했을때, 해당 코드를 원래대로 수정하기 위해 사용하는 기술이며 데이터 저장시 추가로 저장된다.  
            - 패리티 : 기존의 정보와 같은 상태인지 확인을 하기 위해 쓰이는 데이터

        기록용 저장공간과 복구용 저장 공간을 별도로 분리하여 구성하는 방식  
        2는 비트 단위, 3은 바이트 단위, 4는 블록 단위로 데이터를 저장한다.  
        해당 방법은 안정성은 증가하지만, 패리티 저장용 디스크에 패리티 정보를 저장하면서 병목 현상이 발생하고  
        데이터를 읽는 과정에서 패리티 디스크는 제외하는 과정에서 속도가 감소한다.  
        또 패리티 디스크가 고장나면 복구가 불가능해진다.  
        - 5  
        4 처럼 블록 단위로 데이터를 저정하지만, 각 디스크에 돌아가면서 복구 정보를 저장한다.  
        따라서 읽기 작업에서는 전체 디스크에 분산되어 속도가 상승하지만 매번 쓰기 작업때마다 복구 정보 연산 과정이 추가되어서 비효율적인 면도 존재한다.  
        어떤 디스크가 고장나도 고장난 디스크가 한개라면 복구가 가능하다.  
        - 15, 51
        - 6  
        5에서 에러 검출, 수정용 데이터를 추가한 방식  
        디스크 용량이 하나 주는 만큼, 두개의 디스크가 고장나도 복구가 가능하다.
        - 50  
        여러 디스크에 번갈아가며 저장하는 방식인 0 방식으로 5 디스크들을 다시 그룹핑 하는 방식

    
## PV, VG, LV 설정 및 구성
- ### 무엇인가?  
    Logical Volumn Manager : 저장장치를 효율적이고 유연하게 관리해 주는 것  
    디스크 공간을 논리적 레벨에서 관리하기 때문에 저장공간의 확장, 축소가 편리하다.  
    PV : 물리적인 하드디스크  
    PE : LV를 만들기 위해 설정하는 디스크의 최소 공간이며 기본값은 4MB    
    VG : 물리적으로 존재하는 디스크를 하나로 모은 그룹  
    LV : 가상 파티션  
    LE : LV를 만들기 위해 설정하는 최소 공간이며 PE와 크기가 같다.  
- ### 왜 알아야 하는가?
    - 나의 생각  
    로그를 저장하는 저장공간을 늘린다고 했을때 10gb에서 50gb로 늘린다면, 기존 디스크의 파일을 모두 백업해서 교체해야한다.  
    LVM을 사용하면 저장공간이 늘어나는 효과만 가질 수 있다.(디스크 공간을 쉽게 확장할 수 있고, 여러 디스크를 하나처럼 사용할 수 있다.)  

## local mirror site 구성(repository sync)
- ### 무엇인가?  
    - local mirror site
    로컬 환경에서 구성된 미러사이트, 인터넷 접속을 할 수 없는 환경을 위해 구성된 미러 사이트  
    외부에서 yum, apt등 정보를 받아와서 내부에 연결된 서버들이 빠르게 패키지를 받아갈 수 있도록 한다.  

- ### 왜 알아야 하는가? 
    - mirror의 이유  
    사용자를 분산시켜서 부하를 줄인다.  
    서버가 일시적으로 닫히게되어도 자료를 보존할 수 있다.  
    거리가 먼 사용자에게 가까운 미러 서버를 통해 빠른 접근을 제공할 수 있다.  
    - 즉 원래 서버에는 이미 접근이 많기 때문에 부하 분산과 사용자들의 빠른 접근을 위해서 mirror site를 구성해서 근접한 서버들이 접근할 수 있도록 하기 위함이다.  
- ### 방법 
    - (https://chhanz.github.io/linux/2022/05/09/ubuntu-config-apt-mirror-site/#sync)

## NAS 연결(NFS)
- ### 무엇인가?  
    - NFS란  
    네트워크에 파일을 저장하는 메커니즘, 네트워크를 통해 원격 컴퓨터의 파일과 디렉토리에 엑세스할 수 있다.  
    - NAS는  
    네트워크를 통한 물리적 원격 저장소  
```
NFS (Network File System) is a protocol that allows for the sharing of files and directories across a network. On a Linux-based Network Attached Storage (NAS) system, NFS can be used to share files and directories with other devices on the network. NFS allows for fast and efficient file transfer, and can be easily configured on Linux systems.

To set up NFS on a Linux NAS, you will need to install the NFS server package and configure the appropriate settings on the server. This typically involves editing the /etc/exports file to specify which directories should be shared, and what permissions and options should be applied to them. Once the configuration is done, you can mount the shared directories on client devices using the mount command.

A common use case for NFS on a Linux NAS is to provide network storage for other Linux devices in your network, such as servers or workstations. It's also used to store data backups, media files and other large files that need to be shared across devices.

In addition to NFS, there are other protocols that can be used to share files over a network, such as SMB (Server Message Block) or AFP (Apple Filing Protocol). Each protocol has its own advantages and disadvantages, and the one you choose will depend on your specific needs and the devices you are working with.
```
- ### 왜 알아야 하는가?
    - 나의 생각  
    <!-- 로컬 리포지토리를 구성하기 위해서 사용되는 것이 아닐까?   -->
    nas는 그냥 네트워크로 접속이 가능한 저장장치이다.  
    그리고 nfs는 네트워크를 통해 파일에
    - 고객사에서 대용량의 nas를 보유하고  
    우리(회사)가 해당 nas에 파일을 보내는 경우가 많다고 한다.  
    - nas의 장점  
    용량 확장이 쉽다  
    파일 접근 용도로만 사용되기 때문에 빠르다  
    네트워크가 연결된 모든 장치에서 접근이 가능하다.  
    



    
--- 

## 의문 리스트
- 디스크 종류는 SSD, HDD 그리고 SATA PATA이런걸 말하는게 맞는지?
- 디스크에 대해 알아야하는 이유는 입출력 속도와 안정성을 고려하는 능력이 필요해서 인지? 
- NFS를 통해서 local mirror site를 제공하는건지?