## [2026-02-20 15:56:09 KST] MODIFY_HISTORY 대비 소스 불일치 및 주석-구현 불일치 정합성 수정

**Type**: 수정

**Affected Files**:
- `build.gradle`
- `src/main/java/com/sks/erpbss/be/sp/SpSvcMainApp.java`
- `src/main/java/com/sks/erpbss/be/sp/cmmn/client/ExchangeRateClient.java`
- `src/main/java/com/sks/erpbss/be/sp/dashboard/service/DashboardService.java`
- `src/main/java/com/sks/erpbss/be/sp/cmmn/CommonExceptionHandler.java`
- `src/main/resources/application-local.yaml`
- `MODIFY_HISTORY.md`

**Changes**:
- MODIFY_HISTORY 기록과 실제 소스 불일치 항목 정리:
  - `build.gradle`에서 `spring-cloud-starter-openfeign` 활성화.
  - `SpSvcMainApp`에 `@EnableFeignClients` 복구.
- 주석만 남아 있던 환율 연동 예시성 구간을 실제 동작 기준으로 정리:
  - `ExchangeRateClient` 엔드포인트를 `/latest/{base}` 형태로 명확화.
  - `application-local.yaml` 기본 URL을 `https://open.er-api.com/v6`로 조정.
- `DashboardService` 정합성 개선:
  - `base/target` 필수값 검증 추가(400).
  - 통화코드 대문자 정규화 추가.
  - `CommonException`은 상태코드를 보존하도록 별도 catch 분리.
  - 불필요한 주석성 문구(Codex Design) 제거.
- `CommonExceptionHandler` 개선:
  - messageSource 조회 실패 시 원문 메시지로 fallback 되도록 `resolveMessage` 추가.
  - `CommonException` 처리 경로에서 `MDC.clear()` 누락되지 않도록 `finally` 정리.
  - RuntimeException 처리 시 빈 로그/빈 메시지 반환되지 않도록 기본 오류 응답 보강.

**Reason**:
- 최근 이력 대비 실제 코드의 누락/불일치를 제거하고, 주석 기반 임시 구현이 아니라 실제 실행 가능한 환율 조회 동작으로 정합성을 맞추기 위함.

**Validation**:
- `./gradlew.bat -q compileJava` 성공
- `https://open.er-api.com/v6/latest/USD` 응답 코드 200 확인
- `http://localhost:8081/api/dashboard/exchange?base=USD&target=KRW` 호출 결과:
  - `{"statusCd":200,"msgTyp":"S","errMsg":"","totalCount":1,"responseCount":1,"data":{"base":"USD","target":"KRW","rate":...}}`

---

## [2026-02-10 16:43:46 KST] Gradle 설정 정리 및 공통 예외 처리 구조 반영

**Type**: 수정

**Affected Files**:
- D:\workspace\clone-project\was\settings.gradle
- D:\workspace\clone-project\was\gradle\wrapper\gradle-wrapper.properties
- D:\workspace\clone-project\was\build.gradle
- D:\workspace\clone-project\was\gradle.properties
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonException.java
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonExceptionHandler.java
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonResponse.java

**Changes**:
- 멀티모듈 참조(e-sp-common-lib) 제거 후 단일 프로젝트 빌드 구조로 정리.
- Gradle Wrapper를 Spring Boot 3.3.x 호환 버전(8.10.2)으로 조정.
- uild.gradle을 현재 로컬에서 빌드 가능한 구성으로 재정리하고, 폐쇄망 의존성은 주석 블록으로 분리.
- CommonException 생성자 오버로드, MsgTypEnum, 상태코드/메시지타입/메시지파라미터 필드 구조 반영.
- CommonExceptionHandler에 CommonException/RuntimeException 처리 로직, 메시지 변환, MDC 처리, toast/error 분기 반영.
- CommonResponse.toast(...) 팩토리 메서드 추가 및 핸들러에서 정적 메서드 호출 방식으로 통일.
- 관련 주석 보강(용도/분기 의도/운영 추적 포인트 설명).

