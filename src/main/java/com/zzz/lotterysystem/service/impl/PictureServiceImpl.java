package com.zzz.lotterysystem.service.impl;

import com.zzz.lotterysystem.service.PictureService;

import com.zzz.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ServiceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class PictureServiceImpl implements PictureService{

    @Value("${pic.local-path}")
    private String localPath;

    @Override
    public String savePicture(MultipartFile multipartFile) {

        //创建目录
        File dir = new File(localPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        //创建索引
        String filename = multipartFile.getOriginalFilename();
        assert filename != null;
        String suffix = filename.substring(
                          filename.lastIndexOf("."));
        filename = UUID.randomUUID() + suffix;

        //图片保存
        try {
            multipartFile.transferTo(new File(localPath + "/" + filename));
        } catch (IOException e) {
            throw new ServiceException(ServiceErrorCodeConstants.PIC_UPLOAD_ERROR);
        }

        return filename;
    }


}
