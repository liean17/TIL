# 누구나 자료구조와 알고리즘

## 13장 - 속도를 높이는 재귀 알고리즘
대부분의 컴퓨터 언어에 구현된 정렬 알고리즘은 퀵 정렬이다.  
퀵 정렬의 동작 방식을 공부하면 재귀를 사용해 어떻게 알고리즘의 속도를 향상시키는지 배울 수 있고.  
실제 쓰이고 있는 다른 실용적인 알고리즘에도 똑같이 적용할 수 있다.

---
### 1. 분할
* 배열을 분할한다는 것은 임의의 수를 가져와 이 수보다 작으면 수의 왼쪽에, 크면 오른쪽에 두는것 이다.  
배열에서 임의의 숫자를 꺼내 피벗이라 한다.  
그리고 배열 맨 왼쪽과 맨 오른쪽 두가지 포인터를 사용한다.  
왼쪽 포인터의 숫자가 피벗보다 작으면 포인터를 1 증가시키고, 크면 오른쪽 포인터를 차례로 비교한다.  
마찬가지로 오른쪽 포인터가 피벗보다 크면 다음 숫자로 이동하고, 작으면 왼쪽 포인터를 다시 비교한다.   
마지막으로 왼쪽 포인터와 오른쪽 포인터가 만나면, 피벗값과 비교해서 피벗이 작다면 두 위치를 바꾼다.  
이렇게하면 완벽하게 정렬되지는 않지만 피벗보다 작은 수는 모두 왼쪽에 있게 된다.
```java
public static int partition(int leftPointer, int rightPointer){
    int pivotIdx = rightPointer;
    int pivot = arr[pivotIdx];
    rightPointer -= 1;
    while (true){
        while (arr[leftPointer]<pivot) leftPointer +=1;
        while (arr[rightPointer]>pivot) rightPointer -= 1;
        if(leftPointer>=rightPointer) break;
        else{
            int tmp = arr[leftPointer];
            arr[leftPointer] = arr[rightPointer];
            arr[rightPointer] = tmp;
            leftPointer += 1;
        }
    }
    int tmp = arr[leftPointer];
    arr[pivotIdx] = arr[pivotIdx];
    arr[pivotIdx] = tmp;
    return leftPointer;
}
```
위는 피벗을 올바른 위치로 옮기는 코드다.
### 2. 퀵 정렬
* 퀵 정렬 알고리즘은 위 분할을 이용한다.
    1. 배열을 분할한다. 임의의 수 즉 피벗은 제 위치를 찾는다.  
    2. 피벗 전후의 숫자를 각각 배열로 생각하고 다시 분할한다.
    3. 하위 배열의 원소가 0개 혹은 1개이가 되는것이 기저조건이다.
```java
public static void quickSort(int leftIdx, int rightIdx){
    if (rightIdx - leftIdx <= 0) return;
    
    int pivotIdx = partition(leftIdx, rightIdx);
    
    quickSort(leftIdx,pivotIdx-1);
    
    quickSort(pivotIdx+1,rightIdx);
    }
```

### 3. 퀵 정렬의 효율성
* 퀵 정렬의 효율성은 분할에 달려있다.  
각 분할마다 배열 내 각 원소를 피벗과 비교하므로 최소 N번 비교한다.  
교환은 데이터 정렬 상태에 따라 다르다. 가장 교환이 많이 이루어진다고 해도 교환은 한번에 두번 이루어지기 때문에 N/2번 교환된다.  
정렬은 많은 분할로 이루어져있기 때문에 효율성을 따지기 위해서는 더 고려해야한다.  

배열을 분할할 때마다 하위 배열 두 개로 나눈다. 배열 중간 어딘가를 피벗이라 가정하면 평균저인 경우에는 이 두 하위 배열은 크기가 거의 비슷할 것이다.  
크기가 1이 될 때까지 각 하위 배열을 반으로 나누려면 크기가 N인 배열이면 logN번 걸린다.  
크기가 1이 된 배열의 합은 N이다. 따라서 퀵 정렬에는 N x logN 단계가 걸린다.  

