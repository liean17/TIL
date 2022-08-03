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


