package top.alphaship.trade.huobi;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.huobi.client.GenericClient;
import com.huobi.client.MarketClient;
import com.huobi.client.req.market.CandlestickRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.CandlestickIntervalEnum;
import com.huobi.model.generic.Symbol;
import com.huobi.model.market.Candlestick;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.alphaship.trade.bot.BasicTemplate;
import top.alphaship.trade.bot.DingDingHelper;
import top.alphaship.trade.constant.BotType;
import top.alphaship.trade.constant.DirectionConstant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author ChenKangJin
 * @Date 2021/2/9
 */
@Slf4j
@Component
public class HuoBiMarketService {

    /**
     * 获取现货K线
     * @param symbol
     * @param interval
     * @param size
     */
    public List<Candlestick> getKlineOfSpot(String symbol, CandlestickIntervalEnum interval, int size) {
        MarketClient marketClient = MarketClient.create(new HuobiOptions());
        CandlestickRequest request = CandlestickRequest.builder().symbol(symbol).interval(interval).size(size).build();
        List<Candlestick> list = marketClient.getCandlestick(request);
        log.info("kline.size: {}", list.size());
        return list;
    }

    /**
     * 获取所有现货交易对
     */
    public List<Symbol> getSymbolOfSpot() {
        GenericClient genericService = GenericClient.create(new HuobiOptions());
        List<Symbol> symbols = genericService.getSymbols();
        List<Symbol> symbolsOfUsdt = symbols.stream()
                .filter(item -> "usdt".equals(item.getQuoteCurrency()) && !item.getBaseCurrency().contains("3s"))
                .collect(Collectors.toList());
        log.info("symbols.size:{}, symbols:{}", symbolsOfUsdt.size(), JSON.toJSONString(symbolsOfUsdt));
        return symbolsOfUsdt;
    }

    /**
     * 计算平均移动均线
     * @param candlesticks
     * @return
     */
    public BigDecimal calcSma(List<Candlestick> candlesticks) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Candlestick candlestick : candlesticks) {
            sum = sum.add(candlestick.getClose());
        }
        BigDecimal smaValue = sum.divide(BigDecimal.valueOf(candlesticks.size()), RoundingMode.HALF_DOWN);
        log.info("sma: {}", smaValue);
        return smaValue;
    }

    /**
     * 上穿sma
     * @param candlesticks
     * @return
     */
    public boolean crossover(List<Candlestick> candlesticks) {
        BigDecimal latestClose = candlesticks.get(0).getClose();
        BigDecimal previewOpen = candlesticks.get(1).getOpen();
        BigDecimal previewClose = candlesticks.get(1).getClose();

        BigDecimal sma = calcSma(candlesticks);

        if ((previewOpen.compareTo(sma) < 0 && previewClose.compareTo(sma) > 0) && latestClose.compareTo(sma) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 下穿sma
     * @param candlesticks
     * @return
     */
    public boolean crossunder(List<Candlestick> candlesticks) {

        BigDecimal latestClose = candlesticks.get(0).getClose();
        BigDecimal previewOpen = candlesticks.get(1).getOpen();
        BigDecimal previewClose = candlesticks.get(1).getClose();

        BigDecimal sma = calcSma(candlesticks);

        if ((previewOpen.compareTo(sma) > 0 && previewClose.compareTo(sma) < 0) && latestClose.compareTo(sma) < 0) {
            return true;
        }
        return false;
    }


    /**
     * 监控所有现货交易对
     * @param interval
     * @param size
     */
    public void monitorAllPairOfSpot(CandlestickIntervalEnum interval, int size) {
        List<Symbol> symbols = getSymbolOfSpot();
        for (Symbol symbol: symbols) {
            try {
                List<Candlestick> candlesticks = getKlineOfSpot(symbol.getSymbol(), interval, size + 1);

                //消息模板
                BasicTemplate basicTemplate = new BasicTemplate();
                basicTemplate.setWarningTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                basicTemplate.setPair(symbol.getSymbol());
                basicTemplate.setCycleTime(interval.getCode());
                basicTemplate.setType("现货");

                if (crossover(candlesticks)) {
                    //买入
                    log.info("{} 买入", symbol.getSymbol());
                    basicTemplate.setDirection(DirectionConstant.BUY.getText());
                    //发送钉钉
                    DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.SPOT);
                }
                /*if (crossunder(candlesticks)) {
                    //卖出
                    log.info("{} 卖出", symbol.getSymbol());
                    basicTemplate.setDirection(DirectionConstant.SELL.getText());
                    //发送钉钉
                    DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null);
                }*/


            } catch (Exception e) {
                log.error("{} 获取K线失败", e.getMessage());
            }
        }

    }


    public static void main(String[] args) {
        HuoBiMarketService huoBiMarketService = new HuoBiMarketService();
        huoBiMarketService.monitorAllPairOfSpot(CandlestickIntervalEnum.MIN15, 130);
    }

}
