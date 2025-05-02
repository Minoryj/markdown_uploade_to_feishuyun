package feishu;

import com.google.gson.JsonParser;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.docx.v1.model.PatchDocumentBlockReq;
import com.lark.oapi.service.docx.v1.model.PatchDocumentBlockResp;
import com.lark.oapi.service.docx.v1.model.ReplaceImageRequest;
import com.lark.oapi.service.docx.v1.model.UpdateBlockRequest;
import config.Config;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class PatchDocumentBlockSample {
    public void replaceImage(File file, String blockId, String imageToken) throws Exception {
        Client client = FeishuClientFactory.create();
        String documentId = Config.getInstance().getFeishuDocumentId();
        String userAccessToken = Config.getInstance().getFeishuUserAccessToken();


        PatchDocumentBlockReq req = PatchDocumentBlockReq.newBuilder()
                .documentId(documentId)
                .blockId(blockId)
                .documentRevisionId(-1)
                .updateBlockRequest(UpdateBlockRequest.newBuilder()
                        .replaceImage(ReplaceImageRequest.newBuilder()
                                .token(imageToken)
                                .width(ImageIO.read(file).getWidth())
                                .height(ImageIO.read(file).getHeight())
                                .build())
                        .build())
                .build();

        // 发起请求
        PatchDocumentBlockResp resp = client.docx().v1().documentBlock().patch(req, RequestOptions.newBuilder()
                .userAccessToken(userAccessToken)
                .build());

        // 处理服务端错误
        if (!resp.success()) {
            System.out.printf("code:%s,msg:%s,reqId:%s, resp:%s%n",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
            return;
        }

        // 业务数据处理
        System.out.println("图片已经更新到块中！");
    }

}
