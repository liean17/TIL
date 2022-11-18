# 원티드 프리온보딩 안드로이드 챌린지 과제

## 1. NullPointerExeption의 의미와 방지

- 의미 :  
객체의 값이 null이라 사용할 수가 없다는 뜻  
값이 null인 객체안의 필드 값을 변경하거나, method를 호출하는 등 null 객체를 사용하려할때 발생한다.  

- 방지책 :  
1. if나 삼항 연산자등 조건문으로 객체의 null 여부를 검사
2. try catch 문을 사용해서 예외처리
3. Optional<> 타입을 사용해서 null 객체를 다루기

---

## 2. List, Set, Map

- List :  
객체를 인덱스로 관리해서 검색,추가,삭제가 편리하다.  

- SET :  
중복된 값을 허용하지 않으며 개념적으로는 수학에서 집합과 같다.  
기본적으로 순서를 유지하지 않으며 정확하게는 key를 value로 하는 map이다.  
 
- Map :  
Key-Value가 쌍으로 저장되는 구조로 이루어져있다. value는 중복이 가능하지만 key는 중복을 허용하지않는다.  
순서를 유지하지않지만 LinkedHashMap과 같은 순서를 유지하는 map도 존재한다.


---

## 3. Java SingleTon 패턴 구현
- 싱글톤 패턴 : 객체의 인스턴스가 한 개만 생성되는 패턴  
```java
public class SingletonTest {
    // 1
    private static SingletonTest instance = new SingletonTest();
    // 2
    private SingletonTest(){}
    // 3
    public static SingletonTest getInstance(){
        return instance;
    }

    public void hello(){
        System.out.println("Hi!, My name is Singleton");
    }
}
```
1. 인스턴스를 생성한다. 다른 객체에서 사용될 때도 이 인스턴스를 사용하게 된다.
2. 생성자를 private로 설정해서 다른 객체에서 생성할 수 없게 한다.
3. 생성한 인스턴스를 사용할 수 있는 static 메서드를 만든다.


---

## 4. 아래 JSON 형식을 정렬
- price가 높은 순으로 정렬된 List를 반환하는 함수를 구현하라.
```json
{
   "items": [
     {
       "label": "캐시미어 100% 터틀넥 스웨터",
       "price": 70000
     },
     {
       "label": "반팔 스트라이프 스웨터",
       "price": 30000
     },
     {
       "label": "화이트 스포츠 점퍼",
       "price": 150000
     }
   ]
}
```

```java
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Items {

    private String label;
    private Long price;

    public Long getPrice() {
        return price;
    }

    public Items(String label, Long price) {
        this.label = label;
        this.price = price;
    }

    public List<Items> sortWithPrice(String jsonData) throws ParseException {
        //문자열로 받은 json 을 객체 배열로 파싱
        JSONObject object = (JSONObject) new JSONParser().parse(jsonData);
        JSONArray items = (JSONArray)object.get("items");

        //파싱한 정보를 Items 객체에 담아 List 에 저장
        List<Items> itemList = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            JSONObject jsonObject = (JSONObject) items.get(i);
            String parsedLabel = (String) jsonObject.get("label");
            Long parsedPrice = (Long) jsonObject.get("price");
            itemList.add(new Items(parsedLabel, parsedPrice));
        }
        
        //price 를 기준으로 오름차순 정렬
        Collections.sort(itemList,(o1, o2) -> (int) (o1.price-o2.getPrice()));

        return itemList;
    }
}

```
