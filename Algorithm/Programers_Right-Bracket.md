# 올바른 괄호

## 문제
https://programmers.co.kr/learn/courses/30/lessons/12909  
괄호 '(' 과 ')' 만을 가진 문자열이 주어진다  
**올바르게**괄호가 열고 닫혔는지 확인하는 문제이다.  

---
## 풀이
```java
import java.util.Stack;

class Solution {
    boolean solution(String s) {
        boolean answer = true;

        char[] chars = s.toCharArray();
        
        Stack<Character> stack = new Stack<>();

        for (char aChar : chars) {
            if (aChar=='('){
                stack.add(aChar);
            }else{
                if(stack.size()==0) return false;
                stack.pop();
            }
        }
        if (stack.size()!=0) return false;


        return answer;
        }
}
```
보자말자 전에 책에서 봤던 문제임을 깨달았다  
Stack의 예제였는데 오히려 이것보다 복잡해서 쉽게 풀었다.  
풀이는 굉장히 간단하다  
여는 괄호 '('가 나오면 stack에 넣고
닫는 괄호 ')'가 나오면 stack에서 pop한다  

닫는 괄호가 나왔는데 stack에 저장된 여는 괄호가 없으면  
여는 괄호가 부족하다는 뜻이니 **false**  

과정을 모두 마쳤는데 stack이 비어있지 않으면  
닫는 괄호가 부족하다는 뜻이니 **false**

