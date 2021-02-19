package top.alphaship.trade.constant;

import lombok.Getter;

@Getter
public enum DirectionConstant {
    LONG("做多"),
    SHORT("做空"),
    BUY("买入"),
    SELL("卖出");

    private final String text;

    DirectionConstant(String text) {
        this.text = text;
    }
}
