package com.file.demo.fileupdatedownload.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName FileController
 * @Description TODO
 * @date 2022/5/26 15:00
 * @Version 1.0
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private static final String BASE_DIR = "F:\\桌面文件\\fileUpdateDown\\file\\";
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam MultipartFile file){
        //获取文件的名称
        String fileName  = file.getOriginalFilename();
        File uploadFile = new File(BASE_DIR + fileName);
        //如果父目录不存在时，自动创建
        try {
            if (!uploadFile.getParentFile().exists()){
                    uploadFile.getParentFile().mkdirs();
            }
            //存到磁盘
            file.transferTo(uploadFile);
        }catch (IOException e){
            e.printStackTrace();
        }
        Map<String, Object> map=new HashMap<>(8);
        map.put("url","http://localhost:8080/file/download?fileName="+fileName);
        return map;
    }
    @GetMapping("/download")
    public void download(@RequestParam String fileName, HttpServletResponse response){
        //  新建文件流，从磁盘读取文件流
        try {
            FileInputStream fis = new FileInputStream(BASE_DIR + fileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ServletOutputStream os = response.getOutputStream();
            // 新建字节数组，长度是文件的大小，比如文件 6kb, bis.available() = 1024 * 6
            byte[] bytes = new byte[bis.available()];
            bis.read(bytes);
            //重置response
            response.reset();
            // 设置 response 的下载响应头
            response.setContentType("application/octet-stream");
            // 注意，这里要设置文件名的编码，否则中文的文件名下载后不显示
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 写出字节数组到输出流
            os.write(bytes);
            //刷新输出流
            os.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
