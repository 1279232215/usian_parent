package com.usian.controller;
import com.usian.utils.Result;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.usian.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/file")
@RestController
public class FileUploadController {

    @Autowired
    private FastFileStorageClient storageClient;

    //定义图片可能是哪几种类型
    private List<String> CONTENT_TYPES = Arrays.asList("image/jpeg","image/jpg","image/png");

    //图片上传
    @RequestMapping("/upload")
    public Result fileUpload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        //校验文件的类型
        if(!CONTENT_TYPES.contains(file.getContentType())){
            return Result.error("图片上传类型错误！！！"+originalFilename);
        }
        //校验文件的内容
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if(bufferedImage==null){
            return Result.error("图片上传内容错误！！！"+originalFilename);
        }
        //保存到服务器
        /*
        * StringUtils.substringAfterLast("chinachina", "i"); // na "i"最后出现的位置向后截取
        * StringUtils.substringAfter("china", "i"); // na 从第一次出现"i"的位置向后截取，不包含第一次出现的"i"
        * */
        String ext = StringUtils.substringAfterLast(originalFilename, ".");
        StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
        //生成url地址，并返回
        return Result.ok("http://image.usian.com/"+storePath.getFullPath());
    }
}
