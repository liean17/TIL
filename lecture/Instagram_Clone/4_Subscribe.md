# 구독하기 API 구현

## 연관관계  

1. FK는 Many가 가져간다.  
2. 다대다 관계는 중간 테이블이 생긴다. 

## 모델 만들기
구독은 유저간에 행해지기 때문에 다대다 관계다.  
따라서 중간 테이블의 생성이 필요하다.
```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "subscribe_uk",
                        columnNames = {"fromUserId","toUserId"}
                )
        }
)
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "fromUserId")
    @ManyToOne
    private User fromUser;

    @JoinColumn(name = "toUserId")
    @ManyToOne
    private User toUser;

    private LocalDateTime createdDate;

    @PrePersist
    public void createDate(){
        this.createdDate=LocalDateTime.now();
    }
}
```
다대다 관계의 경우 자동으로 중간 테이블을 형성해주기도 한다. 위는 그것을 응용한 것이다.  
맨 위 테이블 어노테이션이 특이한데 데이터의 중복을 걸러내는 것이다.  
단일 데이터라면 unique속성으로 편하게 방지할 수 있지만 두가지 데이터의 관계에서 걸러내야하기때문에  
위와같은 방법을 사용한다.  

---

## 구독,취소 기능 구현
우선 구독관련 Repository를 만든다
```java
public interface SubscribeRepository extends JpaRepository<Subscribe, Integer> {
    @Modifying
    @Query(value = "INSERT INTO subscribe(fromUserId,toUserId,createDate) VALUES(:fromUserId,:toUserId,now())",nativeQuery = true)
    void mSubscribe(int fromUserId, int toUserId);

    @Modifying
    @Query(value = "DELETE FROM subscribe WHERE fromUserId=:fromUserId AND toUserId=:toUserId",nativeQuery = true)
    void mUnSubscribe(int fromUserId, int toUserId);
}
```
객체로 반환해도 되지만 native쿼리를 직접 작성하는게 빠르다고 한다.  :fromUserId 에서 **:** 표시는 아래 변수를 사용한다는 뜻이다.  

그리고 서비스를 생성한다
```java
@RequiredArgsConstructor
@Service
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;

    @Transactional
    public void 구독하기(int fromUserId, int toUserId){
        subscribeRepository.mSubscribe(fromUserId,toUserId);
    }
    @Transactional
    public void 구독취소(int fromUserId, int toUserId){
        subscribeRepository.mUnSubscribe(fromUserId,toUserId);
    }
}
```
서비스 메서드를 실행하면 리포지토리의 쿼리문이 실행되어 구독,구독취소가 처리된다.  
마지막으로 요청을 받는 컨트롤러를 생성한다.  

```java
@RestController
@RequiredArgsConstructor
public class SubscribeApiController {

    private final SubscribeService subscribeService;

    @PostMapping("/api/subscribe/{toUserId}")
    public ResponseEntity<?> subscribe(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable int toUserId){
        subscribeService.구독하기(principalDetails.getUser().getUserId(),toUserId);
        return new ResponseEntity<>(new CMResponseDto<>(1,"구독성공",null), HttpStatus.OK);
    }
    @DeleteMapping("/api/subscribe/{toUserId}")
    public ResponseEntity<?> unSubscribe(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable int toUserId){
        subscribeService.구독취소(principalDetails.getUser().getUserId(),toUserId);
        return new ResponseEntity<>(new CMResponseDto<>(1,"구독취소",null), HttpStatus.OK);
    }
}
```

이렇게하면 구독과 구독 취소는 구현이 되었는데  
같은 구독을 요청했을때의 예외처리를 해줘야한다.  
service의 메서드를 try,catch 문으로 받아서 예외처리해주면 된다.