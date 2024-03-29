package top.alphaship.trade.bot;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import top.alphaship.trade.constant.BotType;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class DingDingHelper {

    public static void sendTextMessage(String content, boolean isAtAll, Set atMobiles, BotType botType) {
        try {
            //组装请求参数
            JSONObject text = new JSONObject();
            text.put("content", content);

            JSONObject at = new JSONObject();
            if (null == atMobiles) {
                atMobiles = new HashSet();
            }
            at.put("atMobiles", ((Set)atMobiles).toArray());
            at.put("isAtAll", isAtAll);

            JSONObject requestParam = new JSONObject();
            requestParam.put("msgtype", "text");
            requestParam.put("text", text);
            requestParam.put("at", at);
            log.info("发送钉钉消息，请求参数：{}", requestParam.toString());

            execute(requestParam, botType);
        } catch (Exception e) {
            log.info("发送钉钉消息失败", e);
        }

    }

    public static void sendMarkdownMessage(String title, String content, boolean isAtAll, Set atMobiles, BotType botType) {
        try {
            //组装请求参数
            JSONObject markdown = new JSONObject();
            markdown.put("title", title);
            markdown.put("text", content);

            JSONObject at = new JSONObject();
            if (null == atMobiles) {
                atMobiles = new HashSet();
            }
            at.put("atMobiles", ((Set)atMobiles).toArray());
            at.put("isAtAll", isAtAll);

            JSONObject requestParam = new JSONObject();
            requestParam.put("msgtype", "markdown");
            requestParam.put("markdown", markdown);
            requestParam.put("at", at);
            log.info("发送钉钉消息，请求参数：{}", requestParam.toString());

            execute(requestParam, botType);
        } catch (Exception e) {
            log.info("发送钉钉消息失败", e);
        }

    }

    private static String getSignUrl(BotType botType) throws Exception {
        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + botType.getSecret();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(botType.getSecret().getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        String signUrl = botType.getUrl() + "&timestamp=" + timestamp + "&sign=" + sign;
        log.info("签名后的url：{}", signUrl);
        return signUrl;
    }
    
    private static void execute(JSONObject requestParam, BotType botType) throws Exception {
        //获取签名url
        String signUrl = getSignUrl(botType);
        String result = HttpRequest
                .post(signUrl)
                .header("Content-Type", "application/json")
                .body(requestParam.toJSONString())
                .execute()
                .body();
        log.info("发送钉钉消息，返回结果：{}", result);
    }

}
