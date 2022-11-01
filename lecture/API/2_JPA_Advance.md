# API 개발 고급

## X ToOne 관계

### 1. V1 엔티티 직접노출
- 무한참조 오류 발생
- LAZY 전략 사용시 프록시 출력오류
>HibernateModule을 사용해서 해결가능  
하지만 애초에 엔티티 반환을 하지 않으면 된다.  

### 2. V2 DTO 출력
- 무한참조가 해결되었고, 필요없는 값은 출력하지않는다.
- LAZY 로딩으로 인한 쿼리 최적화는 여전히 문제(1+N 문제)
    - Entity에서 Dto로 변환되는 순간에 프록시였던 객체가 초기화된다.
    - 여러 결과가 조회되어야한다고 하면, 각 조회객체에 대한 조회쿼리가 추가로 더 실행된다.
    - EAGER 로딩을 한다고 해서 해결되는 것은 아니다.

### 3. V3 Fetch join 사용
- 직접 jpql 작성
    ```
    "select o from Order o" +  
    "join fetch o.member m" +  
    "join fetch o.delivery d"
    ```
- LAZY를 무시하고 진짜 객체를 한번에 가져오는 것  
> **대부분의 성능 문제가 조회에서 N + 1이 발생한다.**   
이를 해결하는 방법이 fetch join이며 fetch join이 필요한 테이블에 정확하게 사용하는 것이 중요하다.  

### 4. V4 JPA에서 DTO를 바로 조회
- 직접 jpql 작성
    ```
    "select new jpabook.jpashop.repository.OrderDto(o.id,m.name,o.orderDate...) + 
    "from Order o" +  
    "join o.member m" +  
    "join o.delivery d"
    ```
    그리고 반환타입을 DTO로 정한다.
- 성능은 좋으나, 재사용성이 거의 없다.  
그리고 대부분의 경우 성능차이가 거의 없다.

### 정리
- Entity는 Dto로 변환하는 방법을 선택한다.
- 필요하면 페치 조인으로 성능을 최적화 한다. -> 여기서 대부분 해결된다.
- 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
- 그래도 안되면 JPA에서 제공되는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다.

---

## OneToMany 관계
- 결과가 많은 만큼 최적화에서 고려할 사항이 많다.

### V1 엔티티 직접 노출
- 마찬가지로 무한참조, 사용하지않는 데이터 반환 문제가 있다

### V2 Dto
- dto에서 반환해야할 엔티티 역시 dto로 변환해줘야한다.
- 연관관계 만큼의 많은 쿼리가 나간다.

### V3 Fetch join
- jpql작성
    ```
    "select o from Order o" +  
    "join o.member m" +  
    "join o.delivery d" + 
    "join fetch o.orderItems oi" +
    "join fetch oi.item i"
    ```
    orderitem과 join 과정에서 order가 orderitem갯수만큼 증가하는 문제가 발생한다.  
    따라서 select 뒤에 "distinct"를 추가해줘야한다. -> JPA에서 객체의 참조값도 비교해서 중복을 제거해준다.  

- 컬렉션 페치 조인은 페이징이 불가능하다.  
정확하게는 메모리에서 페이징처리를 해버린다. 일대다 에서 다를 기준으로 row가 생성되기 때문이다.

- 컬렉션 둘 이상에 페치 조인을 사용하면 데이터가 부정합하게 조회될 수 있다.  

### V3.1 페이징과 한계돌파
- 먼저 ToOne 관계는 모두 페치 조인한다.
- 컬렉션은 지연 로딩으로 조회한다.
- 지연 로딩 성능 최적화를 위해 @BatchSize를 적용한다.(default_batch_fetch_size : 인쿼리 갯수)  
in 쿼리로 일정 갯수를 한번에 조회해준다.  
    - OrderItem안의 Item도 in쿼리 적용이 되어  
    1 + N + N 조회를 1 + 1 + 1로 변경해주기때문에 상당한 성능 최적화가 된다.
    - 최대 1000까지 가능하며 100에서 1000까지 사이즈를 적절하게 선택하는 것이 좋다.  

### V4 컬렉션 DTO 반환
- jpql을 작성해서 가져오되 일대다 에서 다 부분인 컬렉션은 추가할 수 없다.  
따라서 일단 조회 후 반복문을 통해서 따로 쿼리를 날려서 조회 후 set해줘야한다.  
- 즉 루트는 1번 실행되지만 컬렉션에서 N번 실행하게 된다.

### V5 컬렉션 DTO 최적화
- in 절로 id리스트를 받아오는 방법
    - id 리스트는 반복문으로 리스트화해오면된다.  
    - 이렇게하면 같은 Order가 반환된다
- Map을 사용해서 메모리에서 매칭을 해주는 방법
    ```java
    /**
    * 최적화
    * Query: 루트 1번, 컬렉션 1번
    * 데이터를 한꺼번에 처리할 때 많이 사용하는 방식 *
    */
    public List<OrderQueryDto> findAllByDto_optimization() { //루트 조회(toOne 코드를 모두 한번에 조회)
          List<OrderQueryDto> result = findOrders();
    //orderItem 컬렉션을 MAP 한방에 조회
          Map<Long, List<OrderItemQueryDto>> orderItemMap =
      findOrderItemMap(toOrderIds(result));
    //루프를 돌면서 컬렉션 추가(추가 쿼리 실행X)
    result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
          return result;
      }
      private List<Long> toOrderIds(List<OrderQueryDto> result) {
          return result.stream()
                  .map(o -> o.getOrderId())
                  .collect(Collectors.toList());
    }
      private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long>
      orderIds) {
          List<OrderItemQueryDto> orderItems = em.createQuery(
                  "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                      " from OrderItem oi" +
                      " join oi.item i" +
                      " where oi.order.id in :orderIds", OrderItemQueryDto.class)
              .setParameter("orderIds", orderIds)
              .getResultList();
      return orderItems.stream()
              .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }
    ```


