package top.alphaship.trade.job;

import com.huobi.constant.enums.CandlestickIntervalEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.alphaship.trade.huobi.HuoBiContractService;
import top.alphaship.trade.huobi.HuoBiSpotService;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class HuoBiJob {

    @Autowired
    private HuoBiSpotService huoBiSpotService;
    @Autowired
    private HuoBiContractService huoBiContractService;

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * *")
    public void processByPerMinute() {
        //火币合约监控
        List<String> contracts = Arrays.asList("BTC-USDT", "ETH-USDT", "ADA-USDT");
        huoBiContractService.monitoringSignal(contracts, CandlestickIntervalEnum.MIN1, 150);
    }



    /**
     * 每15分钟执行一次
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void processByFifteenMinute() {
        //火币现货监控
        huoBiSpotService.monitoringSignal(CandlestickIntervalEnum.MIN15, 150);
        //火币合约监控
        huoBiContractService.monitoringSignal(CandlestickIntervalEnum.MIN15, 150);
    }


    /**
     * 每1小时执行一次
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void processByOneHour() {
        //火币现货监控
        huoBiSpotService.monitoringSignal(CandlestickIntervalEnum.MIN60, 150);
        //火币合约监控
        huoBiContractService.monitoringSignal(CandlestickIntervalEnum.MIN60, 150);
    }


    /**
     * 每4小时执行一次
     */
    @Scheduled(cron = "0 0 */4 * * ?")
    public void processByFourHour() {
        //火币现货监控
        huoBiSpotService.monitoringSignal(CandlestickIntervalEnum.HOUR4, 150);
        //火币合约监控
        huoBiContractService.monitoringSignal(CandlestickIntervalEnum.HOUR4, 150);
    }


    /**
     * 每天执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processByOneDay() {
        //火币现货监控
        huoBiSpotService.monitoringSignal(CandlestickIntervalEnum.DAY1, 150);
    }


}
