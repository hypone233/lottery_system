package com.zzz.lotterysystem.service.impl;

import com.zzz.lotterysystem.controller.param.CreatePrizeParam;
import com.zzz.lotterysystem.dao.dataobject.PrizeDO;
import com.zzz.lotterysystem.dao.mapper.PrizeMapper;
import com.zzz.lotterysystem.service.PictureService;
import com.zzz.lotterysystem.service.PrizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PrizeServiceImpl implements PrizeService {

    @Autowired
    private PictureService pictureService;
    @Autowired
    private PrizeMapper prizeMapper;
    @Override
    public Long createPrize(CreatePrizeParam param, MultipartFile picFile) {
       //上传图片

        String fileName = pictureService.savePicture(picFile);


       //存储
        PrizeDO prizeDO = new PrizeDO();
        prizeDO.setName(param.getPrizeName());
        prizeDO.setDescription(param.getDescription());
        prizeDO.setImageUrl(fileName);
        prizeDO.setPrice(param.getPrice());
        prizeMapper.insert(prizeDO);
        return prizeDO.getId();
    }
}
