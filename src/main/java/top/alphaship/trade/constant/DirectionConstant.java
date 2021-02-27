package top.alphaship.trade.constant;

import lombok.Getter;

@Getter
public enum DirectionConstant {
    LONG("看涨"),
    SHORT("看跌");

    private final String text;

    DirectionConstant(String text) {
        this.text = text;
    }
}
