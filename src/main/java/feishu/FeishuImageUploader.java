package feishu;

import com.google.gson.JsonParser;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaReq;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaReqBody;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaResp;

import config.Config;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class FeishuImageUploader {

    public String uploadImage(File file, String blockId) throws Exception {
        String userAccessToken = Config.getInstance().getFeishuUserAccessToken();
        Client client = FeishuClientFactory.create();

        UploadAllMediaReq req = UploadAllMediaReq.newBuilder()
                .uploadAllMediaReqBody(UploadAllMediaReqBody.newBuilder()
                        .fileName(file.getName())
                        .parentType("docx_image")
                        .parentNode(blockId)
                        .size((int) file.length())
                        .file(file)
                        .build())
                .build();

        UploadAllMediaResp resp = client.drive().v1().media().uploadAll(req, RequestOptions.newBuilder()
                .userAccessToken(userAccessToken)
                .build());

        if (!resp.success()) {
            throw new Exception("Error uploading image: " + resp.getMsg());
        }

        String rawJson = new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8);
        System.out.println("图片已经挂载到要上传的块下面");
        return JsonParser.parseString(rawJson).getAsJsonObject().getAsJsonObject("data").get("file_token").getAsString();
    }
}
