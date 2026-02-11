package com.sks.erpbss.be.sp.cmmn;

import com.sks.erpbss.be.sp.cntrt.subscmmn.component.ComponentException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@Order(1)
@RestControllerAdvice(
    basePackages = "com.sks.erpbss.be.sp.**",
    annotations = {RestController.class}
)
public class CommonExceptionHandler {
    @Autowired
    private MessageSource messageSource;

    // 모든 예외 처리 공통으로 요청 식별 정보를 MDC에 기록한다.
    // 로그 집계 시스템에서 reqId/uri/method 기준으로 추적하기 위한 목적이다.
    void makeMdc(HttpServletRequest req) {
        MDC.put("reqId", UUID.randomUUID().toString());
        MDC.put("uri", req.getRequestURI());
        MDC.put("method", req.getMethod());
    }

    @ExceptionHandler(CommonException.class)
    public CommonResponse<Void> handleCommonException(CommonException e, HttpServletRequest req) {
        String chngMsg = "";

        makeMdc(req);
        log.error("statusCode={}, msgTyp={}, message={}", e.getStatusCode(), e.getMsgTyp(), e.getMessage());

        if (e.getMessageParams() != null && e.getMessageParams().length != 0) {
            chngMsg = messageSource.getMessage(e.getMessage(), e.getMessageParams(), null, Locale.getDefault());
        } else {
            chngMsg = messageSource.getMessage(e.getMessage(), new Object[] {}, null, Locale.getDefault());
        }

        if (e.getMsgTyp().equals(CommonException.MsgTypEnum.TOAST.getKey())) {
            return CommonResponse.toast(e.getStatusCode(), chngMsg);
        }
        // TODO(check): 이 핸들러 경로도 finally 또는 반환 직전에 MDC.clear()를 호출해 MDC 누수 없이 종료되는 구성이 일반적으로 맞습니다.
        return CommonResponse.error(e.getStatusCode(), chngMsg);
    }

    @ExceptionHandler(RuntimeException.class)
    public CommonResponse<Void> handleRuntimeException(RuntimeException e, HttpServletRequest req) {
        String chngMsg = "";

        // TODO(check): CommonException은 위의 전용 핸들러에서 처리하므로, RuntimeException 핸들러에서는 일반 RuntimeException만 처리되도록 분리하는 구성이 보통 더 명확합니다.
        if (e instanceof CommonException) {
            CommonException ce = (CommonException) e;

            if (ce.getMessageParams() != null && ce.getMessageParams().length != 0) {
                chngMsg = messageSource.getMessage(
                    ce.getMessage(),
                    ce.getMessageParams(),
                    HttpStatus.CONFLICT.getReasonPhrase(),
                    Locale.getDefault()
                );
            } else {
                chngMsg = messageSource.getMessage(
                    ce.getMessage(),
                    new String[] {},
                    HttpStatus.CONFLICT.getReasonPhrase(),
                    Locale.getDefault()
                );
            }

            makeMdc(req);
            log.error(e.getMessage());
            MDC.clear();

            if (ce.getMsgTyp().equals(CommonException.MsgTypEnum.TOAST.getKey())) {
                return CommonResponse.toast(ce.getStatusCode(), chngMsg);
            } else {
                return CommonResponse.error(ce.getStatusCode(), chngMsg);
            }

        } else {
            StringWriter sw = new StringWriter();
            // TODO(check): StringWriter만 생성하면 로그가 비게 되므로, log.error("...", e) 또는 e.printStackTrace(new PrintWriter(sw)) 후 sw 로그가 맞습니다.
            log.error(sw.toString());
        }
        // TODO(check): 일반 RuntimeException 경로는 빈 문자열 대신 기본 메시지(예: HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())를 내려주는 구성이 안전합니다.
        return CommonResponse.error(999, chngMsg);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponse<Void> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e,
        HttpServletRequest req
    ) {
        makeMdc(req);
        String msg = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .orElse("Validation failed");

        log.error("validation error: {}", msg, e);
        MDC.clear();

        return CommonResponse.error(
            // 검증 예외가 가진 HTTP 상태코드를 그대로 응답 statusCd로 사용
            e.getStatusCode().value(),
            e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
    }

    //feign.RetryableException
    @ExceptionHandler({feign.RetryableException.class})
    public CommonResponse<Void> handleFeignTimeoutException(feign.RetryableException e, HttpServletRequest req) {
        // 네트워크 타임아웃/재시도 계열 예외는 외부 연동 장애로 분류한다.
        // 따라서 BAD_GATEWAY(502)로 표준 응답 코드를 고정한다.
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.BAD_GATEWAY.value(), e.getMessage());
    }

