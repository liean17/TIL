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