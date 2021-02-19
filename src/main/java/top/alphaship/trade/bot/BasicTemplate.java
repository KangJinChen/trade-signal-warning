package top.alphaship.trade.bot;

import lombok.Data;

@Data
public class BasicTemplate {

    //类型，合约/现货
    public String type;
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
        return "### "+ type +"信号预警通知\n" +
                "- <font color=#8B8989>预警时间：</font><font color=#1874CD>"+ warningTime +"</font>\n" +
                "- <font color=#8B8989>预警品种：</font><font color=#1874CD>"+ pair +"</font>\n" +
                "- <font color=#8B8989>预警方向：</font><font color=#1874CD>"+ direction +"</font>\n" +
                "- <font color=#8B8989>时间周期：</font><font color=#1874CD>"+ cycleTime +"</font>\n";
    }
}
