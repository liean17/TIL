# 누구나 자료구조와 알고리즘

## 15장 - 이진 탐색 트리로 속도 향상  
퀵 정렬은 오름차순 혹은 내림차순 정렬에 아주 탁월하지만 정렬치곤 빠른거지  
데이터를 주고 받을때 마다 O(NlogN)의 시간이 걸리는 것은 매우 비효율적일 것이다.  
따라서 이미 데이터가 정렬된 것을 이용하는게 좋은데 정렬된 배열은 삽입과 삭제가 느리다.  
해시테이블은 꺼내오고 저장하기 편하지만 정리가 되어있지않다.

---
### 1. 트리  
트리는 노드 기반 자료 구조인데 각 노드는 여러 노드로의 링크를 포함할 수 있다.  
* 용어 설명
    - 루트 : 가장 상위에 위치한 노드
    - 부모노드 : m이라는 노드가 q, z로의 링크를 포함하고있다면 m은 q와 z의 부모노드이다.
    - 자식노드 : 위에서 q와 z는 m의 자식노드다. 
    - 레벨 : 노드를 그림으로 나타내면 루트를 중심으로 아래로 퍼져나가는데 가로선 상으로 같은 위치에 존재하는 노드들이 있다. 이러한 가로선 들을 레벨이라고 한다.
    - 프로퍼티 : 균형잡힌 정도를 뜻한다. 모든 노드의 하위 노드 개수가 같으면 균형이고, 다르면 불균형이된다.

### 2. 이진 탐색 트리
우선 이진 트리는 각 노드의 자식이 2개 이하다.  
이진 탐색 트리는 다음의 규칙이 추가된 트리다.  
    - 각 노드의 자식은 최대 왼쪽 오른쪽에 각각 하나다.
    - 한 노드의 왼쪽 자손은 그 노드보다 작은 값만 포함할 수 있고, 오른쪽은 큰 값만 포함할 수 있다.  

### 3. 검색
이진 탐색 트리에서 어떤 수를 검색한다고 하자.  
보통 알고리즘에서 시작은 루트 노드다.  
먼저 루트노드가 찾는 값인지 비교하고 맞으면 반환한다.  
아니면 루트 노드의 값이 찾는 값 보다 큰지, 작은지를 확인한다.  
이후 작다면 왼쪽 노드로, 크면 오른쪽으로 이동하면서 값을 찾으면 된다.  

* 효율성  
검색하면서 왼쪽 노드 혹은 오른쪽 노드로 이동하게되면  
반대쪽 노드는 완벽하게 검색 대상에서 배제된다. 즉 검색할대 마다 검색대상이 절반만큼 줄어들게 된다.  
시간 복잡도를 알아보면 O(logN)이다. 최악의 시나리오에도 트리의 레벨만큼만 검색하기 때문이다.  
검색면에서는 정렬된 배열의 이진 탐색과 효율성이 동일하다.  
하지만 삽입에 있어서는 훨씬 우수해진다.  

### 4. 삽입
이진 탐색 트리에서 삽입은 검색과 동일하다.  
검색을 통해서 더이상 이동할 수 없는 위치를 찾은 후(가장 아래에 도달했을때) 해당 노드보다 큰지, 작은지를 판단해서 자리에 넣으면 끝이다.  
검색에서 삽입이라는 한단계를 더하기 때문에 O(logN) + 1이며 이것은 O(logN)과 같다.  
이진 탐색 트리는 재귀를 통해 구현한다.
```java
public class Node{
    public int value;
    public Node leftNode;
    public Node rightNode;

    public Node(int value) {
        this.value = value;
    }
}
    
void insert(int value, Node node){
    if(value< node.value){
        if (node.leftNode==null){
            node.leftNode = new Node(value);
        }else{
            insert(value,node.leftNode);
        }
    }else{
        if (node.rightNode==null){
            node.rightNode = new Node(value);
        }else{
            insert(value,node.rightNode);
        }
    }
}
```

### 5. 삭제
삭제는 굉장히 신중하게 접근해야한다.  
검색해서 원하는 값을 찾았을때 바로 삭제해버리면 해당 노드의 자식노드에 접근이 불가해진다.  
따라서 삭제된 빈 자리에 적절한 값으로 대체해줘야한다.  
이 대체될 값을 후속자 노드라고하는데  
후속자 노드를 찾는 방법은 삭제된 값의 오른쪽 자식을 방문한 후, 그 자식의 왼쪽 자식을 쭉 방문했을때 끝에있는 노드를 찾으면 된다.  
해당 값이 삭제된 값보다 크고, 그 다음 값보다는 작은 값이된다.  
만약 후속자 노드에게 오른쪽 자식이 있다면 연결이 끊기기 때문에  
후속자 노드의 부모노드의 왼쪽에 그 자식을 넣어주면 된다.  
코드로 구현하면 다음과 같다
```java
Node delete(int valueToDelete, Node node){
    if (node==null) return null;
    if(valueToDelete<node.value){
        node.leftNode = delete(valueToDelete,node.leftNode);
        return node;
    } else if (valueToDelete> node.value) {
        node.rightNode = delete(valueToDelete,node.rightNode);
        return node;
    } else {
        if(node.leftNode==null){
            return node.rightNode;
        } else if (node.rightNode==null) {
            return node.leftNode;
        }else{
            node.rightNode = lift(node.rightNode,node);
            return node;
        }
    }
}
private Node lift(Node node, Node nodeToDelete) {
    if(node.leftNode!=null){
        node.leftNode = lift(node.leftNode,nodeToDelete);
        return node;
    }else{
        nodeToDelete.value = node.value;
        return node.rightNode;
    }
}
```
기저 조건은 노드가 실제로 존재하지 않을 때 이다. 즉 재귀 호출에서 존재하지 않는 자식 노드에 접근하면 null을 반환한다.  
다음으로 찾는 값을 현재 노드와 비교한다. 작거나 큰 여부에 따라 재귀 호출로 다음 노드를 가져온다.  
lift메서드는 후속자 노드를 찾아 위치를 변경하는 역할을 한다.  




