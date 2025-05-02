package feishu;

import com.lark.oapi.Client;

import config.Config;

public class FeishuClientFactory {

    public static Client create() {
        String appId = Config.getInstance().getFeishuAppId();
        String appSecret = Config.getInstance().getFeishuAppSecret();
        return Client.newBuilder(appId, appSecret).build();
    }
}
