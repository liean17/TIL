# 코틀린에서 코드를 제어하는 방법

### 조건문
- if 문  
```java
private String scoreCheck(int score){
    if(score<0>){
        throw new IllegalArgumentException(String.format("%s는 0보다 작을 수 없습니다",score));
    }else if(score>=50){
        return "PASS";
    }else{
        return "화이팅";
    }
}
```
```kotlin
fun scoreCheck(score: Int) : String{
    return if(score !in 0..100){
        throw IllegalArgumentException("${score}는 0보다 작을 수 없습니다.")
    }else if(score>=50){
        "PASS"
    }else{
        "화이팅"
    }
}
```
- Expression과 Statement  
Statement : 프로그램의 문장, 하나의 값으로 도출되지 않는다.  
Expression : 하나의 값으로 도출되는 문장  
Statement는 Expression을 포함한다.  
```java
int a = 10+20;
```
위에서 10+20은 문장이면서 30이라는 값을 가진다.  

    - 자바에서 if-else는 expression이 아니다. String a = if(true) "Hello"; 와 같은 식으로 출력할 수 없다.  
    3항 연산자를 사용하면 expression의 형태로 사용이 가능하다.
    - 코틀린에서 if-else는 expresion이다.  
    삼항 연산자를 쓸 필요 없이 if문을 반환하면 된다.

- switch와 when
```java
private String getGrade(int score){
    switch(score/10){
        case 9:
            return "A";
        case 8:
            return "B";
        case 7:
            return "C";
        default:
            return "D";            
    }
}
```
```kotlin
fun getGrade(score: Int): String{
    return when(score){
        in 90..99 -> "A"
        in 80..89 -> "B"
        in 70..79 -> "C"
        else -> "D"
    }
}
```
```kotlin
fun judgeNumber(number: Int){
    return when(number){
        1,0,-1 -> println("많이 보던 숫자입니다.")
        else -> "D"
    }
}
```
```kotlin
fun judgeNumber(number: Int){
    return when{
        number == 0 -> println("0 입니다.")
        number % 2 == 0 -> println("짝수입니다.")
        else -> "D"
    }
}
```

---

### 반복문  
- for-each
- for
- Prograssion Range
- while
```java
//1
List<Long> numbers = Arrays.asList(1L, 2L, 3L);
for (Long number : numbers) {
  System.out.println("number = " + number);
}
//2
for (int i = 1; i <= 5; i += 2) {
  System.out.println(i);
}
//3
int i = 1;
while (i <= 3) {
  System.out.println(i);
  i++;
}
```
```kotlin
//1
val numbers = listOf(1L,2L,3L)
for(number in numbers){
    println(number)
}

//2
for (i in 1..3){
    println(i)
}
//2-2 내려가기
for (i in 3 downTo 1){
    println(i)
}
//2-3 2씩 증가
for (i in 1..5 step 2)

//while문은 동일
```

---

### 예외
- try catch finally  
자바와 동일하나 try catch 문 역시 return할 수 있다.  
```kotlin
fun parseOrThrow(str: String): Int{
    try{
        return str.toInt()
    }catch(e:NumberFormatException){
        throw IllegalArgumentException()
    }
}

fun parseOrThrowV2(str: String): Int? {
    return try{
        str.toInt()
    }catch(e:NumberFormatException){
        null
    }
}
```

- Checked, UnChecked Exeption  
코틀린은 Checked Exception을 구분하지 않고  
모두 UnChecked Exception으로 취급한다.
```kotlin
fun readFile(){
    val currentFile = File(".")
    val file = File(currentFile.absolutePath + "/a.txt")
    val reader = BufferdReader(FileReader(file))
    println(reader.readLine())
    reader.close()
}
```
- try with resources  
JDK 7에서 추가된 문법  
try문 안에서 선언한 변수를 사용할 수 있다.
```java
public void readFile(String path) throws IOException {
    try (BufferdREader reader = new BufferedReader(new FileReader(Path))){
        System.out.println(reader.readLine());
    }
}
```
```kotlin
fun readFile(path: String){
    BufferedReader(FileReader(path)).use {
        reader -> println(reader.readLine())
    }
}
 ```
 코틀린에는 try with resources문이없다.  
 위 처럼 사용한다. 

 ---

 ### 함수 프리뷰
 - 함수 선언 문법  
 ```kotlin
 //1 //2
 fun max(a: Int, b: Int) = if(a>b) a else b
 ```
 1. 반환 값이 하나라면 중괄호를 = 로 대체할 수 있다.  
 2. =을 사용하는 경우, 반환값이 Int임을 추론할 수 있기때문에 반환타입을 생략할 수 있다.  

 - default parameter  
 여러 파라미터를 사용하는 메서드에서 자주 사용하는 입력값이 있을때  
 java에서는 OverLoading을 통해서 같은 이름의 메서드에 미리 파라미터를 입력한 형태로 만들 수 있다.  
 이런 방법의 단점은 그런 경우마다 메서드를 만들어줘야한다는 것이다.  
```kotlin
fun hello(name: String = "홍길동"){
    println(name + "반갑습니다")
}
```

 - named argument(parameter)
```kotlin
fun hello(
    name: String = "홍길동",
    how: String = "많이"
    ){
    println(name + how +" 반갑습니다")
}
```
위 함수를 호출할때 how는 입력될 값을 쓰고 name은 기본값을 쓰고 싶은 경우
```kotlin
hello(how = "많이")
```
위와 같이 파라미터 값을 직접 지정하는 것을 named argument라고 한다.  
이를 사용해서 Builder 처럼 활용할 수 있다.  

 - 같은 타입의 여러 파라미터 받기(가변인자)
 ```java
 //java
 public static void printAll(String... strings){
    for(String str : strings){
        System.out.println(str);
    }
 }
 ```
 ```kotlin
fun printAll(vararg strings: String){
    for(str in strings){
        println(str)
    }
}
//배열을 사용하는 경우 스프레드 연산자 * 를 넣어야한다
val array = arrayOf("A","B","C")
printAll(*array)
 ```