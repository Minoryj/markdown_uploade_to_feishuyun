package config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class Config {
    private static Config instance;
    private final Map<String, Object> yamlData;

    private Config(String path) throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(path)) {
            yamlData = yaml.load(fis);
        }
    }

    // 初始化一次
    public static void init(String path) throws IOException {
        if (instance == null) {
            instance = new Config(path);
        }
    }

    // 获取实例
    public static Config getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Config not initialized.");
        }
        return instance;
    }

    // 获取顶级配置
    public Map<String, Object> get(String key) {
        return (Map<String, Object>) yamlData.get(key);
    }

    // Feishu 配置
    public String getFeishuAppId() {
        return (String) get("feishu").get("appId");
    }

    public String getFeishuAppSecret() {
        return (String) get("feishu").get("appSecret");
    }

    public String getFeishuUserAccessToken() {
        return (String) get("feishu").get("userAccessToken");
    }

    public String getFeishuDocumentId() {
        return (String) get("feishu").get("documentId");
    }

    // Local Markdown 配置
    public String getLocalMarkdownPath() {
        return (String) get("localmarkdownpath").get("markdownPath");
    }

    public String getAssetPath() {
        return (String) get("localmarkdownpath").get("assetPath");
    }
}
