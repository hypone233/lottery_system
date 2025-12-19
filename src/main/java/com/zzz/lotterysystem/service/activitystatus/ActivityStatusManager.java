package com.zzz.lotterysystem.service.activitystatus;

import com.zzz.lotterysystem.service.dto.ConvertActivityStatusDTO;



public interface ActivityStatusManager {


    /**
     * 处理活动相关状态转换
     * @param convertActivityStatusDTO
     */
    void handlerEvent(ConvertActivityStatusDTO convertActivityStatusDTO);

}
