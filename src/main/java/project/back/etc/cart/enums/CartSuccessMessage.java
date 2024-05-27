package project.back.etc.cart.enums;

public enum CartSuccessMessage {
    GET("장바구니 조회에 성공했습니다."),
    SEARCH("상품 검색에 성공 했습니다."),
    ADD("장바구니에 '%s'이(가) 담겼습니다."),
    UPDATE("'%s'의 수량을 변경했습니다."),
    DELETE("장바구니에서 '%s'을(를) 삭제했습니다."),
    DELETE_ALL("장바구니 비우기에 성공했습니다.");

    private String message;

    CartSuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
