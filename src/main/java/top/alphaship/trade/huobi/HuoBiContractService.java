package top.alphaship.trade.huobi;

import cn.hutool.core.date.DateUtil;
import com.huobi.api.request.account.SwapMarketHistoryKlineRequest;
import com.huobi.api.response.market.SwapContractInfoResponse;
import com.huobi.api.response.market.SwapMarketHistoryKlineResponse;
import com.huobi.api.service.market.MarketAPIService;
import com.huobi.api.service.market.MarketAPIServiceImpl;
import com.huobi.constant.enums.CandlestickIntervalEnum;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author ChenKangJin
 * @Date 2021/2/16
 */
@Slf4j
@Component
public class HuoBiContractService {

    private MarketAPIService marketAPIService = new MarketAPIServiceImpl();


    public Set<String> getAllContract() {
        SwapContractInfoResponse allContractResp = marketAPIService.getSwapContractInfo(null, "all");
        List<SwapContractInfoResponse.DataBean> data = allContractResp.getData();
        Set<String> allContractList = data.stream().map(SwapContractInfoResponse.DataBean::getContractCode).collect(Collectors.toSet());
        log.info("contract.size: {}, allContractList: {}", allContractList.size(), allContractList);
        return allContractList;
    }

    public List<SwapMarketHistoryKlineResponse.DataBean> getKLine(String contractCode, CandlestickIntervalEnum period, int size) {
        SwapMarketHistoryKlineRequest request = SwapMarketHistoryKlineRequest.builder()
                .contractCode(contractCode)
                .period(period.getCode())
                .size(size)
                .build();
        SwapMarketHistoryKlineResponse response = marketAPIService.getSwapMarketHistoryKline(request);
        List<SwapMarketHistoryKlineResponse.DataBean> dataBeanList = response.getData();
        return dataBeanList.stream().sorted(Comparator.comparing(SwapMarketHistoryKlineResponse.DataBean::getId).reversed()).collect(Collectors.toList());
    }




    public void monitoringSignal(CandlestickIntervalEnum period, int size) {
        Set<String> allContract = getAllContract();
        monitoringSignal(allContract, period, size);
    }

    public void monitoringSignal(Set<String> contracts, CandlestickIntervalEnum period, int size) {
        for (String contractCode: contracts) {
            List<SwapMarketHistoryKlineResponse.DataBean> kLines = getKLine(contractCode, period, size);
            List<BigDecimal> prices = kLines.stream().map(SwapMarketHistoryKlineResponse.DataBean::getClose).collect(Collectors.toList());
            //消息模板
            BasicTemplate basicTemplate = new BasicTemplate();
            basicTemplate.setWarningTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            basicTemplate.setPair(contractCode);
            basicTemplate.setCycleTime(period.getCode());
            basicTemplate.setCurrentPrice(prices.get(0));

            //上穿ema150均线做多
            SwapMarketHistoryKlineResponse.DataBean newestKline = kLines.get(0);
            BigDecimal ema150 = new EMA(prices, 150).getEmaPrice();
            if (IndicatorUtil.crossOver(newestKline.getClose(), ema150, newestKline.getLow())) {
                //做多
                log.info("合约：{} 看涨", contractCode);
                basicTemplate.setDirection(DirectionConstant.LONG.getText());
                //发送钉钉
                DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.CONTRACT);
            }
            //下穿ema150均线做空
            if (IndicatorUtil.crossDown(newestKline.getHigh(), ema150, newestKline.getClose())) {
                //做空
                log.info("合约：{} 看跌", contractCode);
                basicTemplate.setDirection(DirectionConstant.SHORT.getText());
                //发送钉钉
                DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.CONTRACT);
            }


            /*if (WarningHelper.MacdWarning(prices) == 1) {
                //做多
                log.info("合约：{} 看涨", contractCode);
                basicTemplate.setDirection(DirectionConstant.LONG.getText());
                //发送钉钉
                DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.CONTRACT);
            }
            if (WarningHelper.MacdWarning(prices) == 2) {
                //做空
                log.info("合约：{} 看跌", contractCode);
                basicTemplate.setDirection(DirectionConstant.SHORT.getText());
                //发送钉钉
                DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.CONTRACT);

            }*/
        }
    }

    public static void main(String[] args) {
        HuoBiContractService huoBiContractService = new HuoBiContractService();
        huoBiContractService.monitoringSignal(CandlestickIntervalEnum.MIN1, 130);
    }
}
