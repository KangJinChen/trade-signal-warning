package top.alphaship.trade.indicator;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Data
public class SMA {

    private Integer length;
    private BigDecimal smaPrice;

    public SMA(List<BigDecimal> prices, int length) {
        List<BigDecimal> sumList = prices.subList(0, length);
        BigDecimal priceSum = sumList.stream().reduce(BigDecimal::add).get();
        this.smaPrice = priceSum.divide(BigDecimal.valueOf(length), 8, RoundingMode.DOWN);
        this.length = length;
//        log.info("EMA{}: {}", this.length, this.smaPrice);
    }

}
