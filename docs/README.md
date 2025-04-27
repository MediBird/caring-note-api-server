# CaringNote Back-end README

## 기술 스택

 ### 주요 프레임워크 및 언어

| 구성요소      | 사용 기술                 |
| ------------- | ------------------------- |
| Language      | Java 21                   |
| Build         | Gradle 8.10.2             |
| Framework     | Spring Boot 3.3.4         |
| ORM           | Spring Data JPA, QueryDSL |
| MVC           | Spring Web                |
| Test          | Junit5                    |
| AI            | Spring AI                 |
| API documents | Swagger                   |

### 데이터베이스

| 요소  | 사용 기술         |
| ----- | ----------------- |
| RDBMS | postgresql 16.4.0 |
| Cache | caffeine          |

### 외부 연동 API

| 요소         | 외부 API                                                     |
| ------------ | ------------------------------------------------------------ |
| SpeechToText | [naver cloud  clova speech api](https://www.ncloud.com/product/aiService/clovaSpeech) |
| TextAnalysis | openai api                                                   |



## 프로젝트 아키텍처

### 패키지 구조

* 도메인 별로 패키지 분리(DDD는 아님)
* 도메인 내에서 3 layer architecture로 구성

```sh
📦 com.springboot.api
├── common              
│   ├── config           # Spring Boot 실행 시 필요한 config(캐시, 시큐리티 스케쥴러 등)
│   ├── exception        # 사용자 예외 정의 및 핸들러
│   ├── aspect           # api 호출 시 Logging,권한 관련 apsect
│   ├── message          # 사용자 정의 응답/에러 메시지
│   ├── convertor        # Spring security,jpa 에서 사용되는 convertor(권한, json2String 등)
│   ├── util             # api 개발 시 유용한 util (file 처리, date 처리)
│   ├── validator        # request input 관련 검증
├── counselcard          # 상담 카드 도메인(케어링 노트에서 기초 상담 관련) ⭐️⭐️
│   ├── controller
│   ├── service
│   ├── repository
├── counselee            # 내담자 도메인(케어링 노트 내 상담 받는 사람 관련)⭐️⭐️
│   ├── controller
│   ├── service
│   ├── repository
├── counselor            # 상담사 도메인(케어링 노트 내 약사 관련) ⭐️
│   ├── controller
│   ├── service
│   ├── repository
├── counselsession       # 상담 도메인(복약 상담 관련)⭐️⭐️⭐️
│   ├── controller
│   ├── service
│   ├── eventlistener
├── medication           # 복약 정보 도메인(의약물 검색 관련) ⭐️⭐️
│   ├── controller
│   ├── service
│   ├── eventlistener
├── infra.external       # 외부 API 연동 ⭐️⭐️
└── enums                # 공통 enum 정의

```

### 인증

* 케어링 노트 시스템은 인증과 관련된 대부분의 기능을 Keycloak으로 위임하여 사용
  * 자세한 내용은 infra repository 참고

#### 가입

![sign_in](./assets/signin.png)

#### 로그인

![log_in](./assets/login.png)

#### API 호출

![apicall](./assets/apicall.png)



## 환경 설정

### 사전 설치 

* java21
* ffmpeg
  * audio file convert cli tool
  * 사용 목적
    * STT/TA 진행시, client 에서 들어오는 webm file은
      clova speech api에서 지원하지 않기 때문에 mp4로 변환할때 사용 
* postgresql 16.4(옵션)
  * Bare-metal 환경에서 실행 시 필요
* Docker-compose(옵션)
  * container 환경에서 실행 시 필요

### properties 사전 설정

* application-secret.yaml

  * 중요한 정보들은 application-secret.yaml 으로 분리
    실제 동작 시키기 위해서는 아래 정보 기입 필요

  ```yaml
  spring:
    jpa:
      properties:
        hibernate:
          dialect: org.hibernate.dialect.PostgreSQLDialect
          jdbc:
            time_zone: UTC
          ddl-auto: update
    datasource:
      url: <<database-url>>
      driver-class-name: org.postgresql.Driver
      username: <<database-username>>
      password: <<database-password>>
  
    ai:
      openai:
        api-key: <<open-url>>
  
  stt:
    file:
      path:
        origin: <<stt-audio-origin-file-upload-path>>
        convert: <<stt-audio-convert-file-upload-path>>
        
  ffmpeg:
    path: <<ffmpeg-bin-path>>
  
  naver:
    clova:
      api-key: <<naver-clova-api-keyz>>
  ```

### local 환경 실행 방법

* Bare-metal
  ```sh
  ## 사전 설치 및 설정 이후
  ./gradlew clean build
  ./gradlew bootRun
  ```

  

* Docker-compose
  ```sh
  docker-compose up --build -d
  ```

  



## CI/CD

#### branch

* staging branch로 부터 작업 브랜치 생성하여 작업한다.
  * 작업 브랜치 명명 규칙
    * feature/
      * 신규 기능 개발
    * refactor/
      * 기능 개선
    * fix/
      * 결함 조치
  * commit 메시지 규칙 => 이모티콘은 기호에 따라 붙임
    * feat :
      * 신규 기능
    * fix : 
      * 버그 수정
    * refactor :
      * 기능 개선
    * chore :
      * 쓸모없는 주석등 코드 정리

#### CI

* 작업 브랜치에서 개발 후 gitHub에서 PR 요청 진행하여
  reviewer 중 1명이 승인하면 브랜치 담당자가 staging branch로 merge한다.

* staging branch로 merge 이후 특이 사항 없으면 배포 담당자 통해서 staging에서 main 브랜치로 merge
  
  * PR 요청서 양식
  
    ```
    🔍️ 이 PR을 통해 해결하려는 문제가 무엇인가요?
    
    ✨ 이 PR에서 핵심적으로 변경된 사항은 무엇일까요?
    
    🔖 핵심 변경 사항 외에 추가적으로 변경된 부분이 있나요?
    
    🙏 Reviewer 분들이 이런 부분을 신경써서 봐 주시면 좋겠어요
    
    🩺 이 PR에서 테스트 혹은 검증이 필요한 부분이 있을까요?
    
    테스트가 필요한 항목이나 테스트 코드가 추가되었다면 함께 적어주세요
    
    ```
    
  * PR 요청 시, label 설정하여 배포 시급성을 reviewer에게 인지시킴.
  
    * D-0 ~ D-4
    
  * Reviewer check list
  
    ```
    📌 PR 진행 시 이러한 점들을 참고해 주세요
    - Reviewer 분들은 코드 리뷰 시 좋은 코드의 방향을 제시하되, 코드 수정을 강제하지 말아 주세요.
    - Reviewer 분들은 좋은 코드를 발견한 경우, 칭찬과 격려를 아끼지 말아 주세요.
    - Review는 특수한 케이스가 아니면 Reviewer로 지정된 시점 기준으로 7일 이내에 진행해 주세요.
    - Comment 작성 시 Prefix로 P1, P2, P3, P4, P5 를 적어 주시면 Assignee가 보다 명확하게 Comment에 대해 대응할 수 있어요
        - P1 : 꼭 반영해 주세요 (Request Changes) - 이슈가 발생하거나 취약점이 발견되는 케이스 등
        - P2 : 반영을 적극적으로 고려해 주시면 좋을 것 같아요 (Comment)
        - P3 : 이런 방법도 있을 것 같아요~ 등의 사소한 의견입니다 (Chore)
        - P4: 반영해도 좋고 넘어가도 좋습니다 (Approve)
        - P5: 그냥 사소한 의견입니다 (Approve+Chore)
    ```

#### CD

* 케어링 노트는 현재 staging, main 2개의 환경으로 운영됨.

* staging branch에 작업 branch merge 되면
  gitAction 통해서 케어링 노트 서버에 반영됨.

  * gitAction Process

    * dockerfile 기반으로 image build

    * 생성된 imgae DockerHub에 업로드

      * 현재는 @**[jawsbaek](https://github.com/jawsbaek)**  의 docker hub repo로 업로드 됨

    * 업로드 이후 케어링노트 k8s에서 아래 cli 실행되며 운영 서버에 반영됨.
      ```sh
      kubectl apply -f api.yaml
      ```

* 배포 담당자 통해서 특정 주기로 staging branch를 main branch로 merge(release)함.

## 주요 서비스  

### AI 요약

* process
  ```mermaid
  sequenceDiagram
      participant Client
      participant Server
      participant NaverClova
      participant OpenAI
  
  Client ->> Server: audio file(webm) multipart 업로드
  Server ->> Server: ai_counsel_summarys 테이블에 초기 상태 저장(STT_PROGRESS)
  Server -->> Client: 저장 완료 응답
  
  Server ->> Server: audio file(webm) → mp4 변환
  Server ->> NaverClova: mp4 파일로 STT 호출
  NaverClova -->> Server: STT 결과 수신
  Server ->> Server: STT 결과 및 상태 저장(STT_COMPLETE)
  
  Server ->> Server: 유효한 발화자 선정
  Server ->> Server: 선정된 발화자 기준 데이터 필터링
  Server ->> Server: 프롬프트 생성 (STT 필터링 + 프롬프트 템플릿 + Few-shot learning)
  
  Server ->> OpenAI: 생성된 프롬프트로 GPT 호출 (GPT_PROGRESS)
  OpenAI -->> Server: GPT 응답 수신
  Server ->> Server: 응답 결과 저장 (GPT_COMPLETE)
  ```
* 관련 서비스
  * AICounselSummaryService.convertSpeechToText
  * AICounselSummaryService.AIanalyseText

* 관련 테이블
  * counsel_sessions
    * 복약 상담 테이블
  * ai_counsel_summarys
    * ai 요약 테이블
    * counsel_sessions 테이블과 1:1 관계
  * prompt_templates
    * 프롬프트 템플릿 관리 테이블
  * Prompt_learnings
    * few-shot learning을 위한 정보 관리 테이블
* 히스토리
  * 서비스 기획 시 STT와 TA 프로세스가 분리되어 있었으나
    하나의 프로세스로 통합되면서 코드가 지저분해진 상황.



## 케어링 노트 테이블 설계

#### 테이블 설계 Rule

* 테이블 설계는 JPA로 Entity 정의 하되
  Local 환경에서는 auto-update 하지만
  실제 운영환경에서는 none으로 설정되어 있어. 직접 DBMS 접속하여 테이블 생성해야함.

* 현재 테이블 명명은 복수형(ex counselors)으로 되어 있으나
  앞으로 신규 테이블 생성 시 명명은 단수형으로 한다.

* 테이블의 PK는 application에서 ULID로 생성하여 저장(auto increment X)
  * BaseEntity.onCreate 메소드 참고
* 테이블 PK명명은  **id**로 함.
  * PK로 다른 테이블 참조 시, 참조 테이블명_id로 명명한다.
    * 참조 테이블명에서 s는 제외함.
      * ex) ai_counel_summary 테이블에서 counsel_sessions id를 PK로 설정 시
        **counsel_session_id** 명명함

#### 주요 테이블

* conuselors
  * 약사, 늘픔가치 관련자 등 caringNote 회원 정보
* counselSessions
  * 상담관련 main 테이블로 상담 관련 대다수 테이블은 해당 테이블 PK를 FK로 설정함.
* medications
  * 외부 오픈 데이터를 활용하여 구성한 의약물 메타 테이블
  * 케어링 노트 상담 시, 의약물 기록 시 자동완성을 위해 사용함.
* aiCounselSummarys
  * AI 요약 관련 테이블로 STT, TA 결과 저장하는 테이블

#### ERD

* 테이블이 많아 상세 내용은 .erd 파일 첨부함
*  [caringnote.erd](./assets/caringnote.erd) 



## API 정보

* https://caringnote.co.kr/api/swagger-ui/index.html#/



## 코드 작성 룰

### API 코드

* 관련 자세한 내용은 리포지토리 내 .cursor/rule 참고

### 테스트 코드

* 관련 자세한 내용은 리포지토리 내 .cursor/rule 참고
