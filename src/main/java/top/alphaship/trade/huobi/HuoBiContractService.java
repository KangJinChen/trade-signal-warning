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
import top.alphaship.trade.constant.DirectionConstant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author ChenKangJin
 * @Date 2021/2/16
 */
@Slf4j
@Component
public class HuoBiContractService {

    private MarketAPIService marketAPIService = new MarketAPIServiceImpl();


    public List<String> getAllContract() {
        SwapContractInfoResponse allContractResp = marketAPIService.getSwapContractInfo(null, "all");
        List<SwapContractInfoResponse.DataBean> data = allContractResp.getData();
        List<String> allContractList = data.stream().map(SwapContractInfoResponse.DataBean::getContractCode).collect(Collectors.toList());
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
        return response.getData();
    }

    /**
     * 计算平均移动均线
     */
    public BigDecimal calcSma(List<SwapMarketHistoryKlineResponse.DataBean> KLines) {
        BigDecimal sum = BigDecimal.ZERO;
        for (SwapMarketHistoryKlineResponse.DataBean KLine : KLines) {
            sum = sum.add(KLine.getClose());
        }
        BigDecimal smaValue = sum.divide(BigDecimal.valueOf(KLines.size()), RoundingMode.HALF_DOWN);
        log.info("sma: {}", smaValue);
        return smaValue;
    }


    /**
     * 上穿sma
     */
    public boolean crossover(List<SwapMarketHistoryKlineResponse.DataBean> KLines) {
        BigDecimal latestClose = KLines.get(0).getClose();
        BigDecimal previewOpen = KLines.get(1).getOpen();
        BigDecimal previewClose = KLines.get(1).getClose();

        BigDecimal sma = calcSma(KLines);

        if ((previewOpen.compareTo(sma) < 0 && previewClose.compareTo(sma) > 0) && latestClose.compareTo(sma) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 下穿sma
     */
    public boolean crossunder(List<SwapMarketHistoryKlineResponse.DataBean> KLines) {

        BigDecimal latestClose = KLines.get(0).getClose();
        BigDecimal previewOpen = KLines.get(1).getOpen();
        BigDecimal previewClose = KLines.get(1).getClose();

        BigDecimal sma = calcSma(KLines);

        if ((previewOpen.compareTo(sma) > 0 && previewClose.compareTo(sma) < 0) && latestClose.compareTo(sma) < 0) {
            return true;
        }
        return false;
    }


    public void monitorShortSignal(CandlestickIntervalEnum period, int size) {
        List<String> allContract = getAllContract();
        for (String contractCode: allContract) {
            List<SwapMarketHistoryKlineResponse.DataBean> kLines = getKLine(contractCode, period, size);

            //消息模板
            BasicTemplate basicTemplate = new BasicTemplate();
            basicTemplate.setWarningTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            basicTemplate.setPair(contractCode);
            basicTemplate.setCycleTime(period.getCode());
            basicTemplate.setType("合约");

            if (crossover(kLines)) {
                //做多
                log.info("合约：{} 做多", contractCode);
                basicTemplate.setDirection(DirectionConstant.LONG.getText());
                //发送钉钉
                DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null);
            }
            if (crossunder(kLines)) {
                //做空
                log.info("合约：{} 做空", contractCode);
                basicTemplate.setDirection(DirectionConstant.SHORT.getText());
                //发送钉钉
                DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null);

            }
        }
    }

    public static void main(String[] args) {
        HuoBiContractService huoBiContractService = new HuoBiContractService();
        huoBiContractService.monitorShortSignal(CandlestickIntervalEnum.MIN15, 130);
    }
}
