package top.alphaship.trade.job;

import com.huobi.constant.enums.CandlestickIntervalEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.alphaship.trade.data.SymbolData;
import top.alphaship.trade.huobi.HuoBiContractService;
import top.alphaship.trade.huobi.HuoBiSpotService;

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
        huoBiContractService.monitoringSignal(SymbolData.contractSymbols, CandlestickIntervalEnum.MIN1, 150);
    }



    /**
     * 每15分钟执行一次
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void processByFifteenMinute() {
        //火币现货监控
        huoBiSpotService.monitoringSignal(SymbolData.spotSymbols, CandlestickIntervalEnum.MIN15, 150);
        //火币合约监控
        huoBiContractService.monitoringSignal(SymbolData.contractSymbols, CandlestickIntervalEnum.MIN15, 150);
    }


    /**
     * 每1小时执行一次
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void processByOneHour() {
        //火币现货监控
        huoBiSpotService.monitoringSignal(SymbolData.spotSymbols, CandlestickIntervalEnum.MIN60, 150);
        //火币合约监控
        huoBiContractService.monitoringSignal(SymbolData.contractSymbols, CandlestickIntervalEnum.MIN60, 150);
    }


    /**
     * 每4小时执行一次
     */
    @Scheduled(cron = "0 0 */4 * * ?")
    public void processByFourHour() {
        //火币现货监控
        huoBiSpotService.monitoringSignal(SymbolData.spotSymbols, CandlestickIntervalEnum.HOUR4, 150);
    }


}
