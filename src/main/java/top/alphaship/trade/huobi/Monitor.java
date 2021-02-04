package top.alphaship.trade.huobi;

import cn.hutool.core.date.DateUtil;
import top.alphaship.trade.bot.BasicTemplate;
import top.alphaship.trade.bot.DingDingHelper;
import top.alphaship.trade.constant.CycleTimeConstant;
import top.alphaship.trade.constant.DirectionConstant;

import java.util.Date;

public class Monitor {

    public static void main(String[] args) {
        BasicTemplate basicTemplate = new BasicTemplate();
        basicTemplate.setWarningTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        basicTemplate.setPair("ETH/USDT");
        basicTemplate.setDirection(DirectionConstant.SHORT.getText());
        basicTemplate.setCycleTime(CycleTimeConstant.DAY_1.getText());
        DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null);
    }
}
