package top.alphaship.trade.stock.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.alphaship.trade.bot.BasicTemplate;
import top.alphaship.trade.bot.DingDingHelper;
import top.alphaship.trade.constant.BotType;
import top.alphaship.trade.constant.DirectionConstant;
import top.alphaship.trade.constant.StockPeriodEnum;
import top.alphaship.trade.helper.WarningHelper;
import top.alphaship.trade.stock.entity.StockEntity;
import top.alphaship.trade.stock.entity.StockKlineEntity;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author ChenKangJin
 * @Date 2021/2/28
 */
@Slf4j
@Component
public class ChinaStockService {

    private final static String STOCK_LIST_URL = "http://50.push2.eastmoney.com/api/qt/clist/get";
    private final static String STOCK_KLINE_URL = "http://43.push2his.eastmoney.com/api/qt/stock/kline/get";

    public List<StockEntity> getStockList() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pn", 1);
        params.put("pz", 5000);
        params.put("fs", "m:0+t:6,m:0+t:13,m:1+t:2");
        params.put("fields", "f12,f13,f14");

        String response = HttpUtil.get(STOCK_LIST_URL, params);
        log.info("get stock list response: {}", response);
        JSONObject responseObject = JSON.parseObject(response);
        JSONObject data = responseObject.getJSONObject("data");
        JSONObject diff = data.getJSONObject("diff");
        Set<String> keys = diff.keySet();
        log.info("keys size: {}",  keys.size());

        List<StockEntity> list = new ArrayList<>();
        StockEntity stockEntity;

        for (String key : keys) {
            JSONObject item = diff.getJSONObject(key);
            stockEntity = new StockEntity();
            stockEntity.setSymbol(item.getString("f12"));
            stockEntity.setCategory(item.getInteger("f13"));
            stockEntity.setName(item.getString("f14"));
            list.add(stockEntity);
        }

        log.info("get stock list size: {}", list.size());
        return list;

    }

    public List<StockEntity> getStockListOfShenZhen(List<StockEntity> list) {
        return list.stream().filter(item -> item.getCategory() == 0).collect(Collectors.toList());
    }

    public List<StockEntity> getStockListOfShangHai(List<StockEntity> list) {
        return list.stream().filter(item -> item.getCategory() == 1).collect(Collectors.toList());
    }


    public List<StockKlineEntity> getStockKLine(StockEntity stockEntity, StockPeriodEnum period) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("secid", stockEntity.getCategory() + "." + stockEntity.getSymbol());
        params.put("klt", period.getCode());
        params.put("fields1", "f1,f2,f3,f4,f5,f6");
        params.put("fields2", "f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61");
        params.put("fqt", "0");
        params.put("beg", "0");
        params.put("end", "20500101");

        String response = HttpUtil.get(STOCK_KLINE_URL, params);
        log.info("get stock kline list response: {}", response);
        JSONObject responseObject = JSON.parseObject(response);
        JSONObject data = responseObject.getJSONObject("data");
        JSONArray klines = data.getJSONArray("klines");

        List<StockKlineEntity> list = new ArrayList<>();
        StockKlineEntity stockKlineEntity;
        for (int i = klines.size() - 1; i >= 0; i--) {
            String[] kline = klines.getString(i).split(",");
            stockKlineEntity = new StockKlineEntity();
            stockKlineEntity.setDate(kline[0]);
            stockKlineEntity.setOpen(new BigDecimal(kline[1]));
            stockKlineEntity.setClose(new BigDecimal(kline[2]));
            stockKlineEntity.setHighest(new BigDecimal(kline[3]));
            stockKlineEntity.setLowest(new BigDecimal(kline[4]));
            list.add(stockKlineEntity);
        }
        log.info("kline size: {}", list.size());
        return list;
    }


    public void monitoringSignal(StockPeriodEnum period) {
        List<StockEntity> stockList = getStockList();
        monitoringSignal(stockList, period);

    }

    public void monitoringSignal(List<StockEntity> stockList, StockPeriodEnum period) {
        for (StockEntity stock: stockList) {
            try {
                List<StockKlineEntity> stockKLine = getStockKLine(stock, period);
                List<BigDecimal> prices = stockKLine.stream().map(StockKlineEntity::getClose).collect(Collectors.toList());

                //消息模板
                String exchangeTab = stock.getCategory() == 0 ? "sz" : "sh";
                BasicTemplate basicTemplate = new BasicTemplate();
                basicTemplate.setWarningTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                basicTemplate.setPair(exchangeTab + stock.getSymbol() + " (" + stock.getName() + ")");
                basicTemplate.setCycleTime(period.getText());
                basicTemplate.setCurrentPrice(prices.get(0));

                if (prices.size() >= 35) {
                    if (WarningHelper.MacdWarning(prices) == 1) {
                        //看涨
                        log.info("股票：{} 看涨", stock.getSymbol());
                        basicTemplate.setDirection(DirectionConstant.LONG.getText());
                        //发送钉钉
                        DingDingHelper.sendMarkdownMessage("信号预警", basicTemplate.toString(), false, null, BotType.STOCK);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                DingDingHelper.sendTextMessage(e.getMessage(), false, null, BotType.STOCK);
            }
        }
        log.info("end.....");

    }


    public static void main(String[] args) {
        ChinaStockService chinaStockService = new ChinaStockService();

        StockEntity stockEntity = new StockEntity();
        stockEntity.setSymbol("002488");
        stockEntity.setCategory(0);
        stockEntity.setName("金固股份");
        List<StockEntity> stockEntityList = Arrays.asList(stockEntity);
        chinaStockService.monitoringSignal(stockEntityList, StockPeriodEnum.MIN60);
    }

}
