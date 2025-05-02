package markdown;

import config.Config;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class MarkdownImageExtractor {

    public List<String> extractImageFileNames() throws IOException {
        String markdownFilePath = Config.getInstance().getLocalMarkdownPath();

        String content = Files.readString(Paths.get(markdownFilePath));
        List<String> imageFileNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("!\\[[^\\]]*]\\([^)]*[/\\\\]([^/\\\\)]+\\.(png|jpg|jpeg|gif))\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String imagePath = matcher.group(1);
            String fileName = Paths.get(imagePath).getFileName().toString();
            imageFileNames.add(fileName);
        }

        System.out.println("Markdown文件中的图片名称提取完毕！");
        return imageFileNames;
    }
}
