package top.alphaship.trade.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HuoBiJob {

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
        log.info("15分钟的");
    }


    /**
     * 每1小时执行一次
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void processByOneHour() {
        log.info("1小时的");
    }


    /**
     * 每4小时执行一次
     */
    @Scheduled(cron = "0 0 */4 * * ?")
    public void processByFourHour() {
        log.info("4小时的");
    }


    /**
     * 每天执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processByOneDay() {
        log.info("1天的");
    }


}
