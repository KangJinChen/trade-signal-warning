package top.alphaship.trade.constant;

import lombok.Getter;

@Getter
public enum CycleTimeConstant {
    MINUTE_1("1 minute"),
    MINUTE_15("15 minute"),
    HOUR_1("1 hour"),
    HOUR_4("4 hour"),
    DAY_1("1 day");

    private final String text;

    CycleTimeConstant(String text) {
        this.text = text;
    }
}
