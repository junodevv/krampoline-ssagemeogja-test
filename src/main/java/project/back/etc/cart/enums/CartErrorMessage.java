package project.back.etc.cart.enums;

public enum CartErrorMessage {
    // search
    NOT_EXIST_PRODUCT("'%s'은(는) 존재하지 않는 상품입니다."),
    // add, update, delete
    NOT_FOUND_MEMBER("사용자 정보를 찾을 수 없습니다."),
    NOT_FOUND_PRODUCT("상품을 찾을 수 없습니다."),
    // add
    ALREADY_EXIST_PRODUCT("이미 장바구니에 존재하는 상품입니다."),
    // update
    INVALID_QUANTITY("1 이상의 숫자만 입력해주세요"),
    QUANTITY_ONE_OR_MORE("수량은 1개 이상이어야 합니다."),
    DELETE_RECOMMEND("삭제를 원한다면 삭제버튼을 눌러주세요"),
    // update, delete(개별)
    NOT_EXIST_PRODUCT_IN_CART("장바구니에 존재하지 않는 상품입니다.");

    private String message;

    CartErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
