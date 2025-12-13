package com.zzz.lotterysystem.service;

import org.springframework.web.multipart.MultipartFile;

public interface PictureService {

    /**
     *
     * @param multipartFile
     * @return  索引: 上传文件的文件名
     */
    String savePicture(MultipartFile multipartFile);


}