피벗이 항상 끝이되는 최악의 경우에는 효율성이 N<sup>2</sup>/2로 빅오로는 O(N<sup>2</sup>)가 된다.  
하지만 최선의 경우, 평균적인 경우 O(NlogN)으로 효율적이기 때문에 대부분의 정렬 알고리즘은 퀵 정렬을 구현한다.  

### 4. 퀵 셀렉트  
* 퀵 정렬은 대부분 구현되어있어서 위 처럼 직접 구현할 일은 없지만 퀵 셀렉트는 실용적으로 쓸모 있을 수 있다.  

한 배열에서 N번째로 큰값, 작은값을 알고싶다고 할때 정렬과 인덱스를 통해 찾을수도 있지만  
이 경우 퀵 정렬이라면 NlogN이 걸리는데 퀵 셀렉트를 사용해서 조금 더 효율적이게 찾을 수 있다.  

퀵 셀렉트는 퀵 정렬에서 사용했던 분할을 이용한다.  
피벗을 통해 분할을 했다면, 평균적으로 피벗은 중간 즈음에 위치하게된다.  
피벗의 위치를 정확하게 알았기 때문에 찾고자하는 인덱스가 피벗 위치보다 작다면  
피벗 오른쪽에있는 값들은 완벽하게 무시해도 된다.  
이런 방식으로 분할을 통해 찾지않아도 되는 값들을 버려나간다.  

```java
public static int quickSelect(int k번째로작은수,int leftIdx, int rightIdx){
    if (rightIdx - leftIdx <= 0) return arr[leftIdx];
    
    int pivotIdx = partition(leftIdx,rightIdx);
    
    if(k번째로작은수<pivotIdx) return quickSelect(k번째로작은수,leftIdx,pivotIdx-1);
    if(k번째로작은수>pivotIdx) return quickSelect(k번째로작은수,pivotIdx+1,rightIdx);
    else return arr[pivotIdx];
    }
```

퀵 셀렉트의 효율성은 평균적으로 O(N)이다.  
피벗이 중간에 위치한다고 할때 배열의 수가 계속 반으로 줄어들기 때문에  
모든 배열의 수를 합하면 2N이 된다.  

### 5. 정렬의 중요성  
한 배열에서 중복되는 숫자를 찾아서 출력하는 알고리즘을 만든다고하면  
숫자 하나를 정해서 그 수와 동일한것을 다시 찾아야하기때문에 O(N<sup>2</sup>)가 된다.  
하지만 해당 배열이 정렬되어있는 상태라면 문제가 상당히 간단해진다.  
한번의 반복문을 통해서 n번째 숫자가 n+1번째 숫자와 동일한지만 검사하면 되기 때문이다.  
이 경우 정렬을 포함해서 O(N+NlogN)으로 결국 O(NlogN)의 시간복잡도를 가진다.  
정렬을 함으로써 알고리즘이 대폭 효율적이게 된 것이다.  

---
## 연습문제  

1. 양수의 배열이 주어져있을때 세 수의 가장 큰 곱을 반환하는 함수를 작성하라.  
: 반복문을 사용하면 세번 중첩해야하지만 정렬을 사용해서 마지막 3가지 숫자를 곱하면 된다.

2. 0부터 N까지 숫자를 포함하는 배열에서 하나의 숫자가 빠졌다고 할 때 이 숫자를 찾는 함수를 작성하라.  
: 정렬한 뒤 반복문에서 i!=arr[i]인 숫자를 찾아서 출력하면 된다. 정렬하지 않으면 반복문 두개가 들어가는 O(N<sup>2</sup>)함수가 된다.  

3. 배열에서 가장 큰 수를 찾는 함수를 세가지 방법으로 구현하라. 
    - O(N<sup>2</sup>)  
    : 반복문 중첩

    - O(NlogN)  
    : 퀵 정렬 사용 후 마지막 인덱스의 숫자 반환

    - O(N)  
    : 메모리를 사용하는 방법
    ```java
    public static int findMax(int[] arr){
        int max = arr[0];

        for (int i : arr) {
            if(max<i) max = i;
        }
        return max;
    }
    ```

