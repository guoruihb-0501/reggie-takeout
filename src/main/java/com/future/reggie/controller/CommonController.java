package com.future.reggie.controller;

import com.future.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author guorui
 * @create 2022-12-07-10:29
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")   //在application.yml中设置上传文件路径
    private String basePath;


    //上传图片
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info(file.toString());
        //获得上传的原始文件名
        String originalFileName = file.getOriginalFilename();
        //获取上传原始文件的扩展名
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        //随机生成文件名
        String fileName = UUID.randomUUID().toString();
        //给随机生成的文件名加上原始扩展名
        fileName = fileName + suffix;

        //如果参数指定的存储路径不存在，则自动创建
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        //将上传的文件转存到指定的文件夹指定文件
        try {
            file.transferTo(new File(basePath + fileName));
        }catch (IOException ex){
            ex.printStackTrace();
        }
        //返回上传后自动生成的文件名，在下载的时候要使用
        return R.success(fileName);
    }

    //下载图片回显到浏览器
    @GetMapping("/download")
    public void download(String name,HttpServletResponse response){

        try {
            //通过输入流读取上传内容
            FileInputStream fis = new FileInputStream(basePath + name);
            //通过输出流返回浏览器
            ServletOutputStream sos = response.getOutputStream();
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fis.read(bytes)) != -1){
                sos.write(bytes,0,len);
                sos.flush();
            }
            fis.close();
            sos.close();

        }catch (IOException ex){
            ex.printStackTrace();
        }

    }
}
