# 구독하기 뷰 렌더링
구독여부와 구독 수 정보를 프로필 페이지에 반영시킨다.  

## Dto에 전달하기
다른 정보와 마찬가지로 UserProfileDto에 정보를 전달할것이기 때문에  
boolean타입인 구독정보와 int 타입인 구독자 수 변수를 추가한다.  
```java
public class UserProfileDto {

    private boolean pageOwner;
    private User user;
    private int imageCount;

    private boolean subscribeState;
    private int subscribeCount;
}
```
그리고 해당 정보를 가져오는것은 리포지토리에서 쿼리로 직접 구현한다.
```java
@Query(value = "SELECT Count(*) FROM subscribe WHERE fromUserId=:principalId AND toUserId=:pageUserId",nativeQuery = true)
int mSubscribeState(int principalId,int pageUserId);

@Query(value = "SELECT COUNT(*) FROM subscribe WHERE fromUserId=:pageUserId",nativeQuery = true)
int mSubscribeCount(int pageUserId);
```

## 유저의 구독 상세정보 보기  
특정 유저가 구독하고있는 사람의 정보를 가져와야한다  
```java
@Transactional
public List<SubscribeDto> 구독리스트(int userId, int pageUserId) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT u.id,u.username,u.profileImageUrl, ");
    sb.append("if ((SELECT 1 FROM subscribe WHERE fromUserId=1 AND toUserId=u.id),1,0) subscribeState, ");
    sb.append("if ((1=u.id),1,0) equalUserState ");
    sb.append("FROM user u INNER JOIN subscribe s ");
    sb.append("ON u.id=s.toUserid ");
    sb.append("WHERE s.fromUserId=2");

    Query query = em.createNativeQuery(sb.toString())
            .setParameter(1,userId)
            .setParameter(2,userId)
            .setParameter(3,pageUserId);

    //쿼리 실행(qlrm 라이브러리 필요 = Dto에 DB결과를 매핑하기 위해서)
    JpaResultMapper result = new JpaResultMapper();
    List<SubscribeDto> subscribeDto =  result.list(query,SubscribeDto.class);
    return subscribeDto;
}
```
구독 유저에 대한 일부 정보(이름, 사진)과 자신인지 아닌지에 대한 것도 구별이 필요하다.  

