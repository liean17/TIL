# 스프링 JdbcTemplate

## JDBC에서의 repository
```java
@Slf4j
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {

    private final JdbcTemplate template;

    public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name, price, quantity) values(?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            //자동 증가 키
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getItemName());
            ps.setInt(2,item.getPrice());
            ps.setInt(3,item.getQuantity());
            return ps;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    //update 코드 생략

    //findById 코드 생략

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name, price, quantity from item";

        //동적 쿼리 작성..인데 너무 복잡함
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }
        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= ?";
            param.add(maxPrice);
        }
        log.info("sql={}", sql);

        //query는 하나이상 가져올때, queryforobject는 하나만 가져올때
        return template.query(sql, itemRowMapper(), param.toArray());

    }
    //조회 결과를 객체로 변환
    private RowMapper<Item> itemRowMapper() {
        return ((rs,rowNum)->{
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        });
    }
}
```
일단 굉장히 복잡하다  
그마저도 개선된 코드라는게 놀랍다.  
자동으로 id를 증가시키는 코드가 많이 길고, 동적쿼리 코드는 너무 길어서 쉽게 오타를 낼 지경이다.  

### 동적 쿼리 문제
동적 쿼리가 저렇게 긴 이유는 다양한 상황에 따른 모든 쿼리를 적어야했기때문이다.  
상품명과 최대 가격을 기준으로 검색하는데  
1. 둘다 없는 경우(전체조회)
2. 상품명만 검색
3. 최대 가격으로만 검색
4. 상품명과 최대 가격 두가지로 검색  
총 네가지 경우가 존재하게 된다.

### 이름 바인딩
sql 쿼리를 직접 작성할때 문제는 오타이다  
특히 단순한 오타보다 바인딩 순서를 잘못 적으면 큰 문제가 발생한다.  
예를들어 sql문에는 상품이름, 가격, 수량 순인데  
상품이름, 수량, 가격 순으로 데이터를 넣게되면 가격과 수량이 뒤바뀌는 문제가 발생한다.  
이는 코드수정뿐만 아니라 DB의 데이터를 복구해야 해결이 된다.  
이러한 불편함에 NamedParameterJdbcTemplate가 생겼다.  
```java
@Slf4j
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {


    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name, price, quantity) values(:itemName,:price,:quantity)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(item);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(sql,param, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }
    // 생략

    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class);//camel 변환 지원
    }
}
```
물음표가 아니라 직접적으로 이름을 지정해주는 방식으로 바인딩 오류를 예방한다.  
안에서도 다양한 방식이 있는데 BeanPropertySqlParameterSource는 자바빈 규약에 맞게 자동으로 파라미터를 생성해준다.  
단 Dto에 없는 id와 같은 값은 이 방식을 사용할 수 없다.  


---
### 정리
JPA와 같은 ORM기술을 사용하면서 SQL문을 작성해야할때 Jdbc Template를 사용하면 간단하다.  
하지만 동적쿼리를 사용하기 어려운 문제가 있는데 이를 해결할 수 있는게 MyBatis이다.