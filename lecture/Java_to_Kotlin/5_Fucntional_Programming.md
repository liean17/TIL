# 함수형 프로그래밍

## 람다
- 코틀린에서 람다  
코틀린은 자바와 다르게 함수를 변수에 할당하거나 파라미터로 넘기는 것이 가능하다.  
    - 아래 함수역시 타입이 존재하며 함수의 타입은 ```(파라미터 타입) -> 반환타입```이 된다.  
    - fruit -> fruit.name 이라는 코드는  
it.name 으로 간소화할 수 있다.
    - 람다는 여러줄로 작성할 수 있으며 가장 마지막 줄을 반환하는 것으로 인식한다.  

```kotlin
val isApple = fun(fruit: Fruit): Boolean{
    return fruit.name == "사과"
}

val isApple2 = {fruit: Fruit -> fruit.name == "사과"}

//호출방법
isApple(fruit)
isApple.invoke(fruit)
```

- Closure  
자바에서는 람다에서 외부 변수를 사용할때는 final 혹은 변하지않은 실질적인 final인 변수만 사용할 수 있다.  
코틀린에서는 람다 사용시 람다에서 사용하는 모든 변수의 정보를 가지게되는데 이러한 구조를 Closure라고 한다.  

---

## 컬렉션의 함수형  
- filter와 map  
    - mapNotNull : null이 아닌것만 가져오기
    - filterIndexed
    - mapIndexed
```kotlin
val applePrices = fruits.filter {it.name == "사과"} .map {it.currentPrice}
```
- 컬렉션 기능들  
    - all : 조건을 모두 만족하면 true
    - none : 조건을 모두 불만족하면 true
    - any : 조건을 하나라도 만족하면 true
    - count : 갯수
    - sortedBy : 정렬
    - distinctBy : 변형된 값을 기준으로 중복을 제거
    - first(last) : 첫번째 값을 가져오기
    - firstOrNull(lastOrNull) : 첫번째 값 혹은 null값 가져오기
    
- List -> Map  
    - groupBy{fruit -> fruit.name} : 과일이름이 key이며 해당 과일들이 모인 리스트가 값인 Map
    - groupBy({fruit -> fruit.name},{fruit->fruit.price}) : 키와 값 설정
    - associateBy : 중복되지 않는 키를 가지고 Map을 만들때
- 중첩된 컬렉션 처리  
    - flatMap : 리스트를 단일 리스트로
    - flatten : 중첩 리스트를 해제해서 하나의 리스트로