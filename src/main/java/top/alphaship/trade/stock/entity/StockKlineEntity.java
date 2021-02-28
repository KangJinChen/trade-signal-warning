package top.alphaship.trade.stock.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author ChenKangJin
 * @Date 2021/2/28
 */
@Data
public class StockKlineEntity {

    private String date;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal highest;
    private BigDecimal lowest;


}
