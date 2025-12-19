package com.zzz.lotterysystem.service.mq;


import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.controller.param.DrawPrizeParam;
import com.zzz.lotterysystem.service.DrawPrizeService;
import com.zzz.lotterysystem.service.activitystatus.ActivityStatusManager;
import com.zzz.lotterysystem.service.dto.ConvertActivityStatusDTO;
import com.zzz.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityUserStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.stream.Collectors;

import static com.zzz.lotterysystem.common.config.DirectRabbitConfig.QUEUE_NAME;

@Component
@RabbitListener(queues = QUEUE_NAME)
public class MqReceiver {

    private static final Logger logger = LoggerFactory.getLogger(MqReceiver.class);

    @Autowired
    private DrawPrizeService drawPrizeService;

    @Autowired
    private ActivityStatusManager activityStatusManager;

    @RabbitHandler
    public void process(Map<String,String> message){
        //成功接受消息
        logger.info("MQ成功接收到消息，message:{}",
                JacksonUtil.writeValueAsString(message));
        String paramSting = message.get("messageData");
        DrawPrizeParam param = JacksonUtil.readValue(paramSting, DrawPrizeParam.class);

        //处理抽奖流程
        try{
            //校验抽奖结果有效
            drawPrizeService.checkDrawPrizeParam(param);
            //状态扭转处理
            statusConvert(param);
            //保存中奖者名单

            //通知中奖者

        }catch (ServiceException e){
            logger.error("处理 MQ 消息异常 {}:{}",e.getCode(),e.getMessage(),e);
            //异常回滚
        }catch (Exception e){
            logger.error("处理 MQ 消息异常",e);

        }



    }

    /**
     * 状态扭转
     * @param param
     */
    private void statusConvert(DrawPrizeParam param) {

        ConvertActivityStatusDTO convertActivityStatusDTO = new ConvertActivityStatusDTO();
        convertActivityStatusDTO.setActivityId(param.getActivityId());
        convertActivityStatusDTO.setTargetActivityStatus(ActivityStatusEnum.COMPLETED);
        convertActivityStatusDTO.setPrizeId(param.getPrizeId());
        convertActivityStatusDTO.setTargetPrizeStatus(ActivityPrizeStatusEnum.COMPLETED);
        convertActivityStatusDTO.setUserIds(
                param.getWinnerList().stream()
                        .map(DrawPrizeParam.Winner::getUserId)
                        .collect(Collectors.toList())
        );

        convertActivityStatusDTO.setTargetUserStatus(ActivityUserStatusEnum.COMPLETED);
        activityStatusManager.handlerEvent(convertActivityStatusDTO);
    }


}
