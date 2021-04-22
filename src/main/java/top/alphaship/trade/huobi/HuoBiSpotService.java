package top.alphaship.trade.huobi;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.huobi.api.response.market.SwapMarketHistoryKlineResponse;
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
import top.alphaship.trade.helper.WarningHelper;
import top.alphaship.trade.indicator.EMA;
import top.alphaship.trade.util.IndicatorUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author ChenKangJin
 * @Date 2021/2/9
 */
@Slf4j
@Component
public class HuoBiSpotService {

    private final MarketClient marketClient;
    private final GenericClient genericService;

    public HuoBiSpotService() {
        HuobiOptions huobiOptions = HuobiOptions.builder().restHost("https://api.huobi.pro").build();
        marketClient = MarketClient.create(huobiOptions);
        genericService = GenericClient.create(huobiOptions);
    }

    /**
     * 获取现货K线
     * @param symbol
     * @param interval
     * @param size
     */
    public List<Candlestick> getKlineOfSpot(String symbol, CandlestickIntervalEnum interval, int size) {
        CandlestickRequest request = CandlestickRequest.builder().symbol(symbol).interval(interval).size(size).build();
        List<Candlestick> list = marketClient.getCandlestick(request);
        log.info("kline.size: {}", list.size());
        return list;
    }

    /**
     * 获取所有现货交易对
     */
    public Set<String> getSymbolOfSpot() {
        List<Symbol> symbols = genericService.getSymbols();
        Set<String> symbolsOfUsdt = symbols.stream()
                .filter(item -> "usdt".equals(item.getQuoteCurrency()) && !item.getBaseCurrency().contains("3s"))
                .map(Symbol::getSymbol)
                .collect(Collectors.toSet());
        log.info("symbols.size:{}, symbols:{}", symbolsOfUsdt.size(), JSON.toJSONString(symbolsOfUsdt));
        return symbolsOfUsdt;
    }

    /**
     * 监控所有现货交易对
     * @param interval
     * @param size
     */
    public void monitoringSignal(CandlestickIntervalEnum interval, int size) {
        Set<String> symbols = getSymbolOfSpot();
        monitoringSignal(symbols, interval, size);
    }

    public void monitoringSignal(Set<String> symbols, CandlestickIntervalEnum interval, int size) {
        for (String symbol: symbols) {
            try {
                List<Candlestick> candlesticks = getKlineOfSpot(symbol, interval, size + 1);
                List<BigDecimal> prices = candlesticks.stream().map(Candlestick::getClose).collect(Collectors.toList());

                //消息模板
                BasicTemplate basicTemplate = new BasicTemplate();
                basicTemplate.setWarningTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                basicTemplate.setPair(symbol);
                basicTemplate.setCycleTime(interval.getCode());
                basicTemplate.setCurrentPrice(prices.get(0));

                //上穿ema150均线做多
                Candlestick newestKline = candlesticks.get(0);
                BigDecimal ema150 = new EMA(prices, 150).getEmaPrice();
                if (IndicatorUtil.crossOver(newestKline.getClose(), ema150, newestKline.getLow())) {
                    //做多
                    log.info("现货：{} 看涨", symbol);
                    basicTemplate.setDirection(DirectionConstant.LONG.getText());
                    //发送钉钉
                    DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.SPOT);
                }
                //下穿ema150均线做空
                if (IndicatorUtil.crossDown(newestKline.getHigh(), ema150, newestKline.getClose())) {
                    //做空
                    log.info("现货：{} 看跌", symbol);
                    basicTemplate.setDirection(DirectionConstant.SHORT.getText());
                    //发送钉钉
                    DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.SPOT);
                }

                /*if (WarningHelper.MacdWarning(prices) == 1) {
                    //买入
                    log.info("{} 看涨", symbol.getSymbol());
                    basicTemplate.setDirection(DirectionConstant.LONG.getText());
                    //发送钉钉
                    DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.SPOT);
                }
                //监控PinBar
                Candlestick latestKline = candlesticks.get(1);
                if (WarningHelper.pinbarWarning(latestKline.getOpen(), latestKline.getClose(), latestKline.getHigh(), latestKline.getLow())) {
                    log.info("{} 出现PinBar", symbol.getSymbol());
                    basicTemplate.setDirection("出现PinBar");
                    //发送钉钉
                    DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.SPOT);
                }*/


            } catch (Exception e) {
                log.error("{} 获取K线失败", e.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        HuoBiSpotService huoBiSpotService = new HuoBiSpotService();
        huoBiSpotService.monitoringSignal(CandlestickIntervalEnum.MIN15, 130);
    }

}
