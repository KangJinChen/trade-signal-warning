package top.alphaship.trade.constant;

import lombok.Getter;

@Getter
public enum DirectionConstant {
    LONG(1, "看涨"),
    SHORT(2, "看跌");

    private final Integer code;
    private final String text;

    DirectionConstant(Integer code, String text) {
        this.code = code;
        this.text = text;
    }
}
