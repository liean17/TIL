# 누구나 자료구조와 알고리즘

## 7장 - 일상적인 코드 속 빅 오
---
### 1. 짝수의 평균

```
public int averageOfEven(int[] arr){
    int sum = 0;
    int count = 0;
    for(int n : arr){
        if(n%==0)
            sum+=n;
            count++;
    }
    return sum/count;
}
```
주어진 배열을 for문으로 쭉 확인하면서 짝수인 경우 sum에 더한다.
모든 배열을 한번씩 검사하기 때문에 시간 복잡도는 N이며 정확하게 따지면 짝수일때 if문과 sum에 추가하는 작업, 갯수를 세는 작업을 거치기 때문에  
3N, 그리고 각 변수를 선언하고 나누기 역시 수행하기 때문에 3N+3이 정확한 시간복잡도이다  
하지만 빅 오 표기법은 상수를 무시하기에 이 코드의 시간복잡도는 O(N)이다

### 2. 단어 생성기

```
public int stringMaker(String[] strArr) {
    ArrayList<String> list = new ArrayList<>();
    for (int i = 0; i < strArr.length; i++) {
      for (int j = 0; j < strArr.length; j++) {
        for (int k = 0; k < strArr.length; k++) {
          list.add(strArr[i]+strArr[j]+strArr[k])
        }
      }
    }
    return list.size();
  }
```
문자열 배열을 받아서 각 문자열을 조합한 갯수를 계산하는 코드다.  
**3가지 단어**를 조합해서 for반복문 세개가 중첩되어있는데 이때문에 시간 복잡도는 O(N³)이다.  
따라서 해당 코드를 그대로 사용한다면 단어가 늘어날 수록 시간 복잡도가 기하급수적으로 늘어나게 된다.

### 3. 번호 부착기

```
public StringBuilder numberTag(String[] strArr){
    StringBuilder sb = new Stringbuilder();
    
    for(String s : strArr){
        for(int i = 1;i<=5;i++){
            sb.add(s+ " " + i + ",");
        }
    }
}
```
문자열배열을 받아서 각 문자열에 1부터 5까지 숫자를 더한 문자를 담은 StringBuilder를 반환한다.  
for문이 중첩되어있기 때문에 O(N²)라고 생각할 수 있지만 strArr이 얼마나 많은 문자열을 가졌건 간에  
내부의 for문은 5번만 반복되기 때문에 시간 복잡도는 O(5N)이다.

### 4. 다른 배열간의 곱셈

```
public ArrayList<Integer> twoArrayProduct(int[] arr1, int[] arr2){
    ArrayList<Integer> list = new ArrayList<>();

    for(int i = 0;i<arr1.length;i++){
        for(int j = 0;j<arr2.length;i++){
            list.add(arr1[i]*arr2[j]);
        }
    }
    return list;
}
```
배열 두개를 받아서 각 숫자의 곱을 저장하는 리스트를 반환하는 메서드이다.  
이 경우 각 리스트의 배열 크기에 따라 시간 복잡도가 달라질 것이다.  
두 배열의 크기가 같다면 O(N²)이고 한 배열의 크기가 1이라면 O(N)이다.  
이렇게 가변적인 경우에는 O(N)에서 O(N²)사이의 복잡도를 가진다고 생각하는 것이 최선이다. 

---
