# 스프링 Api 문서화
그간 미뤄왔던 Rest Docs를 이용한 Api문서화를 공부한다.  
### **[결과](https://github.com/liean17/code-states-solo-project)**

### 사전준비사항
당연히 restdocs의 의존성을 추가해야한다.  
또 문서가 작성될 경로를 추가해줘야한다.  
다음으로 해당 경로에 adoc파일을 만들어두고  
메서드에서 설정한 이름과 동일하게 값을 받을 수 있도록 만들어야한다.

### 전체적인 흐름
1. 우선 요청을 위한 객체를 생성한다.  
등록의 경우에는 requestDto가 필요할 것이고 수정에는 updateDto,  
조회,삭제에는 객체 id가 필요하다.  
전체조회의 경우에는 응답할 객체들을 페이지화 한 리스트가 필요하다.  

그리고 등록,수정은 gson파일로 변환해줄 필요가 있다.

2. 요청에 대해 예상되는 응답 객체를 생성한다.  
그리고 Mock의 given메서드를 통해 특정 상황에서 객체가 해야하는 행위를 정의한다  
`given(memberService.createMember(Mockito.any(MemberRequestDto.class))).willReturn(responseDto);`  
위 처럼 특정 행위가 있었을때 무엇을 기대할 수 있는지를 작성하고 기대값에는 만들어둔 응답객체가 들어간다.  

3. mockMvc.perform을 통해 요청을 수행한다.  
```java
ResultActions actions = mockMvc.perform(
    post("/api/v1/members/")
        .accept(MediaType.APPLICATION_JSON)//클라이언트 쪽에서 리턴 받을 응답 데이터 타입 설정
        .contentType(MediaType.APPLICATION_JSON)//서버 쪽에서 처리 가능한 타입 설정
        .content(content)
);
```
등록, 수정과 같이 서버내에서 작업이 필요한 경우에는 contentType도 적어준다.

4. .andExpect를 통해 결과를 검증하고, 문서화에 필요한 데이터를 작성한다.  
```java
actions
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.name").value(requestDto.getName()))
    .andExpect(jsonPath("$.companyName").value(requestDto.getCompanyName()))
    .andDo(document("post-member",
        getRequestPreProcessor(),
        getResponsePreProcessor(),
        requestFields(
            List.of(
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                fieldWithPath("companyName").type(JsonFieldType.STRING).description("회사명"),
                fieldWithPath("companyType").type(JsonFieldType.STRING).description("회사 업종 코드"),
                fieldWithPath("companyLocation").type(JsonFieldType.STRING).description("회사 지역 코드")
            )
        ),
        responseFields(
            List.of(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("companyName").type(JsonFieldType.STRING).description("회사명"),
                fieldWithPath("companyType").type(JsonFieldType.STRING).description("회사 업종 코드"),
                fieldWithPath("companyLocation").type(JsonFieldType.STRING).description("회사 지역 코드")
            )
        )
    ));
```
requestFields와 responseFields에 각각 어떤 데이터가 요청되고 응답되는지 작성한다.  
fieldWithPath는 임의로 설정하는게 아니라 실제 객체 데이터이름과 동일해야한다.

---




