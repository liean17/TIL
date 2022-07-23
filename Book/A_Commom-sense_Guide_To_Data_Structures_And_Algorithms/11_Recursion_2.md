# 누구나 자료구조와 알고리즘

## 11장 - 재귀적으로 작성하는 법
---
### 1. 재귀 카테고리 : 반복 실행
* 재귀 트릭 : 추가 인자 넘기기  
배열 내 모든 수에 2를 곱하는 함수를 재귀로 만든다고 할때,  
재귀 대신 반복문을 사용한다면 인덱스를 기록하는 변수를 두고 반복될수록 인덱스를 증가시킬 것이다.  
재귀에서도 마찬가지이다.

```java
public static void main(String[] args){
    int arr = {1,2,3,4,5};

    doubleArray(arr,0);
}

public static void doubleArray(int[] arr, int index){
    if(index>=arr.length) return;
    arr[index] *= 2;
    doubleArray(arr,index++);
}
```
### 2. 재귀 카테고리 : 계산
하위 문제의 계산 결과에 기반해서 계산하는 방법으로  
대표적으로 factorial이 있다.
* 상향식과 하향식  
factorial 구현은 두가지 방식을 사용할 수 있다.

```java
public int factorialUp(int n){
    int answer = 1;
    int num = 1;

    for(int i = 0;i<n;i++){
        answer = answer * num;
        num++;
    }
    return answer;
}
```
```java
public int factorialDown(int n){
    if(n==1) return 1;

    return n * factorialDown(n-1);
}
```
전자를 **상향식** 후자를 **하향식**방식이라고 한다.  
상향식은 루프를 써도되고 재귀를 써도 되는데 재귀를 사용할 경우 매개변수를 추가해줘야한다.  
하향식은 구현 방법이 재귀뿐이다.  

---
### 2.1 하향식 재귀 : 새로운 사고방식
하향식 사고절차는 **문제의 본질에 세부적으로 파고들지 않는 것**이다.  
하향식 재귀 경험이 부족하면 다음 절차를 통해서 하향식 사고 방식을 기를 수 있다.  
1. 작성 중인 함수를 이미 누군가 구현해 놓았다고 상상한다.
2. 문제의 하위 문제를 찾는다.
3. 하위 문제에 함수를 호출하면 어떻게 되는지 보고, 거기서 부터시작한다.  

### 2.1.1 배열 합  
배열을 받아 배열 내 모든 수를 합하는 함수를 작성한다고 한다.  
배열은 [1,2,3,4,5]이다.  
이미 함수가 구현되어있고 하위 문제를 찾는다고 하면, 예제 배열에서 하위 문제는  
맨앞의 1을 제외한 [2,3,4,5]이다. sum 함수는 정상적으로 이미 구현되었다고 했기 때문에  
```1 + sum([2,3,4,5])``` 라고 적어주면 문제가 해결될 것이다.  
여기에서 조금만 더 나아가면 다음처럼 된다.
```java
public static int sum(int[] arr){
    if(arr.length==1) return arr[0];

    return arr[0] + sum(Arrays.copyOfRange(arr,1,arr.length));
}
```

### 2.1.2 문자열 뒤집기
문자열을 역순으로 뒤집는 함수를 작성한다고 한다.  
문자열은 "abcde"이다.  
이전 예제 처럼 문자열을 역으로 만드는 함수는 이미 누군가 구현해놓았다고 생각해보자.  
하위 문제는 "bcde"이며 역으로 만들어야 하므로 a는 뒤에 붙여주면 된다.  
```reverse("bcde")+str[0]```
```java
public static String reverse(String str){
        char[] chars = str.toCharArray();
        if(chars.length==1) return chars[0]+"";
        str = String.valueOf(Arrays.copyOfRange(chars, 1, chars.length));
        return reverse(str) + chars[0];
    }
```

### X 세기
문자열에 포함된 x의 갯수를 세는 함수를 작성한다.  
문자열이 "axbxcxd"라면 하위 문제는 "xbcxd"이며 하위 문제에 대한 답은 3이다.  
그리고 첫번째 문자가 x면 1을 더해주면 된다.  
```java
public static int countX(String str){
    //기저조건
    if(str.length()==0) return 0;

    if(str.toCharArray()[0]=='x'){
        return 1 + countX(str.substring(1,str.length()));
    }
    return countX(str.substring(1,str.length()));
}
```
---
### 계단 문제
계단 갯수가 주어지고 한번에 1,2,3 계단씩 오를 수 있다고 할때 모든 경로의 갯수는?  
앞선 문제들과 달리 단순하게 세기는 어렵다. 하지만 하향식으로 생각하면 쉬워진다.  
10계단을오른다고 할때 하위 문제는 9계단까지 오르는 경우의 수 인가?  
8계단에서 2계단, 7계단에서 3계단을 한번에 오르는 경우도 있다.  
따라서 10계단을 오르는 경우 하위 문제는 9계단 + 8계단 + 7계단 을 오르는 경우의 수가 된다.  
```java
public static int numberOfPaths(int N){
    return numberOfPaths(N-1) + numberOfPaths(N-2)+numberOfPaths(N-3); 
    }
```
여기서 기저조건만 잘 해결해주면 된다.  
```java
public static int numberOfPaths(int N){
    if(N<0) return 0;
    if(N==1||N==0) return 1;

    return numberOfPaths(N-1) + numberOfPaths(N-2)+numberOfPaths(N-3);
    }
```

### 애너그램 생성
주어진 문자열의 모든 애너그램을 반환하는 함수를 만들어본다.  
애너그램은 문자열을 재배열한 조합이다.  
문자열이 "abcd"라면 하위 문제는 "abc"이다.  
"abc"의 모든 애너그램을 반환한 것에서 d를 각 위치마다 추가하면 "abcd"의 애너그램이 된다.  

