# 브라우저 주소창에 www.naver.com을 입력하면 일어나는 일  

1. hosts 파일 확인
2. DNS Cache 확인
3. DNS서버에 질의(PC설정에따라 방법이 다를 수 있다)
4. IP 획득
5. naver IP로 TCP 연결 시도
6. 연결 성공시 HTTP Request
7. HTTP Response 

---

# 심화

### GSLB or CDN
GSLB : CDN은 IP 요청을 받으면 요청 IP에 전달해주기 가장 적절한 서버의 IP를 알려준다.  
백엔드 개발자라면, 사용자가 접속중이던 서버에 장애가 났을때 로그인 세션을 다른 서버에 어떻게 이어나갈지에 대한 고민이 필요하다.  



