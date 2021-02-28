package top.alphaship.trade.constant;

import lombok.Getter;

@Getter
public enum StockPeriodEnum {
    MIN15(15, "15min"),
    MIN30(30, "30min"),
    MIN60(60, "60min"),
    DAY1(101, "1day");

    private final Integer code;
    private final String text;

    StockPeriodEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }
}
