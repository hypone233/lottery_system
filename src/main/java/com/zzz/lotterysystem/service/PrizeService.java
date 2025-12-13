package com.zzz.lotterysystem.service;

import com.zzz.lotterysystem.controller.param.CreatePrizeParam;
import org.springframework.web.multipart.MultipartFile;

public interface PrizeService {

    /**
     * 创建单个奖品
     * @param param 奖品属性
     * @param picFile 奖品图
     * @return 奖品ID
     */
    Long createPrize(CreatePrizeParam param, MultipartFile picFile);

}
