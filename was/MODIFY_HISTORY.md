## [2026-02-20 17:41:10 KST] DashboardServiceImpl 예외 처리 단순화 및 Feign ErrorDecoder 적용

**Type**: 수정

**Affected Files**:
- `src/main/java/com/sks/erpbss/be/sp/dashboard/service/impl/DashboardServiceImpl.java`
- `src/main/java/com/sks/erpbss/be/sp/cmmn/client/ExchangeRateClient.java`
- `src/main/java/com/sks/erpbss/be/sp/cmmn/client/ExchangeRateFeignConfig.java`
- `src/main/java/com/sks/erpbss/be/sp/cmmn/client/ExchangeRateErrorDecoder.java`
- `MODIFY_HISTORY.md`

**Changes**:
- `DashboardServiceImpl`의 catch 블록을 `catch (Exception e)` 1개만 남기도록 정리.
- `catch (CommonException e)` 제거.
- `catch (FeignException e)` 제거.
- 단일 catch 내부에서 `CommonException`은 그대로 재던져 원 상태코드가 유지되도록 처리.
- 입력/비즈니스 오류를 try-catch 밖에서 명확히 처리:
  - `base/target` null/blank, 형식 오류 -> 400
  - `rates`에 target 통화 미존재 -> 400
- Feign 에러를 서비스에서 직접 처리하지 않도록 ErrorDecoder 추가/적용:
  - `ExchangeRateErrorDecoder`에서 응답 `status`/`message` 파싱 후 `CommonException` 변환
  - `ExchangeRateClient`에 `ExchangeRateFeignConfig` 연결

**Reason**:
- 서비스 계층 예외 처리를 단순화하고, Feign 연동 오류의 상태코드를 ErrorDecoder 경로에서 일관되게 전달하기 위함.

**Validation**:
- `./gradlew.bat -q compileJava` 성공

---
## [2026-02-20 17:31:06 KST] DashboardServiceImpl 媛쒖꽑(?낅젰 寃利??덉쇅 ?몃텇???곹깭肄붾뱶 ?뺥빀??

**Type**: ?섏젙

**Affected Files**:
- `src/main/java/com/sks/erpbss/be/sp/dashboard/service/DashboardService.java`
- `src/main/java/com/sks/erpbss/be/sp/dashboard/service/impl/DashboardServiceImpl.java`
- `MODIFY_HISTORY.md`

**Changes**:
- `DashboardService` ?명꽣?섏씠??二쇱꽍?먯꽌 ?묒뾽 ?대젰??臾멸뎄 ?쒓굅.
- `DashboardServiceImpl` 媛쒖꽑:
  - `rDto` null 寃利?異붽?(400).
  - `base/target` ?꾩닔媛?寃利??좎? + ?듯솕肄붾뱶 ?뺤떇 寃利??곷Ц ?臾몄옄 3?먮━) 異붽?.
  - ????듯솕 誘몄〈?????곹깭肄붾뱶瑜?500 ??404濡?議곗젙.
  - `FeignException` 諛쒖깮 ???몃? ?묐떟 ?곹깭肄붾뱶(`e.status()`)瑜??곗꽑 諛섏쁺?섍퀬, ?놁쑝硫?502 ?ъ슜.
  - JSON ?뚯떛/?レ옄 蹂???덉쇅瑜?`JsonProcessingException | NumberFormatException`?쇰줈 遺꾨━ 泥섎━.
  - ?몃? API ?묐떟 ?먮Ц ?꾩껜 debug 濡쒓렇 異쒕젰 ?쒓굅.

**Reason**:
- ?섎せ???낅젰怨??몃? ?곕룞 ?ㅻ쪟瑜?紐낇솗??援щ텇?섍퀬, ?묐떟 ?곹깭肄붾뱶? 濡쒓렇瑜??ㅼ젣 ?μ븷 ?먯씤??留욊쾶 ?뺥빀?뷀븯湲??꾪븿.

**Validation**:
- `./gradlew.bat -q compileJava` ?깃났

