# 코틀린에서 OOP

## 클래스
- 클래스와 프로퍼티  
```java
public class JavaPerson{
    private final String name;
    private int age;

    public JavaPerson(String name, int age){
        if(age < 0){
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.age = age;
    }

    public String getName(){
        return name;
    }

    public int getAge(){
        return age;
    }

    public void setAge(int age){
        this.age = age;
    }

    public JavaPerson(String name){
        this.name = name;
        this.age = 1;
    }
}
```
```kotlin
//주 생성자 - 파라미터가있으면 반드시 존재해야한다.
class Person(
    val name: String,
    var age: nt
){
    init{
        if(age<0){
            throw IllegalArgumentException()
        }
    }
    //부 생성자 - 최종적으로 주 생성자를 호출한다
    constructor(name: String): this(name, 1)
}
```
생성자는 클래스 이름 옆에 프로퍼티를 작성함으로써 만들 수 있다.  
constructor를 붙여주면 되는데 생략도 가능하다.  
내부의 프로퍼티도 생성자에서 선언할 수 있다.  
getter와 setter는 자동으로 만들어진다.  
init 블록은 생성자호출시 최초로 1번 실행되는 블럭이다.  
추가 생성자(부생성자)가 필요하면 constructor()를 사용한다.  
    - 하지만 코틀린에서는 default parameter를 추천한다.  

### 커스텀 getter, setter
```kotlin
//함수로 만드는 방법
fun isAdult(): Boolean{
    return this.age >= 20
}
//프로퍼티 처럼 만드는 방법
val isAdult: Boolean
    get() = this.age >=20
```
```kotlin
class Person(
    name: String,
    var age: nt
){
    //파라미터 값을 대문자로 반환하는 함수(getter)
    val name = name
      //field는 무한루프를 막기 위한 예약어
      get() = field.uppercase() 
```
```kotlin
var name = name
  set(value){
    field = value.uppercase()
  }
```
코틀린은 getter,setter를 지양해서 잘 쓰진 않는다.

---
## 상속
- 추상 클래스  
```kotlin
abstract class Animal(
    protected val species: String,
    //1
    protected open val legCount: Int,
){
    abstract fun move()

}
```
1. 추상 프로퍼티가 아니라면, 프로퍼티를 override를 하려면 open 키워드를 적어야한다.  
```kotlin
class Cat(
    species: String
    //1
) : Animal(species, 4) {
  //2  
  override fun move(){
    println("하하")
  }
}
```
1. 상속을 표현할때는 타입처럼 : 콜론을 사용한다. 생성자도 함께 적어준다.
2. override라는 지시어가 존재한다.
```kotlin
class Penguin(
    species: String
) : Animal(species, 2) {

  private val wingCount: Int = 2

  override fun move(){
    println("팽팽")
  }

  override val legCount: Int
    get() = super.legCount + this.wingCount
}
```
- 인터페이스  
```kotlin
interface Flyable{
    fun act(){
        println("난다요")
    }
}
```
```kotlin
interface Swimable{

    val swimAbility: Int

    fun act(){
        println("수영한다요")
    }
}
```
```kotlin
class Penguin(
    species: String
) : Animal(species, 2), Swimable, Flyable {

  override fun move(){
    println("팽팽")
  }

  override val legCount: Int
    get() = super.legCount + this.wingCount
}
  override fun act(){
    super<Swimable>.act()
    super<Flyable>.act()
  }
```
- 주의점
```kotlin
fun main(){
    Derived()
}

open class Base(
    open val number: Int = 100
){
    init{
        println("Base class")
        println(number)
    }
}

class Derived(
    override val number:Int
) : Base(number){
    init{
        println("Derived Class")
    }
}
```
위 Drived()의 결과는 0이 나온다.  
상위 클래스의 init이 먼저 실행되는데 출력해야하는 number값은 Derived에서 입력된 값을 출력해야해서 초기화 되지 않은 채 값이 출력된 것이다.  
따라서 하위 클래스에서 override하고있는 프로퍼티를 불러와서는 안된다.  
즉 상위 클래스를 설계할 때 생성자 또는 초기화 블록에 사용되는 프로퍼티에는 open을 피해야 한다.  

- 관련 지시어
    - final  
    override를 할 수 없게 한다.
    - open  
    override를 열어준다
    - abstract  
    반드시 override 해야 한다.
    - override  
    상위 타입을 오버라이드 하고 있다.

---

## 접근 제어

- 자바와 차이  
    - public : 동일
    - **protected** : 자바에서는 같은 패키지 또는 하위 클래스에서 접근 가능한데  
    코틀린 에서는 '선언된 클래스' 혹은 하위 클래스에서만 접근이 가능하다.
    - default : 코틀린에서는 삭제
    - **internal** : 같은 모듈에서만 접근 가능
    - private : 동일

