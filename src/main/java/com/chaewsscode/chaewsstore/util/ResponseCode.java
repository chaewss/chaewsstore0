package com.chaewsscode.chaewsstore.util;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    /* 200 OK : 요청 성공 */
    SIGNIN_SUCCESS(OK, "로그인 성공"),
    SIGNUP_SUCCESS(OK, "회원가입 성공"),
    SIGNOUT_SUCCESS(OK, "로그아웃 성공"),

    READ_ACCOUNT_INFO_SUCCESS(OK, "회원 정보 조회 성공"),
    RESET_PASSWORD_SUCCESS(OK, "비밀번호 재설정 성공"),

    READ_PRODUCTS_SUCCESS(OK, "전체 상품 목록 조회 성공"),
    READ_MY_PRODUCTS_SUCCESS(OK, "내 상품 목록 조회 성공"),
    UPDATE_PRODUCT_SUCCESS(OK, "상품 수정 성공"),
    DELETE_PRODUCT_SUCCESS(OK, "상품 삭제 성공"),

    READ_ORDERS_SUCCESS(CREATED, "내 주문 목록 조회 성공"),
    READ_ORDER_SUCCESS(CREATED, "내 주문 상세 조회 성공"),

    /* 201 CREATED : 요청 성공, 자원 생성 */
    CREATE_PRODUCT_SUCCESS(CREATED, "상품 등록 성공"),

    CREATE_ORDER_SUCCESS(CREATED, "주문 성공"),

    /* 400 BAD_REQUEST : 잘못된 요청 */
    VALID_ERROR(BAD_REQUEST, "유효성 검사 실패"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다"),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    UPDATE_PRODUCT_FAIL_NOT_OWNER(FORBIDDEN, "본인 상품만 수정할 수 있습니다"),
    DELETE_PRODUCT_FAIL_NOT_OWNER(FORBIDDEN, "본인 상품만 삭제할 수 있습니다"),

    CREATE_ORDER_FAIL_OWNER(FORBIDDEN, "본인 상품은 구매할 수 없습니다"),
    READ_ORDER_FAIL_NOT_OWNER(FORBIDDEN, "본인 주문만 읽을 수 있습니다"),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    ACCOUNT_NOT_FOUND(NOT_FOUND, "회원을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(NOT_FOUND, "상품을 찾을 수 없습니다."),
    ORDER_NOT_FOUND(NOT_FOUND, "주문을 찾을 수 없습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    ACCOUNT_DUPLICATION(CONFLICT, "이미 사용 중인 아이디입니다"),

;
    private final HttpStatus httpStatus;
    private final String detail;
}
