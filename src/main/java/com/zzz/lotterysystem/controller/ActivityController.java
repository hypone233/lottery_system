package com.zzz.lotterysystem.controller;


import com.zzz.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ControllerException;
import com.zzz.lotterysystem.common.pojo.CommonResult;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.controller.param.CreateActivityParam;
import com.zzz.lotterysystem.controller.result.CreateActivityResult;
import com.zzz.lotterysystem.service.dto.CreateActivityDTO;
import com.zzz.lotterysystem.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivityController {

    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    @RequestMapping("/activity/create")
    public CommonResult<CreateActivityResult> createActivity(
            @Validated @RequestBody CreateActivityParam param){
        logger.info("createActivity CreateActivityParam:{}",
                JacksonUtil.writeValueAsString(param));

        return CommonResult.success(convertToCreateActivityResult(activityService.createActivity(param)));



    }

    private CreateActivityResult convertToCreateActivityResult(CreateActivityDTO createActivityDTO) {

        if(null == createActivityDTO){
            throw new ControllerException(ControllerErrorCodeConstants.CREATE_ACTIVITY_ERROR);
        }

        CreateActivityResult result = new CreateActivityResult();
        result.setActivityId(createActivityDTO.getActivityID());
        return result;

    }

}