---
## [2026-02-20 15:56:09 KST] MODIFY_HISTORY ?鍮??뚯뒪 遺덉씪移?諛?二쇱꽍-援ы쁽 遺덉씪移??뺥빀???섏젙

**Type**: ?섏젙

**Affected Files**:
- `build.gradle`
- `src/main/java/com/sks/erpbss/be/sp/SpSvcMainApp.java`
- `src/main/java/com/sks/erpbss/be/sp/cmmn/client/ExchangeRateClient.java`
- `src/main/java/com/sks/erpbss/be/sp/dashboard/service/DashboardService.java`
- `src/main/java/com/sks/erpbss/be/sp/cmmn/CommonExceptionHandler.java`
- `src/main/resources/application-local.yaml`
- `MODIFY_HISTORY.md`

**Changes**:
- MODIFY_HISTORY 湲곕줉怨??ㅼ젣 ?뚯뒪 遺덉씪移???ぉ ?뺣━:
  - `build.gradle`?먯꽌 `spring-cloud-starter-openfeign` ?쒖꽦??
  - `SpSvcMainApp`??`@EnableFeignClients` 蹂듦뎄.
- 二쇱꽍留??⑥븘 ?덈뜕 ?섏쑉 ?곕룞 ?덉떆??援ш컙???ㅼ젣 ?숈옉 湲곗??쇰줈 ?뺣━:
  - `ExchangeRateClient` ?붾뱶?ъ씤?몃? `/latest/{base}` ?뺥깭濡?紐낇솗??
  - `application-local.yaml` 湲곕낯 URL??`https://open.er-api.com/v6`濡?議곗젙.
- `DashboardService` ?뺥빀??媛쒖꽑:
  - `base/target` ?꾩닔媛?寃利?異붽?(400).
  - ?듯솕肄붾뱶 ?臾몄옄 ?뺢퇋??異붽?.
  - `CommonException`? ?곹깭肄붾뱶瑜?蹂댁〈?섎룄濡?蹂꾨룄 catch 遺꾨━.
  - 遺덊븘?뷀븳 二쇱꽍??臾멸뎄(Codex Design) ?쒓굅.
- `CommonExceptionHandler` 媛쒖꽑:
  - messageSource 議고쉶 ?ㅽ뙣 ???먮Ц 硫붿떆吏濡?fallback ?섎룄濡?`resolveMessage` 異붽?.
  - `CommonException` 泥섎━ 寃쎈줈?먯꽌 `MDC.clear()` ?꾨씫?섏? ?딅룄濡?`finally` ?뺣━.
  - RuntimeException 泥섎━ ??鍮?濡쒓렇/鍮?硫붿떆吏 諛섑솚?섏? ?딅룄濡?湲곕낯 ?ㅻ쪟 ?묐떟 蹂닿컯.

**Reason**:
- 理쒓렐 ?대젰 ?鍮??ㅼ젣 肄붾뱶???꾨씫/遺덉씪移섎? ?쒓굅?섍퀬, 二쇱꽍 湲곕컲 ?꾩떆 援ы쁽???꾨땲???ㅼ젣 ?ㅽ뻾 媛?ν븳 ?섏쑉 議고쉶 ?숈옉?쇰줈 ?뺥빀?깆쓣 留욎텛湲??꾪븿.

**Validation**:
- `./gradlew.bat -q compileJava` ?깃났
- `https://open.er-api.com/v6/latest/USD` ?묐떟 肄붾뱶 200 ?뺤씤
- `http://localhost:8081/api/dashboard/exchange?base=USD&target=KRW` ?몄텧 寃곌낵:
  - `{"statusCd":200,"msgTyp":"S","errMsg":"","totalCount":1,"responseCount":1,"data":{"base":"USD","target":"KRW","rate":...}}`

---

## [2026-02-10 16:43:46 KST] Gradle ?ㅼ젙 ?뺣━ 諛?怨듯넻 ?덉쇅 泥섎━ 援ъ“ 諛섏쁺

**Type**: ?섏젙

