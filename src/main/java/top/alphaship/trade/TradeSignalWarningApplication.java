package top.alphaship.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TradeSignalWarningApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeSignalWarningApplication.class, args);
    }

}