- 코틀린 파일의 접근 제어  
코틀린 파일 kt에는 클래스, 변수, 함수를 한번에 만들 수 있다.  
이러한 요소들에 접근 제어자가 붙는 경우
    - public 어디서든 접근할 수 있다
    - protected 사용이 불가능하다
    - internal 같은 모듈
    - private 같은 파일

- 다양한 구성요소의 접근 제어
    - 생성자 : 생성자에 접근 지시어를 붙이려면 constructor를 생략하지 않고 써야한다.
    - 프로퍼티 : val 혹은 var옆에 작성하면 되는데  
    getter는 열어두고 setter는 닫고싶은 경우  
    ```kotlin
    class Car(
        internal val name : String,
        private var owner: String,
        _price: Int
    ){
        //setter에만 private 적용
        var price = _price
          private set
    }
    ```
- 자바와 함께 사용할 경우
    - 코틀린의 internal은 자바에서는 public으로 인식한다.  
    - 자바는 protected를 통해 같은 패키지까지 접근할 수 있다.  

---

## Object 키워드  
- static 함수, 변수
```kotlin
class Person private constructor(
    var name: String,
    var age: Int
){
    //static 객체를 넣는 공간
    companion object Factory : Log{
        //const 컴파일 시 변수를 할당하는 키워드
        const private val MIN_AGE = 0
        fun newBaby(name: String):Person{
            return Person(name, MIN_AGE)
        }
    }
}
```
static : 클래스가 인스턴스화 될 때 새로운 값이 복제되는게 아니라, 정적으로 인스턴스끼리 값을 공유
companion object : 클래스와 동행하는 유일한 오브젝트. 하나의 객체로 간주되어 이름을 붙일 수도 있고, interface를 구현할 수도 있다.
> 유틸성 함수를 사용할때는 companion object보다 최상단에 따로 구현해두는게 편하다. 
> java에서 사용하는 경우, companion혹은 이름을 명시하거나 @JvmStatic 어노테이션을 사용하면 된다.
- 싱글톤
```kotlin
object SingletonObject
```
끝  
하지만 자바에서 처럼 서버개발시 직접 사용할 일은 거의 없다
- 익명 클래스  
```kotlin
fun main(){
    //object = 익명클래스 사용
    moveSomething(object : Movable{
        override fun move(){
            println("무브")
        }
    })
}
private fun moveSomething(movable: Movable){
    movable.move()
}
```

---

## 중첩 클래스
- 내부 클래스   
    - 내부 클래스는 숨겨진 외부 클래스 정보를 가지고 있어서 참조를 해지하지 못하는 경우, 메모리 누수가 생길 수 있고, 이를 디버깅 하기 어렵다.
    - 내부 클래스의 직렬화 형태가 명확하게 정의되지 않아 직렬화에 있어 제한이 있다. 
    - 자바에서는 클래스 안에 클래스를 만들때 static 클래스를 사용하는것이 좋다.
```kotlin
class JavaHouse(
    val address: String,
    val livingRoom: LivingRoom
){
    class LivingRoom(
        private var area: Double
    )
}
```
코틀린에서는 이를 지켜서 그냥 class만 작성하면 된다.  
 
---

## 코틀린의 클래스

- Data Class  
자바에서 dto를 만들때 IDE나 Lombok을 활용하면 되지만  
그렇지 않거나 추가적인 처리가 필요하면 코드가 길어진다.  

```kotlin
data class PersonDto(
    val name: String,
    val age: Int
)
```
data 키워드를 앞에 넣으면 equals, hashcode, toString 메서드를 자동으로 만들어준다.  

- Enum Class  
```kotlin
enum class Country(
    private val code: String
){
    KOREA("KO"),
    AMERICA("US")
    ;
}
```
```kotlin
fun handleCountry(country: Country){
  when (country) {
    Country.KOREA -> TODO()
    Country.AMERICA -> TODO()
  }
}
enum class Country(
    private val code: String
){
    KOREA("KO"),
    AMERICA("US")
    ;
}
```
enum에 대한 분기처리를 할 때 컴파일러 단에서 놓친 부분을 체크할 수 있다.  


- Sealed Class, Sealed Interface  
외부에서는 상속받을 수 없는 클래스  
컴파일 타임 때 하위 클래스의 타입을 모두 기억한다.  
즉, 런타임때 클래스 타입이 추가될 수 없다.  

    - abstract class와 차이는 구현체가 같은 패키지에 있어야 하는가이다.  
    
