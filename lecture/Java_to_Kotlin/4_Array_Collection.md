# 함수형 프로그래밍

## 배열과 컬렉션
- 배열  
```kotlin
val array = arrayOf(100,200)
for(i in array.indices){
    println("${i} 출력")
}
for((idx, value) in array.withIndex()){
    println("${idx}" "${value}")
}
array.plus(300)
```

- 컬렉션  
불변, 가변을 지정해줘야한다
    - 가변 컬렉션 : 컬렉션에 요소 추가 삭제 가능
    - 불변 컬렉션 : 컬렉션에 요소 추가, 삭제 불가(수정은 가능)  
> 불변 리스트를 기본으로 사용하고, 필요하면 가변 리스트를 사용하는 것이 좋다.    
```kotlin

/*LIST*/
val numbers = listOf(100,200)//불변
val mnumbers. = mutableListOf(100,200)//가변
mnumbers.add(300)

val emptyList = emptyList<Int>() //타입 지정 필수

numbers.get(0)
numbers[0]

for(number in numbers){
    println(number)
}
for((idx, value) in numbers.withIndex()){
    println("$idx $value")
}


/*SET*/ //리스트와 동일
mutableSetOf(100L,200L)


/*MAP*/
val oldMap = mutableMapOf<Int,String>()
oldMap[1] = "MONDAY"

mapOf(1 to "MONDAY", 2 to "TUESDAY")

for(key in oldMap.keys){
    println(key)
    println(oldMap[key])
}
for((key,value) in oldMap.entries){
    println(key)
    println(value)
}
```    

- null  
```kotlin
//차이를 잘 구별해야한다.

List<Int?>

List<Int>?

List<Int?>?
```

- 자바와 사용  
가변, 불변을 구분하지 않는 자바와 사용할때는  
Collections.unmodifiableMap()과 같은 메서드를 활용해야한다.  