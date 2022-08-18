# 스프링 데이터 JPA

### Spring Data JPA
인터페이스만 정의함으로써 데이터에대한 모든 코드를 작성하지 않아도 된다.  
따라서 도메인 클래스에 더 집중할 수 있고 비즈니스 로직을 이해하기 쉬워졌다.  

### Repository
```java
public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByItemNameLike(String itemName);
    List<Item> findByPriceLessThanEqual(Integer price);

    //쿼리 메서드(아래 메서드와 같은 기능 수행)
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);

    //쿼리 직접 실행
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);

}
```
이름만 제대로 작성하는 것으로 관련 메서드를 쉽게 만들 수 있다.  
조건이 복잡한 경우 쿼리를 직접 적용시킬수도 있는데  
이 경우에는 @Query어노테이션과 @Param어노테이션이 필수다.