    //bean component exception
    @ExceptionHandler(ComponentException.class)
    public CommonResponse<Void> handleComponentException(ComponentException e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        // Component Exception 처리
        return CommonResponse.error(e.getStatusCode(), e.getMessage());
    }

    //json을 dto로 변환할 수 없을 때 or dto에 없는 필드를 보냈을 때, 날짜 포맷 등등
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResponse<Void> handleUnreadableException(HttpMessageNotReadableException e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "요청 본문을 읽을 수 없습니다.");
    }

    //validate 조건 어긋났을 때, dto로 바인딩 할 때 실패
    @ExceptionHandler({BindException.class, ConstraintViolationException.class})
    public CommonResponse<Void> handleBindException(Exception e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "잘못된 요청 형식입니다.");
    }
    //NPE발생
    @ExceptionHandler(NullPointerException.class)
    public CommonResponse<Void> handleNPEException(Exception e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "일시적인 오류가 발생하였습니다.");
    }

    //헤더, 쿠키 관련 문제 발생시
    @ExceptionHandler(ServletRequestBindingException.class)
    public CommonResponse<Void> handleServletRequestBindingException(Exception e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "header 정보 바인딩 실패");
    }

    //request parameter 누락
    @ExceptionHandler({MissingServletRequestParameterException.class, TypeMismatchDataAccessException.class})
    public CommonResponse<Void> handleParameterException(Exception e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "요청 파라미터가 올바르지 않습니다.");
    }

    //지원하지 않는 http method 호출 get으로 보내는 등
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResponse<Void> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        makeMdc(req);
    log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(), "지원하지 않는 메소드 입니다.");
    }
    //지원하지 않는 컨텐트 타입
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CommonResponse<Void> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "지원하지 않는 미디어 타입입니다.");
    }

    //매핑된 url 없음
    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResponse<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.NOT_FOUND.value(), "요청 경로를 찾을 수 없습니다.");
    }

    //not null 컬럼에 null 저장 시도, unique 제약 위반, fk 제약 위반
    @ExceptionHandler({DataIntegrityViolationException.class, DuplicateKeyException.class})
    public CommonResponse<Void> handleIntegrityException(DataAccessException e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: ", e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.CONFLICT.value(), "데이터 무결성 제약에 위반되었습니다.");
    }

    //sql 에러
    @ExceptionHandler(DataAccessException.class)
    public CommonResponse<Void> handleDataAccessException(DataAccessException e, HttpServletRequest req) {
        // DataAccessException 계열을 단일 진입점으로 처리한다.
        // 세부 DB 구현 기술(JDBC/MyBatis)별 예외 타입에 결합되지 않도록 유지한다.
        makeMdc(req);
        log.error("error: ", e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터 접근 중 오류 발생");
    }
    //feignclient exception
    @ExceptionHandler(feign.FeignException.class)
    public CommonResponse<Void> handleFeignException(feign.FeignException e) {
        HttpStatus status = HttpStatus.resolve(e.status());
        if (status == null) status = HttpStatus.BAD_GATEWAY;
        return CommonResponse.error(status.value(), "feign api 호출 오류 발생");
    }

    //RestTemplate 사용시 에러 발생하면
    @ExceptionHandler(org.springframework.web.client.RestClientException.class)
    public CommonResponse<Void> handleRestClientException(org.springframework.web.client.RestClientException e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.BAD_GATEWAY.value(), "api 호출 오류 발생");
    }

    //잘못된 메소드 인자 전달, 객체 상태가 잘못된 경우
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public CommonResponse<Void> handleIllegalArgumentException(RuntimeException e, HttpServletRequest req) {
        makeMdc(req);
        log.error("error: {}", e.getMessage(), e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "요청을 처리할 수 없습니다.");
    }
    @ExceptionHandler(Exception.class)
    public CommonResponse<Void> handleEtcException(Exception e, HttpServletRequest req) {
        // 도메인/프레임워크/인프라 핸들러에서 처리되지 않은 예외의 최종 fallback.
        // 클라이언트에는 내부 구현 정보를 숨기고, 서버 로그에서만 상세 추적한다.
        makeMdc(req);
        log.error("Unhandled error : ", e);
        MDC.clear();
        return CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류가 발생했습니다.");
    }
}
