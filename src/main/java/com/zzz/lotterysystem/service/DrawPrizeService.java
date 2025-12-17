package com.zzz.lotterysystem.service;


import com.zzz.lotterysystem.controller.param.DrawPrizeParam;

import org.springframework.stereotype.Service;

@Service
public interface DrawPrizeService {

    /**
     * 异步抽奖接口
     * @param param
     */
    void drawPrize(DrawPrizeParam param);

}
