# 이미지, 자료 받기  

### 이미지, 자료 여러개 받기  
이미지를 여러개 받는 경우  
가장 문제되는 것은 이미지의 보관이다.  
받을 수 있는 이미지 갯수를 제한해서 각각 받는 방법도 있지만,  
이미지 엔티티를 만들어서 게시글과 연관관계 매핑을 함으로써 쉽게 받을 수 있다.  
List로 MultiPartFile을 받아서 게시글 생성시 엔티티로 변환해서 저장해주면 된다.  
저장에서 한번 오류가 났었는데, post가 저장이 된후에 이미지를 set해줘야한다는 것이다.  