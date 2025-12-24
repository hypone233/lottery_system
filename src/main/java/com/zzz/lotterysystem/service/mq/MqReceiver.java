package com.zzz.lotterysystem.service.mq;


import cn.hutool.core.date.DateUtil;
import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.common.utils.MailUtil;
import com.zzz.lotterysystem.controller.param.DrawPrizeParam;
import com.zzz.lotterysystem.dao.dataobject.ActivityPrizeDO;
import com.zzz.lotterysystem.dao.dataobject.WinningRecordDO;
import com.zzz.lotterysystem.dao.mapper.ActivityPrizeMapper;
import com.zzz.lotterysystem.dao.mapper.WinningRecordMapper;
import com.zzz.lotterysystem.service.DrawPrizeService;
import com.zzz.lotterysystem.service.activitystatus.ActivityStatusManager;
import com.zzz.lotterysystem.service.dto.ConvertActivityStatusDTO;
import com.zzz.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityPrizeTiersEnum;
import com.zzz.lotterysystem.service.enums.ActivityStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityUserStatusEnum;
import io.lettuce.core.ScriptOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.List;
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
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private ActivityPrizeMapper activityPrizeMapper;
    @Autowired
    private WinningRecordMapper winningRecordMapper;

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
            if (!drawPrizeService.checkDrawPrizeParam(param)) {
                return;
            }
            //状态扭转处理
            statusConvert(param);
            //保存中奖者名单
            List<WinningRecordDO> winningRecordDOList =
                drawPrizeService.saveWinnerRecords(param);
            //通知中奖者
            syncExecute(winningRecordDOList);

        }catch (ServiceException e){
            logger.error("处理 MQ 消息异常 {}:{}",e.getCode(),e.getMessage(),e);
            //异常回滚
            rollback(param);
            //异常抛出
            throw e;
        }catch (Exception e){
            logger.error("处理 MQ 消息异常",e);
            //异常回滚
            rollback(param);
            //异常抛出
            throw e;
        }



    }

    /**
     * 处理异常回滚:恢复至处理请求前
     * @param param
     */
    private void rollback(DrawPrizeParam param) {
        //回滚状态
        //状态是否需要回滚
        if(!statusNeedRollback(param)){
            //不需要
            return;
        }
        //需要

        rollbackStatus(param);

        //回滚中奖者名单
        //状态是否需要回滚
        if(!winnerNeedRollback(param)){
            //不需要
            return;
        }
        //需要

        rollbackWinner(param);
    }

    private void rollbackWinner(DrawPrizeParam param) {
        drawPrizeService.deleteRecords(param.getActivityId(), param.getPrizeId());
    }

    private boolean winnerNeedRollback(DrawPrizeParam param) {
        int count = winningRecordMapper.countByAPId(param.getActivityId(),param.getPrizeId());
        return count>0;

    }

    private void rollbackStatus(DrawPrizeParam param) {
        ConvertActivityStatusDTO convertActivityStatusDTO = new ConvertActivityStatusDTO();
        convertActivityStatusDTO.setActivityId(param.getActivityId());
        convertActivityStatusDTO.setTargetActivityStatus(ActivityStatusEnum.RUNNING);
        convertActivityStatusDTO.setPrizeId(param.getPrizeId());
        convertActivityStatusDTO.setTargetPrizeStatus(ActivityPrizeStatusEnum.INIT);
        convertActivityStatusDTO.setUserIds(
                param.getWinnerList().stream()
                        .map(DrawPrizeParam.Winner::getUserId)
                        .collect(Collectors.toList())
        );
        convertActivityStatusDTO.setTargetUserStatus(ActivityUserStatusEnum.INIT);
        activityStatusManager.rollbackHandlerEvent(convertActivityStatusDTO);





    }

    private boolean statusNeedRollback(DrawPrizeParam param) {
        ActivityPrizeDO activityPrizeDO =
            activityPrizeMapper.selectByAPId(param.getActivityId(),param.getPrizeId());
        return activityPrizeDO.getStatus()
                .equalsIgnoreCase(ActivityPrizeStatusEnum.COMPLETED.name());
    }

    /**
     * 并发处理抽奖后续流程
     *
     * @param winningRecordDOList
     */
    private void syncExecute(List<WinningRecordDO> winningRecordDOList) {

        //邮件通知
        threadPoolTaskExecutor.execute(()->sendMail(winningRecordDOList));

    }

    private void sendMail(List<WinningRecordDO> winningRecordDOList) {
        if(CollectionUtils.isEmpty(winningRecordDOList)){
            logger.info("中奖者列表为空，不用发邮件");
            return;
        }
        for(WinningRecordDO winningRecordDO: winningRecordDOList){
            String context = "Hi，" + winningRecordDO.getWinnerName() + "。恭喜你在"
                    + winningRecordDO.getActivityName() + "活动中获得"
                    + ActivityPrizeTiersEnum.fromName(winningRecordDO.getPrizeTier()).getMessage()
                    + "：" + winningRecordDO.getPrizeName() + "。获奖时间为"
                    + DateUtil.formatTime(winningRecordDO.getWinningTime()) + "，请尽快领取您的奖励！";
            mailUtil.sendSampleMail(winningRecordDO.getWinnerEmail(),
                              "中奖通知",
                             context);


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
