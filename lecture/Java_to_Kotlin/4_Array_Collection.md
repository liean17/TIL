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

---

## 다양한 함수
- 확장함수  
외부에서 생성되었지만 특정 클래스안에서 실행하는 함수  
Java와의 호환을 목적으로 만들어졌다.  
```kotlin
fun String.lastChar(): Char{
    return this[this.length - 1]
}
```
확장하려는클래스. 을 통해 접근 가능  
확장함수는 클래스 내의 private, protected 멤버를 가져올 수 없다.  
또한 멤버함수와 동일한 확장함수를 만들면 멤버함수가 우선적으로 호출된다.  
반대로 확장함수가 먼저 만들어졌다면 오류가 발생  
그리고 확장함수는 현재 타입을 기준으로 호출된다.  
자바에서는 정적 메소드 처럼 사용가능하다.  

- infix중위함수  
downTo, step 등  
. () 를 생략하고 사용할 수 있는 함수
- inline  
함수를 호출하는 대신. 함수를 호출한 지점에 함수 본문을 입력하고 싶은 경우  
함수를 파라미터로 전달할때 오버헤드를 줄이는 용도로 사용된다.  
- 지역함수  
함수 안에 선언하는 함수  
```kotlin
fun createPerson(firstName: String, lastName: String): Person{
    fun validateName(name: String, fieldName: String){
        if(name.isEmpty()){
            throw IllegalArgumentException("${fieldName}"은 비어있을 수 없습니다. 현재 값 : $name)
        }
    }
    validateName(firstName,"firstName")
    validateName(lastName,"lastName")

    return Person(firstName,lastName,1)
}
```