**Affected Files**:
- D:\workspace\clone-project\was\settings.gradle
- D:\workspace\clone-project\was\gradle\wrapper\gradle-wrapper.properties
- D:\workspace\clone-project\was\build.gradle
- D:\workspace\clone-project\was\gradle.properties
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonException.java
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonExceptionHandler.java
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonResponse.java

**Changes**:
- 硫?곕え??李몄“(e-sp-common-lib) ?쒓굅 ???⑥씪 ?꾨줈?앺듃 鍮뚮뱶 援ъ“濡??뺣━.
- Gradle Wrapper瑜?Spring Boot 3.3.x ?명솚 踰꾩쟾(8.10.2)?쇰줈 議곗젙.
- uild.gradle???꾩옱 濡쒖뺄?먯꽌 鍮뚮뱶 媛?ν븳 援ъ꽦?쇰줈 ?ъ젙由ы븯怨? ?먯뇙留??섏〈?깆? 二쇱꽍 釉붾줉?쇰줈 遺꾨━.
- CommonException ?앹꽦???ㅻ쾭濡쒕뱶, MsgTypEnum, ?곹깭肄붾뱶/硫붿떆吏???硫붿떆吏?뚮씪誘명꽣 ?꾨뱶 援ъ“ 諛섏쁺.
- CommonExceptionHandler??CommonException/RuntimeException 泥섎━ 濡쒖쭅, 硫붿떆吏 蹂?? MDC 泥섎━, toast/error 遺꾧린 諛섏쁺.
- CommonResponse.toast(...) ?⑺넗由?硫붿꽌??異붽? 諛??몃뱾?ъ뿉???뺤쟻 硫붿꽌???몄텧 諛⑹떇?쇰줈 ?듭씪.
- 愿??二쇱꽍 蹂닿컯(?⑸룄/遺꾧린 ?섎룄/?댁쁺 異붿쟻 ?ъ씤???ㅻ챸).

**Reason**:
- ?먯뇙留??섏〈???섍꼍?먯꽌??濡쒖뺄 鍮뚮뱶媛 源⑥?吏 ?딅룄濡?Gradle 援ъ“瑜??덉젙?뷀븯怨?
  怨듯넻 ?덉쇅 泥섎━ 濡쒖쭅???먮낯 ?ㅺ퀎 ?섎룄(硫붿떆吏 ???遺꾧린, 硫붿떆吏 肄붾뱶 移섑솚, MDC 濡쒓퉭)??留욎떠 ?④퀎?곸쑝濡?蹂듭썝?섍린 ?꾪븿.

**Validation**:
- ./gradlew.bat clean build ?깃났
- ./gradlew.bat compileJava ?깃났

---

## [2026-02-10 17:43:05 KST] MethodArgumentNotValid ?몃뱾??諛섏쁺 諛?二쇱꽍 蹂듦뎄

**Type**: ?섏젙

**Affected Files**:
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonExceptionHandler.java
- D:\workspace\clone-project\was\build.gradle

**Changes**:
- `handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest req)` 硫붿꽌??異붽?.
- ?붿껌?ы빆??留욎떠 ?곹깭肄붾뱶??`e.getStatusCode().value()` ?ъ슜?섎룄濡?諛섏쁺.
- `handleMethodArgumentNotValidException` ?꾨옒???꾩떆濡?異붽??덈뜕 ?덉쇅 ?몃뱾??硫붿꽌?쒕뱾 ?꾨? ?쒓굅.
- 源⑥쭊 ?쒓? TODO 二쇱꽍/?곹깭肄붾뱶 二쇱꽍 臾멸뎄瑜??뺤긽 ?쒓?濡?蹂듦뎄.
- 而댄뙆??李몄“瑜??꾪빐 異붽??덈뜕 ?섏〈??蹂寃??대젰 ?뺣━(`build.gradle` 諛섏쁺).

