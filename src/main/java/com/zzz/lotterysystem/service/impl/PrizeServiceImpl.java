package com.zzz.lotterysystem.service.impl;

import com.zzz.lotterysystem.controller.param.CreatePrizeParam;
import com.zzz.lotterysystem.controller.param.PageParam;
import com.zzz.lotterysystem.dao.dataobject.PrizeDO;
import com.zzz.lotterysystem.dao.mapper.PrizeMapper;
import com.zzz.lotterysystem.service.PictureService;
import com.zzz.lotterysystem.service.PrizeService;
import com.zzz.lotterysystem.service.dto.PageListDTO;
import com.zzz.lotterysystem.service.dto.PrizeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public PageListDTO<PrizeDTO> findPrizeList(PageParam param) {
        //设置总量
        int total = prizeMapper.count();
        //查询当前列表
        List<PrizeDTO> prizeDTOList = new ArrayList<>();
        List<PrizeDO> prizeDOList = prizeMapper.selectPrizeList(param.offset(),param.getPageSize());
        for(PrizeDO prizeDO : prizeDOList){
            PrizeDTO prizeDTO = new PrizeDTO();
            prizeDTO.setPrizeId(prizeDO.getId());
            prizeDTO.setName(prizeDO.getName());
            prizeDTO.setDescription(prizeDO.getDescription());
            prizeDTO.setImageUrl(prizeDO.getImageUrl());
            prizeDTO.setPrice(prizeDO.getPrice());
            prizeDTOList.add(prizeDTO);
        }
        return new PageListDTO<>(prizeDTOList,total);
    }
}
