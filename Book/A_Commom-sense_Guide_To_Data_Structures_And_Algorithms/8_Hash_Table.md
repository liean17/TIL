# 누구나 자료구조와 알고리즘

## 8장 - 해시 테이블로 매우 빠른 룩업
---
### 1. 해시 테이블
* 해시 테이블
```
public class Solotion {
    public static void main(String[] args) {
        HashMap<String,String> map = new HashMap<>();
        map.put("A","Apple");
        map.put("B","Banana");
        map.put("C","Coconut");

        String c = map.get("C");
        System.out.println("c = " + c);
    }
}
```
해시 테이블은 자료를 Key와 Value의 쌍으로 저장한다.  
자바에서 구현된 대표적인 해시 테이블은 Hash Map인데  
어느 자료가 어디에 저장되어 있는지 바로 알 수 있기 때문에
검색에 상당히 유리하다.

### 2. 해싱
* 문자를 가져와 숫자로 변환하는 과정  
앞서 해시 테이블은 Key와 Value의 쌍으로 저장된다고 했는데 바로 Key로 저장되는 것은 아니다  
**해시 함수**를 통해 적절하게 변환된 숫자가 value의 인덱스가 되는 것이다.  
* 예를 들어 위 코드의 Coconut은 키 C에 저장되어있는데 C를 인덱스 3이라고 바꾸어 저장하는(세번째라서) 해시 함수가 있다고 하자.  
다음으로 'ㄷ'를 Key로 단어를 저장하고자 하는데 이 해시함수는 순서를 인덱스로 변환하기 때문에 ㄷ 역시 가나다 순으로 3이기에 인덱스 3에 저장하려한다.  
하지만 이미 단어가 존재하기 때문에 새로운 단어는 기존의 coconut과 함께 저장된다. 이것을 **충돌**이라고 한다  
한 인덱스에 두가지가 저장되어 있기 때문에 해당 인덱스 안의 값을 검색하려면  
추가로 인덱스 내에서 선형 검색을 수행하게 된다.

### 3. 효율적인 해시 테이블 만들기
 * 해시 테이블 효율성의 세가지 요인
 1. 해시 테이블에 얼마나 많은 데이터를 저장하는가
 2. 해시 테이블에서 얼마나 많은 셀을 쓸 수 있는가
 3. 어떤 해시 함수를 사용하는가  
 좋은 해시 함수란 사용 가능한 모든 셀에 데이터를 분산시킬 수 있어야 한다.   

 
 * 조정  
    데이터가 많은 셀에 저장될 수록 효율성이 좋다. 충돌이 적게 일어날 수록 효율성이 늘어나기 때문이다. 하지만 불필요하게 셀이 많은 것도 메모리 낭비가 된다.  
    따라서 많은 메모리를 낭비하지 않으면서 최대한 충돌을 피할 수 있도록 조정해야한다.

### 4. 해시 테이블의 예
* 메뉴판  
```
public class Restaurant {
    public static void main(String[] args) {
        HashMap<String,String> map = new HashMap<>();
        map.put("Hamburger","4000원");
        map.put("Bibimbab","5000원");
        map.put("Kimchijeon","5000원");
    }
}
```
식당의 메뉴판은 각 음식의 메뉴이름(Key)과 가격(Value)을 가진다

* 해시 테이블을 이용한 배열

```
public static void main(String[] args) {
    //배열에서 검색
    int[] arr = {1, 2, 3, 4, 5};
    for (int i : arr) {
        if (i == 5) {
            System.out.println(true);
        }
    }
    
    //해시 테이블에서 검색
    HashMap<Integer, Boolean> map = new HashMap<>();
    for (int i = 1; i <= 5; i++) {
        map.put(i, true);
    }
    System.out.println(map.get(5)); //true
    System.out.println(map.get(7)); //null
}
```
위 배열에서 5를 검색할 경우 시간복잡도는 5이며 빅 오 표기법으로는 O(N)이다  
하지만 아래 해시테이블에서 검색 하면 key가 5인 값을 단번에 찾아서 시간 복잡도는 O(1)이다  
그리고 찾는 값이 없다면 null을 반환한다.  

---
### 연습문제 풀이

1. 두 배열의 교집합을 반환하는 함수 만들기.(시간복잡도가 O(N)일것)
```
public static ArrayList<Integer> maker(int[] arr1, int[] arr2){
    ArrayList<Integer> answer = new ArrayList<>();
    HashMap<Integer,Boolean> map = new HashMap<>();

    for (int i = 0; i < arr1.length; i++) {
        map.put(arr1[i],true);
    }
    for (int i = 0; i < arr2.length; i++) {
        if(map.containsKey(arr2[i]))
            answer.add(arr2[i]);
    }
    return answer;
}
```
첫번째 for문을 통해서 hashmap에 데이터를 저장하고  
두번째 for문에서 hashmap의 데이터와 arr2의 데이터를 비교해서 같은 값이 있다면(교집합) list에 저장한다.
