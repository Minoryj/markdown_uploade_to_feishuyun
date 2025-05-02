import config.Config;
import feishu.FeishuImageBlockCollector;
import feishu.FeishuImageUploader;
import feishu.PatchDocumentBlockSample;
import markdown.MarkdownImageExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) throws Exception {
        // 初始化配置类
        try {
            Config.init(Objects.requireNonNull(Main.class.getClassLoader().getResource("config.yml")).getPath());
        } catch (FileNotFoundException e) {
            System.err.println("配置文件未找到！");
        }

        // 提取配置中assetPath的值，下面很快就会用
        String assetPath = Config.getInstance().getAssetPath();

        // 提取 Markdown 文件中的图片名称，封装成列表
        List<String> imageNameList = new MarkdownImageExtractor().extractImageFileNames();

        // 获取飞书文档中的图片 Block ID，封装成列表
        List<String> blockIdList = new FeishuImageBlockCollector().getImageBlockIds();


        // 检查数量是否匹配
        if (imageNameList.size() != blockIdList.size()) {
            System.out.println("图片数量不匹配，停止程序");
            System.out.println("markdown中的图片数量：" + imageNameList.size());
            System.out.println("云文档中待上传的图片数量：" + blockIdList.size());
            return;
        }

        // 上传图片并替换飞书文档中的图片
        FeishuImageUploader uploader = new FeishuImageUploader();
        PatchDocumentBlockSample replace = new PatchDocumentBlockSample();
        for (int i = 0; i < imageNameList.size(); i++) {
            System.out.println((i + 1) + " / " + imageNameList.size());
            File file = new File(assetPath + imageNameList.get(i));
            String file_token = uploader.uploadImage(file, blockIdList.get(i));
            replace.replaceImage(file, blockIdList.get(i), file_token);
        }

        // 显示结束
        System.out.println("所有图片已成功上传！");
    }
}
