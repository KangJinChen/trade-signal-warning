package top.alphaship.trade.bot;

import lombok.Data;

@Data
public class EnhancedTemplate extends BasicTemplate {

    //进场价格
    public String entryPrice;
    //止盈价格
    public String stopProfitPrice;
    //止损价格
    public String stopLossPrice;
    //盈亏比
    public String profitLossRate;


    @Override
    public String toString() {
        return "### 信号预警通知\n" +
                "- <font color=#8B8989>预警时间：</font><font color=#1874CD>"+ warningTime +"</font>\n" +
                "- <font color=#8B8989>预警品种：</font><font color=#1874CD>"+ pair +"</font>\n" +
                "- <font color=#8B8989>预警方向：</font><font color=#1874CD>"+ direction +"</font>\n" +
                "- <font color=#8B8989>时间周期：</font><font color=#1874CD>"+ cycleTime +"</font>\n" +
                "- <font color=#8B8989>进场价格：</font><font color=#1874CD>"+ entryPrice +"</font>\n" +
                "- <font color=#8B8989>止盈价格：</font><font color=#1874CD>"+ stopProfitPrice +"</font>\n" +
                "- <font color=#8B8989>止损价格：</font><font color=#1874CD>"+ stopLossPrice +"</font>\n" +
                "- <font color=#8B8989>盈亏比：</font><font color=#1874CD>"+ profitLossRate +"</font>\n";
    }
}
