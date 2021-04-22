package top.alphaship.trade.util;

import java.math.BigDecimal;

public class IndicatorUtil {

    //向上穿越
    public static boolean crossOver(BigDecimal topPrice, BigDecimal middlePrice, BigDecimal bottomPrice) {
        return topPrice.compareTo(middlePrice) > 0 && middlePrice.compareTo(bottomPrice) > 0;
    }

    //向下穿越
    public static boolean crossDown(BigDecimal topPrice, BigDecimal middlePrice, BigDecimal bottomPrice) {
        return topPrice.compareTo(middlePrice) < 0 && middlePrice.compareTo(bottomPrice) < 0;
    }

}
