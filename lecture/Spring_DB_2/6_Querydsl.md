# Querydsl

### 기존 방식의 문제점
sql은 문자라서 컴파일오류처럼 쉽게 틀린것을 찾아낼 수 없다.  
따라서 작성할때 바로 오류를 알아차릴 수 있는 기술이 필요했는데  
이를 위해 Criteria API라는 것이 개발되었다.  
이를 이용해서 자바코드 쓰듯 SQL문을 작성할 수 있었는데 문제는 너무너무 복잡하다는 것이다.  

### Querydsl
Domain Specific Language  
JPA, MongoDb, SQL 같은 기술들을 위해 type-safe SQL을 만드는 프레임워크  
JPQL을 만들어주는 빌더역할을 한다.  

### 설정
의존성을 추가하고 빌드를 한번 하면 QItem이라는 객체가 생성된다.  

### Querydsl을 적용한 Repository
```java
@Repository
@Transactional
public class JpaItemRepositoryV3 implements ItemRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public JpaItemRepositoryV3(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {

        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        return query
                .select(item)
                .from(item)
                .where(likeItemName(itemName),maxPrice(maxPrice))
                .fetch();
    }

    private Predicate maxPrice(Integer maxPrice) {
        if(maxPrice!=null){
            item.price.loe(maxPrice);
        }
        return null;
    }

    private BooleanExpression likeItemName(String itemName){
        if(StringUtils.hasText(itemName)){
            return item.itemName.like("%"+itemName+"%");
        }
        return null;
    }


    //리팩토링 이전
    public List<Item> findAllOld(ItemSearchCond cond) {

        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        BooleanBuilder builder = new BooleanBuilder();
        if(StringUtils.hasText(itemName)){
            builder.and(item.itemName.like("%"+itemName+"%"));
        }
        if(maxPrice!=null){
            builder.and(item.price.loe(maxPrice));
        }

        List<Item> result = query.select(item)
                .from(item)
                .where(builder)
                .fetch();

        return result;
    }
}
```
우선 JPQL팩토리가 필요하다. querydsl은 jpql을 편하게 작성해주는 매퍼의 역할을 하기 때문이다.  
동적쿼리가 매우 단순해졌는데 BooleanBuilder를 통해 조건을 쉽게 추가할 수 있고  
밖으로 빼내면 다른 곳에서도 활용이 가능해진다.  
이로써 쿼리작성에 오류가 발생해도 컴파일오류가 발생하게된다.