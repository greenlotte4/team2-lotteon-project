<img align="center" width="100%" alt="header:Lotteon" src="https://capsule-render.vercel.app/api?type=waving&height=150&color=0:e99ab5,100:f9cad4&text=Lotte%20ON&desc=Team%202%20Project&descAlign=41&descAlignY=34&fontColor=E60029&reversal=false&animation=fadeIn&fontAlignY=64&section=header">


## 프로젝트 개요
롯데이커머스의 웹 쇼핑몰 프로젝트 **Lotte On**은 고객에게 다양한 상품을 제공하고 최고의 쇼핑 경험을 제공하기 위해 개발되고 있는 온라인 플랫폼입니다. 본 프로젝트는 사용자 친화적인 인터페이스를 통해 쇼핑이 보다 쉽고 편리하게 이루어질 수 있도록 설계되었습니다.

<div align=center> 

### [LotteOn - 더 나은 쇼핑의 발견](http://ec2-13-124-94-213.ap-northeast-2.compute.amazonaws.com:8080/)

</div>

## Team 2
- **팀장**: 이상훈
- **팀원**:
  - 박경림
  - 신승우
  - 박연화
  - 김주경
  - 김민희
  - 하정훈


## 프로젝트 진행
- **현재 버전**: 0.1.1-SNAPSHOT
- **첫 번째 커밋**: 프로젝트 디렉토리 구조 설계 완료, 레이아웃 구성, 엔터티 및 디비설계
- **커밋 주의사항**: yml 파일은 ignore 시켜놨으니 각자 yml파일 자기 DB로 구성 (없으면 팀장에게 yml파일 요청)


## 작업 관리
- **작업 진행상황 공유**
  - GitHub Project - 작업 진행상황 공유 
  - Google Sheets - 참고 데이터 공유
    - Lotte On 사이트맵 (화면 구축 단계에서 작업자 파악)
    - Entity 설계서 (DB 정보 구조 파악)
    - Url 설계서 (공통 url 및 request/response 데이터 파악)
    - html/css 유의사항
- **일간 프로젝트 작업 패턴** 
  - 오전 12:30 /오후 17:30 작업물 통합 
  - 17:30 코드 리뷰 & 배포 (매일)
  - Notion 작업일지 & 배포일지 작성(매일)


