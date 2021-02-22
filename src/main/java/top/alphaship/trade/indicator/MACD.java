package top.alphaship.trade.indicator;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class MACD {

    private final int FAST_MA_LENGTH = 12;
    private final int SLOW_MA_LENGTH = 26;
    private final int SIGNAL_MA_LENGTH = 9;

    private BigDecimal fastMaPrice;
    private BigDecimal slowMaPrice;
    private BigDecimal signalMaPrice;
    private BigDecimal macdPrice;
    private BigDecimal hist;

    public MACD(List<BigDecimal> prices) {
        List<BigDecimal> macdPriceList = new ArrayList<>();
        for (int i = 0; i < SIGNAL_MA_LENGTH; i++) {
            List<BigDecimal> fastMaList = prices.subList(i, FAST_MA_LENGTH + i);
            EMA fastMa = new EMA(fastMaList, FAST_MA_LENGTH);
            List<BigDecimal> slowMaList = prices.subList(i, SLOW_MA_LENGTH + i);
            EMA slowMa = new EMA(slowMaList, SLOW_MA_LENGTH);
            BigDecimal fastMaPrice = fastMa.getEmaPrice();
            BigDecimal slowMaPrice = slowMa.getEmaPrice();
            BigDecimal macdPrice = fastMaPrice.subtract(slowMaPrice);
            macdPriceList.add(macdPrice);
            if (i == 0) {
                //最新的
                this.fastMaPrice = fastMaPrice;
                this.slowMaPrice = slowMaPrice;
                this.macdPrice = macdPrice;
            }
        }
        //计算macd的指数移动平均线
        EMA signalMa = new EMA(macdPriceList, SIGNAL_MA_LENGTH);
        this.signalMaPrice = signalMa.getEmaPrice();
        this.hist = this.macdPrice.subtract(signalMaPrice);
        log.info("MACD: fast【{}】，slow【{}】，macd【{}】，signal【{}】，hist【{}】",
                this.fastMaPrice, this.slowMaPrice, this.macdPrice, this.signalMaPrice, this.hist);
    }

}
