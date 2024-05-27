package project.back.etc.cart.enums;

public enum quantityUpdateSign {
    PLUS("+"),
    MINUS("-");

    private String value;

    quantityUpdateSign(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
