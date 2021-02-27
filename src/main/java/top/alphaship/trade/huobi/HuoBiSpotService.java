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
import top.alphaship.trade.helper.WarningHelper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author ChenKangJin
 * @Date 2021/2/9
 */
@Slf4j
@Component
public class HuoBiSpotService {

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
     * 监控所有现货交易对
     * @param interval
     * @param size
     */
    public void monitoringSignal(CandlestickIntervalEnum interval, int size) {
        List<Symbol> symbols = getSymbolOfSpot();
        for (Symbol symbol: symbols) {
            try {
                List<Candlestick> candlesticks = getKlineOfSpot(symbol.getSymbol(), interval, size + 1);

                //消息模板
                BasicTemplate basicTemplate = new BasicTemplate();
                basicTemplate.setWarningTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                basicTemplate.setPair(symbol.getSymbol());
                basicTemplate.setCycleTime(interval.getCode());

                List<BigDecimal> prices = candlesticks.stream().map(Candlestick::getClose).collect(Collectors.toList());

                if (WarningHelper.MacdWarning(prices) == 1) {
                    //买入
                    log.info("{} 看涨", symbol.getSymbol());
                    basicTemplate.setDirection(DirectionConstant.LONG.getText());
                    //发送钉钉
                    DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.SPOT);
                }

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
