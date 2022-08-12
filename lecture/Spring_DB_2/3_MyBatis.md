# MyBatis

### 소개
MyBatis는 xml파일을 사용해서 설정한다.  
sql문을 직접 작성하지않기 때문에 오타로인한 오류방지가 가능하다.  
또한 동적쿼리역시 JDBC Template에 비하면 설정이 쉬운 편이다.  

### 설정
```properties
#MyBatis

#마이바티스에서 반드시 적어야하는 타입 앞의 패키지를 생략할 수 있다.
mybatis.type-aliases-package=hello.itemservice.domain
#언더바 에서 카멜케이스 문법으로 자동 변경해준다
mybatis.configuration.map-underscore-to-camel-case=true
```

---
### 적용
마이바티스는 매핑 XML을 호출하기 위한 매퍼 인터페이스를 정의해야한다.  
매퍼 인터페이스에는 @Mapper 어노테이션이 붙어야 MyBatis에서 인식한다.  
```java
@Mapper
public interface ItemMapper {

    void save(Item item);

    void update(@Param("id") Long id, @Param("updateParam")ItemUpdateDto updateDto);

    List<Item> findAll(ItemSearchCond itemSearch);

    Optional<Item> findById(Long id);
}
```
이 인터페이스의 메서드를 호출하면 xml의 해당 sql을 실행하고 결과를 돌려준다.  

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="hello.itemservice.repository.mybatis.ItemMapper">

    <insert id="save" useGeneratedKeys="true" keyProperty="id">
        insert into item (item_name,price,quantity)
        values (#{itemName},#{price},#{quantity})
    </insert>

    <update id="update">
        update item
        set item_name=#{updateParam.itemName},
            price=#{updateParam.price},
            quantity=#{update.Param.quantity}
        where id = #{id}
    </update>

    <select id="findById" resultType="Item">
        select id,item_name, price, quantity
        from item
        where id = #{id}
    </select>

    <select id="findAll" resultType="Item">
        select id, item_name, price, quantity
        from item
        <where>
            <if test="itemName != null and itemName != ''">
                and item_name like concat('%',#{itemName},'%')
            </if>
            <if test="maxPrice != null">
                and price &lt;= #{maxPrice} <!--괄호 사용 불가-->
            </if>
        </where>
    </select>

</mapper>
```
마이바티스 매핑 xml코드다.  
id에는 매퍼 인터페이스에서 설정한 메서드 이름을 지정하고, 파라미터는 #{}문법을 사용한다.  
resources 폴더안에, 매퍼 인터페이스의 위치와 동일한 위치를 만들어 생성해야한다.  
xml의 위치를 수정하는 것도 가능한데  
application.properties에 `mybatis.mapper-locations=classpath:mapper/**/*.xml`라고 설정하면  
resources/mapper를 포함한 그하위 폴더에 있는 XML을 매핑 파일로 인식한다.

### 리포지토리 생성
```java
@Repository
@RequiredArgsConstructor
public class MybatisItemRepository implements ItemRepository {

    private final ItemMapper itemMapper;

    @Override
    public Item save(Item item) {
        itemMapper.save(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        itemMapper.update(itemId, updateParam);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemMapper.findById(id);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        return itemMapper.findAll(cond);
    }
}
```
Mapper가 관련된 로직을 모두 구현하기때문에 리포지토리는 간단하게 만들 수 있다.

### MyBatis의 기능
1. 동적 SQL  
if, choose, trim, foreach와 같은 기능을 제공한다.  
    - if : 해당 조건에 따라 값을 추가할지 판단한다.
    - choose, when, otherwise : 자바의 switch구문과 유사하다
    - trim, where, set : 동적쿼리에서 모든 조건을 만족하지 않는 경우, 구문이 미완성되어 에러가 발생할 수 있다.  
    where태그를 사용하면 문장이 없는 경우 where를 추가하지 않고, and가 먼저 시작되면 and를 지워준다.

2. 어노테이션으로 SQL작성  
@Selete("select id ~~")와 같이 어노테이션으로 SQL을 작성할 수 있다.  

3. 문자열 대체
파라미터 바인딩이 아니라 문자 그대로를 처리하고 싶은 경우 `${}`를 사용하면 된다.  
단 이경우 SQL 인젝션 공격을 당할 수 있는 위험이 있다.  

4. Result Map
결과를 매핑할 때 테이블명과 객체 명이 다를 수 있다. 이럴때는 as 를 사용하면 된다.  
```xml
<select id="selectUsers" resultType="User">
    select
      user_id as "id",
    from some_table
    where id = #{id}
</select>
```
혹은 Result Map을 사용하면 된다.
```xml
<resultMap id="userResultMap" type="User">
    <id property="id" column="user_id" />
    <result property="username" column="username"/>
    <result property="password" column="password"/>
  </resultMap>
  
  <select id="selectUsers" resultMap="userResultMap">
    select user_id, user_name, hashed_password
    from some_table
    where id = #{id}
</select>
```