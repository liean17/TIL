# 코틀린에서의 변수, 타입, 연산자

### 변수선언
```java
long number1 = 10L;
final long number2 = 10L;

Long number3 = 10L;
Person person = new Person("KDK");
```
```kotlin
//1
var number1: Long = 10L
//2
val number2 = 10L

//3
var number3: Long = 10L
//4
var number4: Long? = 10L
//5
val person = Person("KDK")
```
1. 변경 가능한 변수는 var(variable), 변경 불가능한 변수는 val(value)를 사용한다. 타입을 명시하지 않아도  컴파일러가 추론해줘서 생략이 가능하다.  
2. val은 초기화 한 뒤에 변경이 불가능하지만 초기화 하지않고 먼저 선언한 경우 최초 한번에 한해 변수를 선언할 수 있다.
    > 관련 팁  
    - 변수는 우선적으로 val로 만들고 필요한 경우 var로 변경하는 것이 좋다.  
3. 숫자,불리언,문자열 타입에서 기본형과 참조형을 입력으로 구분하지 않는다. 컴파일 과정에서  알아서 연산한다.  
4. null 값을 입력할 수 있는 변수를 만들때는 타입 뒤에 ?물음표 를 붙여줘야한다.
5. 객체를 인스턴스화 할때 new 를 붙이지 않는다.  

---

### Null

- null 체크  
자바에서 처리방법 : null일때 예외 발생시키기, null 반환하기, null일 경우에만 값을 출력하기 
```java
public boolean startsWithA(String str){
    return str.startsWith("A");
}
```  

코틀린에서 처리방법 : 

```kotlin
fun startsWithA1(str: String?): Boolean{
    return str?.startsWith("A") 
    :? throw IllegalArgumentException()
}
```
코틀린은 null이 가능한 타입을 완전히 다르게 취급한다.  
***1*** 에서 ?로 null이 가능함을 선언했기 때문에 null일 경우에 대한 처리를 해주지 않으면 컴파일 오류가 발생한다.  
  
- Safe Call, Elvis  
```kotlin
//1
val str1: String? = "ABC"
str.length//에러
str?.length

val str2: String? = "ABC"
//2
str?.length ?: 0
```
1. Safe Call : null이 가능한 변수를 호출할때 변수명에 ?를 붙이지 않으면 컴파일에러가 발생한다.  
2. Elvis : 앞의 연산 결과가 null이면 뒤의 값을 사용

- 널이 아님 단언  
nullable타입이지만 더이상 null일 수가 없는 경우  
```kotlin
val str2: String? = "ABC"
str!!.length ?: 0
```
변수명 뒤에 !!를 붙이면 된다.  
혹시나 null이 들어오면 NPE를 출력한다.  

- 플랫폼 타입  
: 다른 언어를 사용해서 kotiln이 null 여부를 알 수 없는 타입  
코틀린은 java 코드에 있는 @Nullable,@NotNull 과 같은 어노테이션을 이해해서 컴파일에러를 보여준다.  
하지만 어노테이션이 없거나 지원하지 않는 어노테이션을 사용하는 경우에는 null값이 입력되어도 컴파일단계에서 잡아낼 수 없기 때문에 주의가 필요하다.  

---

### 타입

- 기본타입  
    - 코틀린에서는 선언된 기본값(1,1L,1.0,1.0f)을 보고 타입을 추론한다.  
    - java와 달리 큰 타입으로의 변환을 명시적으로 변경해야한다.  
    ```kotlin
    val number1 = 3
    val number2: Long = number1.toLong()

    println(number1+number2)
    ```

- 기본 타입 캐스팅  
```kotlin
fun printAgeIfPerson(obj: Any){
    if(obj is Person){
        val person = obj as Person
        println(person.age)
    }
    
}
```
(!)is : (!)InstanceOf
as(?) : (Person)  
코틀린에서 위 경우에는 if문에서 이미 타입 체크를 해줬기 때문에 obj.age 역시 동작한다.  
is 앞에 !를 붙이면 not이라는 의미다.  
as뒤에 ?를 붙이면 null 여부를 판단하게 된다.  

- Kotlin 만의 3가지 타입  
    1. Any : java에서 Object 역할, 기본형의 최상위 타입도 Any이다. null을 포함하지 않기때문에 null을 포함하는 최상위 타입은 Any? 이다.
    2. Unit : java의 void와 동일한 역할.  
    java에서 void에서 제네릭을 사용하려면 Void를 사용해야하지만 Unit은 그 자체로 타입 인자가 될 수 있다.  
    3. Nothing : 함수가 정상적으로 끝나지 않았다는 사실을 표현한다.  
    무조건 예외를 반환하거나 무한 루프 함수에 사용한다.  


- String Interpolation, String indexing  
```java
Person person = new Person("KDK",100);
String log = String.format("나는 %s이고 나이는 %s살 이다.",person.getName(),person.getAge());
```
```kotlin
val person = Person("KDK",100)
val hello = "안녕하다"
val log = "나는 ${person.name}이고 나이는 ${person.age}살이다 $hello"
```
그냥 변수를 사용하는 경우 중괄호를 생략할 수 있다
    
---

```kotlin
val str = """
대신귀
여운알
파카를
드리겠
습니다.
""".trimIndent()
```
"""따옴표 3개를 사용해서 문자열 띄어쓰기를 표현할 수 있다.  

---
```kotlin
val str = "ABC"
println(str[1]) //B
```
문자열의 특정 문자를 가져올때 위와 같이 바로 가져올 수 있다.  

---

### 연산자
- 단항 연산자/ 산술 연산자  
java와 동일하다  
: ++ -- += -= 등

- 비교 연산자와 동등성, 동일성  
java와 동일한데  
객체간 비교에서 비교 연산자를 사용하면 compareTo 메서드를 사용해서 비교해준다.  
    - 동등성(Equality) : 두 객체의 값이 같은가  
    java(.equals), kotlin(==) 

    - 동일성(Identity) : 완전히 동일한 객체인가  
    java(==), kotlin(===)

- 논리 연산자/ 코틀린 만의 연산자  
논리 연산자는 java와 동일하다(&& || !)  
    - in / !in 
    컬렉션이나 범위에 포함되어 있다/포함되어 있지 않다
    - a..b  
    a부터 b까지의 범위 객체를 생성한다.
    ```java
    if(0<= score && score <= 100) return "A";
    ```
    ```kotlin
    if(score in 0..100) return "A"
    ```
 
- 연산자 오버로딩  
```kotlin
operator fun plus(other: Money):Money{
    return Money(this.amount + other.amount)
}
```
객체간 연산도 연산자를 오버로딩하면 연산자를 사용해서 계산할 수 있다.  
