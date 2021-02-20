package top.alphaship.trade.indicator;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Data
public class EMA {

    private Integer length;
    private BigDecimal emaPrice;

    public EMA(List<BigDecimal> closes, int length, BigDecimal macd) {
        List<BigDecimal> sumList = closes.subList(0, length);
        BigDecimal latestClosePrice = sumList.get(0);
        if (Objects.nonNull(macd)) {
            latestClosePrice = macd;
        }
        BigDecimal priceSum = sumList.stream().reduce(BigDecimal::add).get();
        BigDecimal smaPrice = priceSum.divide(BigDecimal.valueOf(length), RoundingMode.HALF_DOWN);
        BigDecimal alpha = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(length + 1), 8, RoundingMode.DOWN);
        this.emaPrice = alpha.multiply(latestClosePrice.subtract(smaPrice)).add(smaPrice);
        this.length = length;
    }

}
