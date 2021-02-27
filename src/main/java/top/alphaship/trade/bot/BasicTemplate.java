package top.alphaship.trade.bot;

import lombok.Data;

@Data
public class BasicTemplate {

    //预警时间
    public String warningTime;
    //交易对
    public String pair;
    //方向
    public String direction;
    //时间周期
    public String cycleTime;


    @Override
    public String toString() {
        return  "- <font color=#8B8989>预警时间：</font><font color=#1874CD>"+ warningTime +"</font>\n" +
                "- <font color=#8B8989>预警品种：</font><font color=#1874CD>"+ pair +"</font>\n" +
                "- <font color=#8B8989>预警方向：</font><font color=#1874CD>"+ direction +"</font>\n" +
                "- <font color=#8B8989>时间周期：</font><font color=#1874CD>"+ cycleTime +"</font>\n";
    }
}