## 기술 스택
- ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=flat-square&logo=openjdk&logoColor=white)
- ![Spring](https://img.shields.io/badge/SpringBoot-%236DB33F.svg?style=flat-square&logo=spring&logoColor=white) ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=flat-square&logo=Thymeleaf&logoColor=white)
- ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=flat-square&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=flat-square&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=flat-square&logo=javascript&logoColor=%23F7DF1E)
- ![JPA](https://img.shields.io/badge/JPA-%236DB33F.svg?style=flat-square&logoColor=white)
- ![MySQL](https://img.shields.io/badge/MySQL-4479A1.svg?style=flat-square&logo=mysql&logoColor=white)
- ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=flat-square&logo=docker&logoColor=white)
- ![Redis](https://img.shields.io/badge/Redis-%23DD0031.svg?style=flat-square&logo=redis&logoColor=white) ![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=flat-square&logo=mongodb&logoColor=white)
- ![Git](https://img.shields.io/badge/git-E44C30?style=flat-square&logo=git&logoColor=white)
- ![Github Action](https://img.shields.io/badge/Github%20Action-121013?style=flat-square&logo=github&logoColor=white) ![AWS](https://img.shields.io/badge/AmazonAWS-%23FF9900.svg?style=flat-square&logo=amazon-aws&logoColor=white)
  
Thanks For
- ![ChatGPT](https://img.shields.io/badge/chatGPT-74aa9c?style=flat-square&logo=openai&logoColor=white) ![Bootstrap](https://img.shields.io/badge/Bootstrap-%238511FA.svg?style=flat-square&logo=bootstrap&logoColor=white)



## 버전 업데이트 정보
**0.0.1-SNAPSHOT**
>화면구현 및 로그인
>관리자 환경설정 배너 등록
>프로덕트 카테고리 출력


**0.0.2-SNAPSHOT**
>화면 구현 완료
>관리자 카테고리 기능(드래그앤 드롭)
>관리자 쿠폰 등록/수정/삭제/페이징/검색 기능 추가
>상점 등록 기능 구현
>커스터머 회원가입 기능 (유효성 검사 X)
>배너 등록/활성비활성/삭제/목록 기능(페이징 X)
>장바구니 아이템 추가


**0.0.3-SNAPSHOT**
>관리자 쿠폰 사용 / 기간만료 / 만료시일정기간 후 삭제 / 발급쿠폰 사용중지
>쿠폰발급현황 목록(페이징 및 검색 가능)
>쿠폰발급정보 상세조회
>관리자 환경설정 - 기본설정 전체
>관리자 게시판
>자주 묻는 질문 - 게시판 목록 / 1차, 2차 카테고리 출력
>공지사항 - 게시판 목록 / 글 작성 / 1차, 2차 카테고리 출력 
>장바구니 아이템 추가
>상품 등록 / 상품 옵션 등록 / 상품 등록시 카테고리 선택
>판매자 회원가입(유효성검사 X)
>이용약관 출력


**0.0.4-SNAPSHOT**
>일반회원가입
>관리자 환경설정사항 출력(푸터, 파비콘, 로고) / 버전관리 등록 / 버전관리 조회 / 버전관리 목록 출력 / 버전관리 선택 삭제
>장바구니 리스트 출력 / 장바구니 선택 상품 삭제
>관리자 게시판
>공지사항 수정 / 공지사항 삭제(선택삭제) / 공지사항 정렬
>관리자 상품목록 출력 / 상품등록 오류 수정
>출석이벤트(연속 7일 출석 이벤트/매달 초기화) / 회원가입 적립포인트 / 포인트 내역 출력


**0.0.5-SNAPSHOT**
>주문서 출력
>기간별 포인트 내역 조회
>배송지 설정 (삭제/수정/추가)
>로그인 배너 출력 / 이용약관 내용 수정
>관리자 회원관리 목록 출력
>관리자 상품 목록 출력 / 삭제 / 검색 (판매자 관리자 구분)
>관리자 자주 묻는 질문 글작성 / 글 목록 카테고리 적용 / 글 보기


**0.0.6-SNAPSHOT**
> 주문하기 기능
> 관리자 회원가입 목록 출력 / 페이징 추가
> 아이디/비밀번호 찾기 페이지 추가
> 자주 묻는 질문 작성 / 목록
> 공지사항 작성 / 목록 / 수정 / 삭제
> 상품 옵션 데이터베이스 수정
> 상품목록 출력(카테고리 구분) / 상품 상세 페이지 연결

**0.0.7-SNAPSHOT**
> 관리자 회원 수정 페이지 데이터 출력
> 주문완료 페이지 출력, 마이페이지 주문목록 출력 / 주문 시 포인트 처리
> 이용약관 / 버전 정보 캐싱 처리
> 메인 배너 슬라이드 기능
> 관리자 자주 묻는 질문 글목록 / 수정 / 삭제
> 고객센터 공지사항 목록 출력
> 상품 상세보기 출력(옵션 및 추가금액 출력)
> 채용공고 목록 / 등록 / 수정 / 검색 / 상세보기
> 카테고리 출력 DB Redis로 변경
> 관리자 방문자수 기능
> 상점별 쿠폰 발급 기능
> 아이디 찾기 / 비밀번호 변경하기

**0.0.8-SNAPSHOT**
> 관리자 주문목록 출력 / 상세보기 출력
> 상품 옵션 추가 / 상품 리스트 정렬
> 관리자 회원 수정 상세 보기
> 배너 시간대 캐싱
> 고객센터 문의하기 글 작성 / 문의 답변 / 관리자 문의하기 목록 출력
> 실시간 검색어
> 메인 상품 검색

**0.0.9-SNAPSHOT**
> 관리자 기본설정 롤백
> 공지사항 글 보기 / 검색 
> 상품 옵션 / 리스트 정렬
> 회원가입 유효성 검사
> 연관검색어 기능
> mongo DB 환경 구축
> 문의하기 글 목록 / 글 보기

**0.1.0-SNAPSHOT**
> 관리자 주문목록 배송하기
> 바로 구매 페이지 출력
> 상품 등록 및 카테고리 / 고객센터 / 관리자 유저목록 / 카트 오더 오류 수정
> 자동로그인 
> 공지사항 / 문의하기 카테고리별 검색 / 관리자 공지사항 글 보기
> 관리자 배송현황 목록 출력 / 검색 / 상세 정보 출력

**0.1.1-SNAPSHOT**
> 상품 이미지 출력
> 마이 페이지 주문목록 기간별 검색
> 관리자 회원목록 회원 수정
> 소셜 로그인(kakao)
> 고객센터 자주 묻는 질문 글 목록 /  글 보기
> 상품 등록 및 상품 상세 보기 오류 수정 
> 상품 옵션별 자동 조합
> 추천 연관 상품
> 쿠폰 오류 수정


<img align="center" width="100%" alt="header:Lotteon" src="https://capsule-render.vercel.app/api?type=waving&height=150&color=0:e99ab5,100:f9cad4&descAlign=41&descAlignY=34&fontColor=E60029&reversal=false&animation=fadeIn&fontAlignY=64&section=footer">
# lotteonProject
# team2-lotteon-project
# team2-lotteon-project
