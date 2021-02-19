package top.alphaship.trade.job;

import com.huobi.constant.enums.CandlestickIntervalEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.alphaship.trade.huobi.HuoBiContractService;
import top.alphaship.trade.huobi.HuoBiMarketService;

@Slf4j
@Component
public class HuoBiJob {

    @Autowired
    private HuoBiMarketService huoBiMarketService;
    @Autowired
    private HuoBiContractService huoBiContractService;

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * *")
    public void processByPerMinute() {
        log.info("1分钟的");
    }



    /**
     * 每15分钟执行一次
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void processByFifteenMinute() {
        huoBiMarketService.monitorAllPairOfSpot(CandlestickIntervalEnum.MIN15, 130);
        huoBiContractService.monitorShortSignal(CandlestickIntervalEnum.MIN15, 130);
    }


    /**
     * 每1小时执行一次
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void processByOneHour() {
        huoBiMarketService.monitorAllPairOfSpot(CandlestickIntervalEnum.MIN60, 130);
        huoBiContractService.monitorShortSignal(CandlestickIntervalEnum.MIN60, 130);
    }


    /**
     * 每4小时执行一次
     */
    @Scheduled(cron = "0 0 */4 * * ?")
    public void processByFourHour() {
        huoBiMarketService.monitorAllPairOfSpot(CandlestickIntervalEnum.HOUR4, 130);
        huoBiContractService.monitorShortSignal(CandlestickIntervalEnum.HOUR4, 130);
    }


    /**
     * 每天执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processByOneDay() {
        huoBiMarketService.monitorAllPairOfSpot(CandlestickIntervalEnum.DAY1, 130);
        huoBiContractService.monitorShortSignal(CandlestickIntervalEnum.DAY1, 130);
    }


}
