package feishu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.docx.v1.model.GetDocumentBlockChildrenReq;
import com.lark.oapi.service.docx.v1.model.GetDocumentBlockChildrenResp;

import config.Config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FeishuImageBlockCollector {
    private List<String> extractImageBlockIds(String jsonString) {
        List<String> result = new ArrayList<>();
        JsonElement root = JsonParser.parseString(jsonString);
        if (root.isJsonObject()) {
            JsonObject rootObj = root.getAsJsonObject();
            if (rootObj.has("data")) {
                JsonObject dataObj = rootObj.getAsJsonObject("data");
                if (dataObj.has("items")) {
                    for (JsonElement item : dataObj.getAsJsonArray("items")) {
                        JsonObject itemObj = item.getAsJsonObject();
                        if (itemObj.has("block_type") && itemObj.get("block_type").getAsInt() == 27) {
                            if (itemObj.has("block_id")) {
                                result.add(itemObj.get("block_id").getAsString());
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private String extractPageToken(String jsonString) {
        JsonElement root = JsonParser.parseString(jsonString);
        if (root.isJsonObject()) {
            JsonObject rootObj = root.getAsJsonObject();
            if (rootObj.has("data")) {
                JsonObject dataObj = rootObj.getAsJsonObject("data");
                if (dataObj.has("has_more") && dataObj.get("has_more").getAsBoolean()) {
                    if (dataObj.has("page_token")) {
                        return dataObj.get("page_token").getAsString();
                    }
                }
            }
        }
        return null;
    }

    public List<String> getImageBlockIds() throws Exception {
        List<String> imageBlockIds = new ArrayList<>();
        // 初始化Client类
        Client client = FeishuClientFactory.create();
        String documentId = Config.getInstance().getFeishuDocumentId();

        String userAccessToken = Config.getInstance().getFeishuUserAccessToken();
        String pageToken = null;

        while (true) {
            GetDocumentBlockChildrenReq req = GetDocumentBlockChildrenReq.newBuilder()
                    .documentId(documentId)
                    .blockId(documentId)
                    .documentRevisionId(-1)
                    .pageSize(500)
                    .withDescendants(true)
                    .pageToken(pageToken)
                    .build();

            GetDocumentBlockChildrenResp resp = client.docx().v1().documentBlockChildren().get(
                    req, RequestOptions.newBuilder().userAccessToken(userAccessToken).build());

            String rawJson = new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8);
            imageBlockIds.addAll(extractImageBlockIds(rawJson));

            pageToken = extractPageToken(rawJson);
            if (pageToken == null || pageToken.isEmpty()) {
                break;
            }
        }

        System.out.println("云文档中待上传图片的blockid统计完毕！");
        return imageBlockIds;
    }
}
