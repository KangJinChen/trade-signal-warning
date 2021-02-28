package top.alphaship.trade.helper;

import top.alphaship.trade.indicator.MACD;

import java.math.BigDecimal;
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
            return 1;
        }

        //跌信号
        if (currentMacd.getHist().compareTo(BigDecimal.ZERO) <= 0
                && previaMacd.getHist().compareTo(BigDecimal.ZERO) >= 0
                && prices.get(0).compareTo(prices.get(1)) < 0) {
            return 2;
        }

        return -1;
    }

}
