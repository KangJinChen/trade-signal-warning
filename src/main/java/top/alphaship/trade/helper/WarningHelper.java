package top.alphaship.trade.helper;

import top.alphaship.trade.constant.DirectionConstant;
import top.alphaship.trade.indicator.MACD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @Author ChenKangJin
 * @Date 2021/2/27
 */
public class WarningHelper {


    /**
     * MACD预警
     * return: 1为看涨信号，2为看跌信号，-1为无信号
     */
    public static int MacdWarning(List<BigDecimal> prices) {
        //当前的macd
        MACD currentMacd = new MACD(prices);
        //上一个macd
        List<BigDecimal> previaPrices = prices.subList(1, prices.size());
        MACD previaMacd = new MACD(previaPrices);

        //涨信号
        if (currentMacd.getHist().compareTo(BigDecimal.ZERO) >= 0
                && previaMacd.getHist().compareTo(BigDecimal.ZERO) <= 0
                && prices.get(0).compareTo(prices.get(1)) > 0) {
            return DirectionConstant.LONG.getCode();
        }

        //跌信号
        if (currentMacd.getHist().compareTo(BigDecimal.ZERO) <= 0
                && previaMacd.getHist().compareTo(BigDecimal.ZERO) >= 0
                && prices.get(0).compareTo(prices.get(1)) < 0) {
            return DirectionConstant.SHORT.getCode();
        }
        return -1;
    }

    public static boolean pinbarWarning(BigDecimal open, BigDecimal close, BigDecimal high, BigDecimal low) {
        BigDecimal bodyLength = open.compareTo(close) > 0 ? open.subtract(close) : close.subtract(open);
        BigDecimal upperTailLength = open.compareTo(close) > 0 ? high.subtract(open) : high.subtract(close);
        BigDecimal lowerTailLength = open.compareTo(close) > 0 ? close.subtract(low) : open.subtract(low);
        BigDecimal oneThird = BigDecimal.valueOf(1).divide(BigDecimal.valueOf(3), 8, RoundingMode.DOWN);
        return bodyLength.divide(upperTailLength,8, RoundingMode.DOWN).compareTo(oneThird) < 0
                || bodyLength.divide(lowerTailLength,8, RoundingMode.DOWN).compareTo(oneThird) < 0;
    }


}