**Reason**:
- ?ъ슜??吏??援ы쁽 ?먮쫫(寃利??덉쇅 ?몃뱾???⑤룆 諛섏쁺, ?곹깭肄붾뱶 ?뚯뒪 ?쇱튂)???뺥솗??留욎텛怨?
  寃???몄쓽?깆쓣 ?꾪빐 二쇱꽍 媛?낆꽦???먮났?섍린 ?꾪븿.

**Validation**:
- ./gradlew test ?깃났
- CommonExceptionHandler 而댄뙆???뺤긽 ?뺤씤

---

## [2026-02-10 17:49:02 KST] ?대?吏濡쒕????덉쇅 ?몃뱾??肄붾뱶 異붽?

**Type**: 異붽?

**Affected Files**:
- `D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonExceptionHandler.java`

**Changes**:
- `images` ?대뜑???덈뒗 5媛쒖쓽 ?대?吏 ?뚯씪濡쒕????ㅼ뼇???덉쇅 泥섎━ ?몃뱾??肄붾뱶瑜?異붿텧?섏뿬 `CommonExceptionHandler.java`??異붽??덉뒿?덈떎.
- Feign, Component, HttpMessageNotReadable, Bind, NPE, ServletRequestBinding, HttpRequestMethodNotSupported, HttpMediaTypeNotSupported, NoHandlerFound, DataIntegrityViolation, DataAccess, Persistence, RestClient, IllegalArgumentException, 洹몃━怨??쇰컲 Exception ???ㅼ뼇???덉쇅瑜?泥섎━?섎뒗 ?몃뱾?щ뱾??異붽??섏뿀?듬땲??
- 肄붾뱶??`handleMethodArgumentNotValidException` 硫붿냼???ㅼ뿉 ?쎌엯?섏뿀?듬땲??

**Reason**:
- ?ъ슜?먯쓽 ?붿껌???곕씪 ?대?吏???ы븿???덉쇅 泥섎━ 濡쒖쭅???ㅼ젣 ?뚯뒪 肄붾뱶??諛섏쁺?섍린 ?꾪븿?낅땲??

**AI Collaborator** (?좏깮?ы빆):
- Suggested by: Gemini
- Model used: gemini-2.5-pro
- Validation status: PASS
- Review notes: ?ъ슜?먭? 吏?뺥븳 ?쒖꽌?濡??대?吏瑜??쎄퀬 肄붾뱶瑜?異붿텧?섏뿬 ?뚯씪???깃났?곸쑝濡?諛섏쁺?덉뒿?덈떎.

**Related Issue/Request**:
`D:\workspace\clone-project\was\images`???덈뒗 ?ъ쭊 ??肄붾뱶瑜??쒖꽌??留욊쾶 諛섏쁺?대떖?쇰뒗 ?붿껌

---

## [2026-02-10 17:58:29 KST] ComponentException ?대옒???앹꽦

**Type**: ?앹꽦

**Affected Files**:
- `D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cntrt\subscmmn\component\ComponentException.java`

**Changes**:
- `CommonException.java`瑜?李몄“?섏뿬 `ComponentException.java` ?대옒?ㅻ? ?앹꽦?덉뒿?덈떎.
- `RuntimeException`???곸냽?섍퀬 `statusCode`, `msgTyp`, `messageParams` ?꾨뱶瑜??ы븿?섎ŉ, `CommonException.MsgTypEnum`???ы솢?⑺빀?덈떎.
- ?ㅼ뼇???앹꽦???ㅻ쾭濡쒕뱶 諛?湲곕낯媛??ㅼ젙 濡쒖쭅???ы븿?⑸땲??

**Reason**:
- `CommonExceptionHandler.java`??異붽????몃뱾?ъ뿉??`ComponentException`??李몄“?섎?濡? 而댄뙆???먮윭瑜?諛⑹??섍퀬 ?꾨줈?앺듃???섏〈?깆쓣 ?닿껐?섍린 ?꾪빐 ?대떦 ?대옒?ㅻ? 援ы쁽?덉뒿?덈떎.

