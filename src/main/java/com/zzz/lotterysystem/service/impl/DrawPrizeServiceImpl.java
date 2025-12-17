package com.zzz.lotterysystem.service.impl;

import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.controller.param.DrawPrizeParam;
import com.zzz.lotterysystem.service.DrawPrizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.zzz.lotterysystem.common.config.DirectRabbitConfig.EXCHANGE_NAME;
import static com.zzz.lotterysystem.common.config.DirectRabbitConfig.ROUTING;

@Service
public class DrawPrizeServiceImpl implements DrawPrizeService {

    private static final Logger logger = LoggerFactory.getLogger(DrawPrizeServiceImpl.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void drawPrize(DrawPrizeParam param) {

        Map<String,String> map = new HashMap<>();
        map.put("messageId",String.valueOf(UUID.randomUUID()));
        map.put("messageData", JacksonUtil.writeValueAsString(param));
        //发消息: 交换机，绑定key,消息体
        rabbitTemplate.convertAndSend(EXCHANGE_NAME,ROUTING,map);
        logger.info("mq消息发送成功: map={}",JacksonUtil.writeValueAsString(map));

    }
}
