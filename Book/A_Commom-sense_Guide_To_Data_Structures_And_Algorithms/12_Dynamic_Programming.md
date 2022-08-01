# 누구나 자료구조와 알고리즘

## 11장 - 동적 프로그래밍
재귀는 좋은 해결 방법임과 동시에 가장 느린 빅 오 카테고리의 요인이기도 하다.  
이러한 문제가 어디서 나오는지를 알고 개선하는 법을 배우자.

---
### 1. 불필요한 재귀 호출
* 배열에서 최댓값을 찾는 재귀함수를 살펴보자.
```java
public static int max(int[] arr){
    if(arr.length==1) return arr[0];
    
    if(arr[0]>max(Arrays.copyOfRange(arr,1,arr.length))) return arr[0];
    return max(Arrays.copyOfRange(arr,1,arr.length));
    }
```
위 코드에서 재귀는 두 조건문에 존재한다.  
따라서 한번 재귀함수를 호출할 때 마다 2의 제곱으로 증가하게 된다.  
[1,2,3,4]에서 최댓값을 찾는다고 할때,  
현실에서는 각 4가지 숫자를 비교하면 되지만  
위 코드에서는 한번의 비교를 위해 최댓값 4를 여러번 출력하게 된다.  
콘솔 출력을 통해 알아보면 4개의 숫자를 가진 배열은 같은 함수를 15번 출력한다.

### 2. 작은 개선
* 추가적인 호출을 막기위해 변수에 값을 저장해두자.
```java
public static int max(int[] arr){
    if(arr.length==1) return arr[0];
    
    int maxOfArray = max(Arrays.copyOfRange(arr,1,arr.length));
    if(arr[0]>maxOfArray) return arr[0];
    return maxOfArray;
    }
```
함수를 한번만 호출해서 변수에 저장하면  
다른 곳에서 굳이 다시한번 계산할 필요가 없기 때문에 효율성이 늘어난다  
이 경우 max를 단 4번만 호출한다.

이전 코드에서 시간 복잡도는 O(2<sup>N</sup>)이다.  
요소가 늘어날수록 2,3..으로 늘어나기 때문에 심각하게 느린 알고리즘이다.  
하지만 변경한 코드의 시간복잡도는 O(N)이다.  
따라서 불필요한 재귀 호출을 피하는 것이 재귀를 빠르게 만드는 핵심이다.

### 3. 하위 문제 중첩
피보나치 수열은 재귀로 해결할 수 있는 대표적인 문제다.  
```java
public int fibonacci(int n){
    if(n==0||n==1) return n;
    
    return fibonacci(n-2) + fibonacci(n-1);
    }
```
이전 같았으면 이렇게 풀었지만 지금 보면 문제가 보인다.  
본인을 두번 호출하고있기 때문에 시간 복잡도가 O(2<sup>N</sup>)가 된다.  
하지만 이번에는 간단하게 해결하기 어렵다  
변수에 저정할 데이터가 하나가 아니기 때문이다  
피보나치의 수는 두 수의 합이므로 그중 한 결과만 저장해서는 나머지 한 결과를 얻지 못한다.  
이런 문제를 하위 문제 중첩(overlapping subproblems)라 부른다.

### 4. 동적 프로그래밍
동적 프로그래밍(dynamic programming)은 하위 문제가 중첩되는 재귀 문제를 최적화하는 절차다.  
동적 프로그래밍을 통한 알고리즘 최적화에는 일반적으로 두 기법 중 하나를 사용한다.
1. 메모이제이션(memoization)  
같은 값을 반복적으로 출력하지않도록 해시 테이블에 저장해두는 방식이다.  
```java
public static int fibonacci(int n, int[] memo){
    if(n==0||n==1) return n;
    if(memo[n]==0){
        memo[n] = fibonacci(n-2,memo) + fibonacci(n-1,memo);
    }
    return memo[n];
    }
```
매개변수에 memo라고하는 배열을 추가한다. 이 배열은 인덱스와 관련해서 n+1의 크기를 가져야한다.  
배열에 출력값을 저장하고 꺼내어쓰는 방식이기 때문에 불필요한 함수호출이 줄게된다.

2. 상향식  
상향식은 재귀 대신 반복과 같은 다른 방식으로 해결하는 방법이다.  
상향식이 동적 프로그래밍의 하나로 간주되는 이유는 동적 프로그래밍이 재귀적으로 풀 수 있는 문제에 대해  
중첩되는 하위 문제를 중복 호출하지 않게 해주기 때문이다.  
```java
public static int fibonacci(int n){
    if(n==0) return 0;

    int a = 0;
    int b = 1;

    for (int i = 1; i < n; i++) {
        int temp = a;
        a = b;
        b = temp + a;
    }

    return b;
}
```

### 5. 마무리
두 기법의 사용은 문제에 따라, 왜 재귀를 사용하는지에 따라 다르다.  
메모이제이션을 쓰더라도 반복에 비해 재귀는 덜 효율적일 수 있다.  
따라서 재귀가 매우 직관적이지 않은 이상 일반적으로 상향식을 택하는 편이 낫다.  

---
## 연습문제

### 1. 수 배열을 받아 그 합을 반환하되 합이 100을 초과하면 초과하게 만드는 수를 제외하는 함수를 효율적으로 수정하라.  
```java
int addUntil100(int[] arr){
    if(arr.length==0) return 0;
    if(arr[0]+ addUntil100(Arrays.copyOfRange(arr,1,arr.length))>100){
        return addUntil100(Arrays.copyOfRange(arr,1,arr.length));
    }
    return arr[0] + addUntil100(Arrays.copyOfRange(arr,1,arr.length));
}
```
- addUntil100(Arrays.copyOfRange(arr,1,arr.length)) 가 반복되므로 이것을 변수로 정해주면 된다.

### 2. 재귀를 사용해서 '골롬 수열'을 구현한 코드를 메모이제이션으로 최적화하라.
```java
int golomb(int n){
    if(n==1) return 1;
    return 1 + golomb(n-golomb(golomb(n-1)));
}
```
메모이제이션의 핵심은 출력값을 배열에 저장해두는 것이다.
```java
int golomb(int n, int[] memo){
    if(n==1) return 1;
    if(memo[n]==0){
        memo[n] = 1 + golomb(n - golomb(golomb(n-1,memo),memo),memo);
    }
    return memo[n];
}
```

### 3. 다음 문제를 메모이제이션으로 효율성을 개선하라.
```java
int uniquePaths(int row,int col){
    if(row==1||col==1)return 1;
    return uniquePaths(row-1,col) + uniquePaths(row,col-1);
}
```
2차원 배열의 차이일 뿐 위와 동일하다.
```java
int uniquePaths(int row,int col,int[][] arr){
    if(row==1||col==1)return 1;
    
    if(arr[row][col]==0){
        arr[row][col]=uniquePaths(row-1,col,arr) + uniquePaths(row,col-1,arr);
    }
    
    return arr[row][col];
}
```


