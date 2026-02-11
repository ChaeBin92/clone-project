package com.sks.erpbss.be.sp.cntrt.subscmmn.component;

import com.sks.erpbss.be.sp.cmmn.CommonException; // CommonException의 MsgTypEnum을 재활용
import lombok.Getter;
import lombok.Setter;

public class ComponentException extends RuntimeException {

    // 응답 상태 코드
    private final int statusCode;

    /**
     * 상태코드 + 메시지 + 타입 + 치환 파라미터를 모두 지정하는 생성자.
     */
    public ComponentException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
   }

   public  int getStatusCode() {return statusCode; }
}
