package top.alphaship.trade.constant;

import lombok.Getter;

@Getter
public enum StockPeriodEnum {
    MIN15(15),
    MIN30(30),
    MIN60(60),
    DAY1(101);

    private final Integer code;

    StockPeriodEnum(Integer code) {
        this.code = code;
    }
}