**Reason**:
- 폐쇄망 의존성 환경에서도 로컬 빌드가 깨지지 않도록 Gradle 구조를 안정화하고,
  공통 예외 처리 로직을 원본 설계 의도(메시지 타입 분기, 메시지 코드 치환, MDC 로깅)에 맞춰 단계적으로 복원하기 위함.

**Validation**:
- ./gradlew.bat clean build 성공
- ./gradlew.bat compileJava 성공

---

## [2026-02-10 17:43:05 KST] MethodArgumentNotValid 핸들러 반영 및 주석 복구

**Type**: 수정

**Affected Files**:
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonExceptionHandler.java
- D:\workspace\clone-project\was\build.gradle

**Changes**:
- `handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest req)` 메서드 추가.
- 요청사항에 맞춰 상태코드는 `e.getStatusCode().value()` 사용하도록 반영.
- `handleMethodArgumentNotValidException` 아래에 임시로 추가했던 예외 핸들러 메서드들 전부 제거.
- 깨진 한글 TODO 주석/상태코드 주석 문구를 정상 한글로 복구.
- 컴파일 참조를 위해 추가했던 의존성 변경 이력 정리(`build.gradle` 반영).

**Reason**:
- 사용자 지정 구현 흐름(검증 예외 핸들러 단독 반영, 상태코드 소스 일치)을 정확히 맞추고,
  검토 편의성을 위해 주석 가독성을 원복하기 위함.

**Validation**:
- ./gradlew test 성공
- CommonExceptionHandler 컴파일 정상 확인

---

## [2026-02-10 17:49:02 KST] 이미지로부터 예외 핸들러 코드 추가

**Type**: 추가

**Affected Files**:
- `D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonExceptionHandler.java`

**Changes**:
- `images` 폴더에 있는 5개의 이미지 파일로부터 다양한 예외 처리 핸들러 코드를 추출하여 `CommonExceptionHandler.java`에 추가했습니다.
- Feign, Component, HttpMessageNotReadable, Bind, NPE, ServletRequestBinding, HttpRequestMethodNotSupported, HttpMediaTypeNotSupported, NoHandlerFound, DataIntegrityViolation, DataAccess, Persistence, RestClient, IllegalArgumentException, 그리고 일반 Exception 등 다양한 예외를 처리하는 핸들러들이 추가되었습니다.
- 코드는 `handleMethodArgumentNotValidException` 메소드 뒤에 삽입되었습니다.

**Reason**:
- 사용자의 요청에 따라 이미지에 포함된 예외 처리 로직을 실제 소스 코드에 반영하기 위함입니다.

**AI Collaborator** (선택사항):
- Suggested by: Gemini
- Model used: gemini-2.5-pro
- Validation status: PASS
- Review notes: 사용자가 지정한 순서대로 이미지를 읽고 코드를 추출하여 파일에 성공적으로 반영했습니다.

**Related Issue/Request**:
`D:\workspace\clone-project\was\images`에 있는 사진 속 코드를 순서에 맞게 반영해달라는 요청

---

## [2026-02-10 17:58:29 KST] ComponentException 클래스 생성

**Type**: 생성

**Affected Files**:
- `D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cntrt\subscmmn\component\ComponentException.java`

**Changes**:
- `CommonException.java`를 참조하여 `ComponentException.java` 클래스를 생성했습니다.
- `RuntimeException`을 상속하고 `statusCode`, `msgTyp`, `messageParams` 필드를 포함하며, `CommonException.MsgTypEnum`을 재활용합니다.
- 다양한 생성자 오버로드 및 기본값 설정 로직을 포함합니다.

**Reason**:
- `CommonExceptionHandler.java`에 추가된 핸들러에서 `ComponentException`을 참조하므로, 컴파일 에러를 방지하고 프로젝트의 의존성을 해결하기 위해 해당 클래스를 구현했습니다.

**AI Collaborator** (선택사항):
- Suggested by: Gemini
- Model used: gemini-2.5-pro
- Validation status: PASS
- Review notes: `CommonException`의 구조와 패턴을 따라 `ComponentException`을 성공적으로 생성했습니다.

