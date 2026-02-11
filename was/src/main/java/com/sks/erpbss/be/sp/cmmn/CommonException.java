package com.sks.erpbss.be.sp.cmmn;

import lombok.Getter;
import lombok.Setter;

/**
 * 서비스 전반에서 사용하는 공통 예외 객체.
 *
 * 구성 의도:
 * - statusCode: 클라이언트에 전달할 상태 코드
 * - msgTyp: 메시지 성격 구분(I: 안내, E: 오류)
 * - messageParams: messageSource 코드 치환용 파라미터
 * - RuntimeException의 message: 메시지 코드 또는 원문 메시지
 */
@Getter
@Setter
public class CommonException extends RuntimeException {

    /**
     * 응답 메시지 타입 정의.
     * key는 실제 응답/분기 로직에서 비교값으로 사용됩니다.
     */
    @Getter
    public enum MsgTypEnum {
        TOAST("I", "웹토스트(알림)"),
        ERROR("E", "오류반환");

        // 분기 처리용 코드값
        private String key;
        // 운영/문서 확인용 설명
        private String value;

        private MsgTypEnum(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    // 응답 상태 코드
    private int statusCode;
    // 메시지 타입(I/E)
    private String msgTyp;
    // 메시지 코드 치환 파라미터
    private Object[] messageParams;

    /**
     * 상태코드 + 메시지 + 타입을 직접 지정하는 기본 생성자.
     */
    public CommonException(int statusCode, String message, String msgTyp) {
        this(statusCode, message, msgTyp, null);
    }

    /**
     * 상태코드 + 메시지 + 타입 + 치환 파라미터를 모두 지정하는 생성자.
     */
    public CommonException(int statusCode, String message, String msgTyp, Object[] messageParams) {
        super(message);
        this.statusCode = statusCode;
        this.msgTyp = msgTyp;
        this.messageParams = messageParams;
    }

    /**
     * 상태코드와 메시지만 전달할 때 사용.
     * 메시지 타입은 ERROR(E) 기본값을 사용합니다.
     */
    public CommonException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.msgTyp = MsgTypEnum.ERROR.getKey();
    }

    /**
     * 메시지만 전달할 때 사용.
     * 상태코드는 500, 메시지 타입은 ERROR(E)로 기본 설정합니다.
     */
    public CommonException(String message) {
        super(message);
        this.statusCode = 500;
        this.msgTyp = MsgTypEnum.ERROR.getKey();
    }

    /**
     * 레거시 호출부 호환용 생성자.
     * (message, statusCode, msgTyp) 순서 사용 코드가 남아있을 때만 사용.
     */
    @Deprecated
    public CommonException(String message, int statusCode, String msgTyp) {
        super(message);
        this.statusCode = statusCode;
        this.msgTyp = msgTyp;
    }
}
