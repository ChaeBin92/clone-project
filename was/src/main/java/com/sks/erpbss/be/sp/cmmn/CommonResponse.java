package com.sks.erpbss.be.sp.cmmn;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "CommonResponse")
public class CommonResponse<T> {
    @Schema(description = "상태코드")
    private int statusCd;

    @Schema(description = "메세지타입")
    private String msgTyp;

    @Schema(description = "에러메세지")
    private String errMsg;

    @Schema(description = "전체데이터수")
    private int totalCount;

    @Schema(description = "응답 데이터 수")
    private int responseCount;

    @Schema(description = "실제데이터")
    private T data;

    public CommonResponse(
        int statusCd,
        String msgTyp,
        String errMsg,
        int totalCount,
        int responseCount,
        T data
    ) {
        this.statusCd = statusCd;
        this.msgTyp = msgTyp;
        this.errMsg = errMsg;
        this.totalCount = totalCount;
        this.responseCount = responseCount;
        this.data = data;
    }

    public static <T> CommonResponse<T> success(int statusCd, T data) {
        return new CommonResponse<>(statusCd, "S", "", 1, 1, data);
    }

    public static <T> CommonResponse<T> success(int statusCd, T data, int totalCount, int responseCount) {
        return new CommonResponse<>(statusCd, "S", "", totalCount, responseCount, data);
    }

    // 안내성 메시지(I) 응답
    public static <T> CommonResponse<T> toast(int statusCd, String msg) {
        return new CommonResponse<>(statusCd, "I", msg, 0, 0, null);
    }

    public static <T> CommonResponse<T> error(int statusCd, String errMsg) {
        return new CommonResponse<>(statusCd, "E", errMsg, 0, 0, null);
    }
}