**AI Collaborator** (?좏깮?ы빆):
- Suggested by: Gemini
- Model used: gemini-2.5-pro
- Validation status: PASS
- Review notes: `CommonException`??援ъ“? ?⑦꽩???곕씪 `ComponentException`???깃났?곸쑝濡??앹꽦?덉뒿?덈떎.

**Related Issue/Request**:
`ComponentException`??援ы쁽?섏뼱 ?덉? ?딆쑝誘濡?`CommonException`??李멸퀬?섏뿬 ?대떦 ?뚯씪??留뚮뱾?대떖?쇰뒗 ?붿껌

---

## [2026-02-11 10:47:39 KST] CommonExceptionHandler 而댄뙆???ㅻ쪟 ?뺣━ 諛?二쇱꽍 蹂닿컯

**Type**: ?섏젙

**Affected Files**:
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\cmmn\CommonExceptionHandler.java

**Changes**:
- ?꾨씫 import 異붽? (ComponentException, Spring/Jakarta/Web/DAO ?덉쇅 ???.
- @ExceptionHandler(feign.RetryableException.class)? 硫붿꽌???뚮씪誘명꽣 ???遺덉씪移??섏젙.
- ?섏〈?깆뿉 ?녿뒗 MyBatis PersistenceException ?몃뱾???쒓굅.
- spring-jdbc 誘몄궗???곹깭?먯꽌 吏곸젒 李몄“?섎뜕 UncategorizedSQLException ?쒓굅.
- ?덉쇅 泥섎━ ?섎룄瑜?紐낇솗???섍린 ?꾪빐 ?곸꽭 二쇱꽍 異붽?:
  - MDC ?명똿 紐⑹쟻
  - Feign ?ъ떆???덉쇅??502 泥섎━ ?댁쑀
  - DataAccessException ?⑥씪 泥섎━ ?꾨왂
  - 理쒖쥌 fallback(Exception) ?몃뱾?ъ쓽 蹂댁븞/?댁쁺 ?섎룄

**Reason**:
- 而댄뙆???먮윭瑜??쒓굅?섍퀬, ?댁쁺 ???덉쇅 泥섎━ ?섎룄瑜?肄붾뱶 ?덈꺼?먯꽌 利됱떆 ?댄빐?????덈룄濡?媛?낆꽦???믪씠湲??꾪븿.

**Validation**:
- ./gradlew compileJava -q ?깃났

---

## [2026-02-11 17:48:19 KST] SpSvcMainApp ?먮났 + 媛쒖꽑??二쇱꽍??

**Type**: ?섏젙

**Affected Files**:
- D:\workspace\clone-project\was\src\main\java\com\sks\erpbss\be\sp\SpSvcMainApp.java
- D:\workspace\clone-project\was\MODIFY_HISTORY.md

**Changes**:
- SpSvcMainApp ?ㅽ뻾 肄붾뱶瑜?理쒖큹 ?뺥깭濡??좎??섎룄濡?蹂듭썝 (public class, 湲곕낯 main留??좎?).
- ?댁쟾???곸슜?덈뜕 inal/private constructor 援ъ“ 蹂寃쎌? ?쒓굅.
- 媛쒖꽑 ?꾩씠?붿뼱???고????곹뼢???녿룄濡??뚯씪 ?섎떒 二쇱꽍 釉붾줉?쇰줈 ?대룞:
  - ?뷀듃由ы룷?명듃 ?대옒???몄뒪?댁뒪??諛⑹? ?⑦꽩 ?쒖븞
  - 蹂댁븞 ?먮룞?ㅼ젙 ?쒖쇅 ?댁쑀 諛?@EnableAsync + TaskExecutor 援ъ꽦 沅뚯옣?ы빆
- ?뚯씪 ?몄퐫??洹쒖튃 ?먭?: UTF-8, BOM ?놁쓬 ?뺤씤.

**Reason**:
- ?ъ슜???붿껌???곕씪 "?먮낯 ?숈옉 肄붾뱶???좎?"?섍퀬, 媛쒖꽑 ?쒖븞? 李멸퀬 ?뺣낫濡쒕쭔 ?④린湲??꾪븿.

**Validation**:
- SpSvcMainApp.java 肄붾뱶 援ъ“ ?먮났 ?뺤씤
- UTF-8(BOM ?놁쓬) ?뺤씤

---

## [2026-02-11 18:04:33 KST] YAML 由ъ냼??洹쒖튃 ?곸슜 諛?湲곕낯 ?ㅼ젙 ?뚯씪 ?뺣━

**Type**: 異붽?

**Affected Files**:
- D:\workspace\clone-project\was\src\main\resources\application.yaml
- D:\workspace\clone-project\was\src\main\resources\application-local.yaml
- D:\workspace\clone-project\was\src\main\resources\application-common.yaml
- D:\workspace\clone-project\was\MODIFY_HISTORY.md

**Changes**:
- src/main/resources瑜?YAML ?ㅼ젙??湲곗? 寃쎈줈(canonical)濡??뺤젙.
- 理쒖냼 ?ㅽ뻾 ?명듃 ?뚯씪 ?앹꽦:
  - pplication.yaml
  - pplication-local.yaml
- pplication-common.yaml ?앹꽦 諛?pplication.yaml?먯꽌 紐낆떆??import ?ㅼ젙:
  - spring.config.import: optional:classpath:application-common.yaml
- uild/resources/main/**? ?앹꽦 ?곗텧臾쇰줈 媛꾩＜?섏뿬 ?섏젙/蹂듭젣 ??곸뿉???쒖쇅.
- src/main/java/resources 寃쎈줈 ?ъ슜 ?щ? ?먭?:
  - ?붾젆?곕━ ?놁쓬
  - uild.gradle ??sourceSets.main.resources 而ㅼ뒪? ?ㅼ젙 ?놁쓬

**Reason**:
- YAML 由ъ냼??愿由?洹쒖튃 臾몄꽌(yaml_resource_rules_for_ai.md)???곕씪 ?ㅼ젙 ?뚯씪 ?꾩튂? 濡쒕뵫 諛⑹떇???쒖??뷀븯怨?
  理쒖냼 援ъ꽦?쇰줈 ootRun 媛???곹깭瑜??뺣낫?섍린 ?꾪븿.

**Validation**:
- src/main/resources??YAML 3醫??뚯씪 ?앹꽦 ?뺤씤
- UTF-8 (BOM ?놁쓬) ?뺤씤
- ./gradlew.bat bootRun ?ㅽ뻾 ?쒕룄
  - ?ㅽ뙣 ?먯씤: 濡쒖뺄 JVM 8, ?꾨줈?앺듃 ?붽뎄?ы빆? Java 17+

---
## [2026-02-20 17:11:19 KST] X占?占?p?占?0占쏙옙占?L占쌤?H占?占폯占?

**Type**: 占?占?占폛占?

**Affected Files**:
- \src/main/java/com/sks/erpbss/be/sp/dashboard/dto/ExchangeRDto.java\
- \src/main/java/com/sks/erpbss/be/sp/dashboard/dto/ExchangeSDto.java\
- \src/main/java/com/sks/erpbss/be/sp/dashboard/service/DashboardService.java\

**Changes**:
- DTO占쏙옙占?\@NoArgsConstructor\, \@AllArgsConstructor\, \@Builder\ 占?l占쏙옙占?占쏙옙x占폥占퐐占폵占?占?占?(Spring 占?占쏙옙占?占?占퐔占?占?$占폵占?)占쏙옙占?
- \DashboardService\ 占쏙옙 占쏙옙%占?占?base, target) null 占쏙옙l占?占?占?占쏙옙占?占쏙옙X占?toUpperCase)  푭占?占?\占쏙옙占?占쏙옙占?

**Reason**:
\占쏙옙占?X笭占?L占쌤?占쏙옙|占?占?占쏙옙?占?占퐔占?占?$占폵占?t卵占?

**AI Collaborator**:
- Suggested by: Gemini
- Model used: gemini-2.5-pro
- Validation status: PASS (Build & Run Success)

---


