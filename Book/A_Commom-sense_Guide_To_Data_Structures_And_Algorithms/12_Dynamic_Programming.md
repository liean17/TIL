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

