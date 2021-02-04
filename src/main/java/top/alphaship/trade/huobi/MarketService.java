package top.alphaship.trade.huobi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huobi.utils.ConnectionFactory;
import okhttp3.Request;

public class MarketService {

    public static void main(String[] args) {
        String url = "https://www.huobi.be/-/x/pro/v2/beta/common/symbols";
        Request executeRequest = (new Request.Builder()).url(url).addHeader("Content-Type", "application/x-www-form-urlencoded").build();
        String resp = ConnectionFactory.execute(executeRequest);
        JSONObject result = JSON.parseObject(resp);
        if ("ok".equals(result.getString("status"))) {
            JSONArray data = result.getJSONArray("data");
            System.out.println("总大小 :" + data.size());
            JSONArray usdtList = new JSONArray();
            for (int i = 0; i < data.size(); i++) {
                String collect = data.getJSONObject(i).getString("quote_currency_display_name");
                if ("USDT".equals(collect)) {
                    usdtList.add(data.getJSONObject(i));
                }
            }
            System.out.println("USDT交易对大小：" + usdtList.size());
            System.out.println("USDT交易对数据：" + usdtList.toJSONString());
        }
    }
}
