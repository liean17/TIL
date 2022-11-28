# 기타

## 코틀린의 이모저모
- Type Alias, as import  
typealias : 긴 이름의 클래스 혹은 타입을 축약하거나 다른 이름을 붙이고 싶을때.  
as import : 어떤 클래스나 함수를 임포트 할 때 이름을 바꾸는 기능
```kotlin
typealias FruitFilter = (Fruit) -> Boolean
typealias SSGSSMap = Map<String, SuperSaiyajinGodSuperSaiyajin>

import com.practice.a.hello as printHelloA
import com.practice.b.hello as printHelloB


```

- 구조분해와 componentN   
data 클래스가 componentN이라는 함수를 만들어준다.  
아래처럼 변수를 편하게 초기화 하는 방법이 구조분해이다.  
data 클래스가 아니라도 componentN함수를 구현해서 사용할 수 있다.  

```kotlin
data class Person(
    val name: String,
    val age: Int
)
//data 클래스가 아니라면
{
    operator fun component1(): String{
        return this.name
    }
    operator fun component2(): String{
        return this.age
    }
}

fun main(){
    val person = Person("홍길동",1000)
    val (name, age) = person
    println("이름 : ${name}, 나이 : ${age}")
}
```
- Label  
라벨을 명시함으로써 return, break, continue에 특정 조건을 부여할 수 있다.  
```kotlin
loop@ for (i in 1..100){
    for(j in 1..100){
        if(j == 2){
            break@loop
        }
    }
}
```

- TakeIf, TakeUnless  

    - takeIf : 조건을 만족하면 그 값, 아니면 null 반환
    - takeUnless : 주어진 조건을 만족하지 않으면 그 값, 맞으면 null

```kotlin
fun getNumberOrNull(): Int? {
    return if(number<=0){
        null
    }else{
        number
    }
}   
fun getNumberOrNull(): Int? {
    return number.takeIf{it>0}
}  
```

---

## Scope function

- scope function  
일시적인 영역을 형성하는 함수  
    - let : 람다를 받아, 람다 결과를 반환
```kotlin
fun printPerson(person: Person?){
    person?.let{
        println(it.name)
        println(it.age)
    }

    //if(person!=null){
    //    println(person.name)
    //    println(person.age)
    //}
}
```

- 가독성  
    - 보기에 간단해보일지 몰라도 보기좋은 코드는 아니다
    - 디버깅이 어렵고 수정도 어렵다