package top.alphaship.trade.indicator;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Data
public class EMA {

    private Integer length;
    private BigDecimal emaPrice;

    public EMA(List<BigDecimal> prices, int length) {
        BigDecimal latestPrice = prices.get(0);
        SMA sma = new SMA(prices, length);
        BigDecimal alpha = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(length + 1), 8, RoundingMode.DOWN);
        this.emaPrice = alpha.multiply(latestPrice.subtract(sma.getSmaPrice())).add(sma.getSmaPrice());
        this.length = length;
//        log.info("EMA{}: {}", this.length, this.emaPrice);
    }

}
