# 누구나 자료구조와 알고리즘

## 10장 - 재귀를 사용한 재귀적 반복  
---
### 1. 재귀란?
* 재귀함수
```
public static void hello(){
    System.out.println("안녕하소");
    hello();
```
재귀는 함수가 자기 자신을 호출할 때를 뜻하는 용어다.
위 코드에서 hello메서드는 콘솔에 문자를 출력하고 다시 자신을 호출한다  

```
public static void hello(){
    for(int i = 0;;i++)
        System.out.println("안녕하소");
```
재귀 코드를 반복문을 사용해서 똑같이 만든 모습인데 비슷해보여도 재귀 코드가 더 간단함을 알 수 있다.  

### 2. 기저 조건  
* 기저 조건  
위 두 코드는 멈추는 조건이 없기때문에 오류가 발생할때 까지 반복된다.  
함수가 반복되지 않게 하는 것을 **기저 조건**이라고 하며 모든 재귀 함수에는 기저 조건이 적어도 하나 있어야한다.  
```
public static void hello(int n){
    System.out.println("안녕하소");
    if(n==0)
        return;
    hello(n-1);
```
재귀 함수가 실행될 때 마다 매개변수 n이 1씩 줄어 0이되면 작동을 멈춘다. 
hello() 메서드는 0이 기저 조건이다.

### 3. 활용
* 파일시스템 순회  
재귀가 주로 사용되는 예는 몇 단계나 깊이 들어가야 하는지 모르는 상황이다.
폴더에서 파일을 검색하고자 할때 파일을 검색하고 찾지못하면 다시 함수를 실행하는 방식으로 재귀를 활용할 수 있다.
---
### **연습 문제**

1. 다음 함수는 low부터 high까지의 수를 하나 걸러 하나씩 출력한다고 할때, 다음 함수의 기저 조건을 알아내라.
```
public static void printEveryOther(int low,int high){
    if(low>high) return;
    System.out.println(low);
    printEveryOther(low+2,high);
    }
```
- 입력한 low가 high보다 커지면 종료된다.

2. low부터 high까지 모든 수의 합을 반환하는 factorial을 작성했으나 조건이 빠져서 무한대로 실행된다! 올바른 기저 조건을 추가해서 수정하자.
```
 public static int sum(int low,int high){
        return high + sum(low,high-1);
    }
```
- `if(high==0) return 0;`  
high가 0보다 작아지지 않게 기저조건을 추가한다.

        