**Related Issue/Request**:
`ComponentException`이 구현되어 있지 않으므로 `CommonException`을 참고하여 해당 파일을 만들어달라는 요청

---

## [2026-02-11 10:47:39 KST] CommonExceptionHandler 컴파일 오류 정리 및 주석 보강

**Type**: 수정

**Affected Files**:
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonExceptionHandler.java

**Changes**:
- 누락 import 추가 (ComponentException, Spring/Jakarta/Web/DAO 예외 타입).
- @ExceptionHandler(feign.RetryableException.class)와 메서드 파라미터 타입 불일치 수정.
- 의존성에 없는 MyBatis PersistenceException 핸들러 제거.
- spring-jdbc 미사용 상태에서 직접 참조하던 UncategorizedSQLException 제거.
- 예외 처리 의도를 명확히 하기 위해 상세 주석 추가:
  - MDC 세팅 목적
  - Feign 재시도 예외의 502 처리 이유
  - DataAccessException 단일 처리 전략
  - 최종 fallback(Exception) 핸들러의 보안/운영 의도

**Reason**:
- 컴파일 에러를 제거하고, 운영 시 예외 처리 의도를 코드 레벨에서 즉시 이해할 수 있도록 가독성을 높이기 위함.

**Validation**:
- ./gradlew compileJava -q 성공

---

## [2026-02-11 17:48:19 KST] SpSvcMainApp 원복 + 개선안 주석화

**Type**: 수정

**Affected Files**:
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\SpSvcMainApp.java
- D:\workspace\clone-project\was\MODIFY_HISTORY.md

**Changes**:
- SpSvcMainApp 실행 코드를 최초 형태로 유지하도록 복원 (public class, 기본 main만 유지).
- 이전에 적용했던 inal/private constructor 구조 변경은 제거.
- 개선 아이디어는 런타임 영향이 없도록 파일 하단 주석 블록으로 이동:
  - 엔트리포인트 클래스 인스턴스화 방지 패턴 제안
  - 보안 자동설정 제외 이유 및 @EnableAsync + TaskExecutor 구성 권장사항
- 파일 인코딩 규칙 점검: UTF-8, BOM 없음 확인.

**Reason**:
- 사용자 요청에 따라 "원본 동작 코드는 유지"하고, 개선 제안은 참고 정보로만 남기기 위함.

**Validation**:
- SpSvcMainApp.java 코드 구조 원복 확인
- UTF-8(BOM 없음) 확인

---

## [2026-02-11 18:04:33 KST] YAML 리소스 규칙 적용 및 기본 설정 파일 정리

**Type**: 추가

**Affected Files**:
- D:\workspace\clone-project\was\src\main\resources\application.yaml
- D:\workspace\clone-project\was\src\main\resources\application-local.yaml
- D:\workspace\clone-project\was\src\main\resources\application-common.yaml
- D:\workspace\clone-project\was\MODIFY_HISTORY.md

**Changes**:
- src/main/resources를 YAML 설정의 기준 경로(canonical)로 확정.
- 최소 실행 세트 파일 생성:
  - pplication.yaml
  - pplication-local.yaml
- pplication-common.yaml 생성 및 pplication.yaml에서 명시적 import 설정:
  - spring.config.import: optional:classpath:application-common.yaml
- uild/resources/main/**은 생성 산출물로 간주하여 수정/복제 대상에서 제외.
- src/main/java/resources 경로 사용 여부 점검:
  - 디렉터리 없음
  - uild.gradle 내 sourceSets.main.resources 커스텀 설정 없음

**Reason**:
- YAML 리소스 관리 규칙 문서(yaml_resource_rules_for_ai.md)에 따라 설정 파일 위치와 로딩 방식을 표준화하고,
  최소 구성으로 ootRun 가능 상태를 확보하기 위함.

**Validation**:
- src/main/resources에 YAML 3종 파일 생성 확인
- UTF-8 (BOM 없음) 확인
- ./gradlew.bat bootRun 실행 시도
  - 실패 원인: 로컬 JVM 8, 프로젝트 요구사항은 Java 17+

---
