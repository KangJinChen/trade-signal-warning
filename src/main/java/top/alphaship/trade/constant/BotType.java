package top.alphaship.trade.constant;

import lombok.Getter;

@Getter
public enum BotType {
    SPOT("spot",
            "https://oapi.dingtalk.com/robot/send?access_token=5081793364c62fdb47dbaedb9b93de25d8b30b4f3b471ef79696633c5b56f055",
            "SEC6e5f16c8207d367ed1ce2c080eb9272e0c4be6b33d0741e511b41b7155a88806"),
    CONTRACT("contract",
            "https://oapi.dingtalk.com/robot/send?access_token=38bbd2bacfb4acaaa8296afa3f30d5a154a8594b64a93f26ea3a534d92cc28d7",
            "SECcbcd136e4757a598b62a6d6f4e72cb08fa79a22a6061774db8e0af10bb956b70"),
    STOCK("stock",
            "https://oapi.dingtalk.com/robot/send?access_token=227e87180a33c11d219a4003fa1963da7cc7ba6206d6ecd23102aed670b13ff3",
            "SECc314484631179a895a673e67032bbac07d5a53c74aa6866a722518f5204b65b4")
    ;


    private String code;
    private String url;
    private String secret;

    BotType(String code, String url, String secret) {
        this.code = code;
        this.url = url;
        this.secret = secret;
    }
}
