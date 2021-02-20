package top.alphaship.trade.indicator;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MACD {

    private BigDecimal fastMa; //12
    private BigDecimal slowMa; //26
    private BigDecimal signalMa; //9
    private BigDecimal macd;
    private BigDecimal hist;

    public MACD(List<BigDecimal> closes) {
        EMA ema12 = new EMA(closes, 12, null);
        EMA ema26 = new EMA(closes, 26, null);
        this.fastMa = ema12.getEmaPrice();
        this.slowMa = ema26.getEmaPrice();
        this.macd = this.fastMa.subtract(this.slowMa);
        EMA ema9 = new EMA(closes, 9, this.macd);
        this.signalMa = ema9.getEmaPrice();
        this.hist = this.macd.subtract(this.signalMa);
    }

}
