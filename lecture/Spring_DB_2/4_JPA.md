# JPA

### SQL 중심적인 개발의 문제점
JAVA는 객체지향적 언어지만 기존에는 **객체지향적 언어**를 통해 **관계형 DB**에 데이터를 저장했다.  
따라서 저장을 하거나 조회하려할때 객체 개념을 적용시키려하면 그냥 사용하는 것 보다 더 복잡한 코드를 작성해야했다.  

### JPA
자바 진영의 ORM 기술 표준  
객체는 객체대로 설계하고, 관계경 데이터베이스도 각각 설계하되 ORM이 중간에 매핑해준다.  
이로써 SQL 중심적인 개발에서 객체 중심으로 개발을 도와준다.  
JPA는 애플리케이션과 DB사이에 존재하기 때문에 다음 최적화 기능을 제공할 수 있다.  
    - 1차 캐시를 통해 같은 트랜잭션 안에서는 같은 엔티티를 반환한다.(SQL을 한번만 실행)
    - 트랜잭션을 커밋할 깨 까지 INSERT SQL을 모았다가 한번에 보낼 수 있다.  
    - 객체가 실제 사용될 때만 로딩하는 지연 로딩과, 쿼리 한번에 연관된 객체까지 미리 조회하는 즉시 로딩을 제공한다.

### JPA Repository
```java
@Slf4j
@Repository
@Transactional
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em;

    public JpaItemRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        //jpa는 트랜잭션 커밋 시점에 변경된 엔티티 객체를 확인하여 UPDATE SQL을 실행한다.
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        //엔티티를 대상으로하는 sql
        String jpql = "select i from Item i";

        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }
        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            param.add(itemName);
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
            param.add(maxPrice);
        }
            log.info("jpql={}", jpql);
            TypedQuery<Item> query = em.createQuery(jpql, Item.class);
            if (StringUtils.hasText(itemName)) {
                query.setParameter("itemName", itemName);
            }
            if (maxPrice != null) {
                query.setParameter("maxPrice", maxPrice);
            }
            return query.getResultList();
    }
}
```
가장 큰 특징은 EntityManager 이다.  
JPA의 모든 동작은 엔티티 매니저를 통해서 이루어지고, 엔티티 매니저가 가진 데이터소스를 통해 데이터베이스에 접근할 수 있다.  
엔티티매니저를 통해 컬렉션에 저장하고 빼듯 메서드를 작성할 수 있다.  
하지만 동적 쿼리에는 자유롭지 못하다.  

### JPA 예외 변환
엔티티매니저에서 발생한 JPA와 관련된 예외는 리포지토리로 넘어가는데 이를 제대로 처리하지못하면  
서비스 계층까지 넘어가게된다. 이렇게되면 서비스 계층이 JPA기술에 종속적이게 된다.  
@Repository는 컴포넌트 스캔 대상이 되게 할 뿐만 아니라, 예외 변한 AOP의 적용 대상이 되게 한다.  
