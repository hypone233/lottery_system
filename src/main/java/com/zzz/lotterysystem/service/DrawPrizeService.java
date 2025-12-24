package com.zzz.lotterysystem.service;


import com.zzz.lotterysystem.controller.param.DrawPrizeParam;

import com.zzz.lotterysystem.dao.dataobject.WinningRecordDO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DrawPrizeService {

    /**
     * 异步抽奖接口
     * @param param
     */
    void drawPrize(DrawPrizeParam param);



    Boolean checkDrawPrizeParam(DrawPrizeParam param);



    List<WinningRecordDO> saveWinnerRecords(DrawPrizeParam param);

    /*
    删除活动奖品中奖记录
     */
    void deleteRecords(Long activityId,Long prizeId);
}
