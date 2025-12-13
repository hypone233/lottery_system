package com.zzz.lotterysystem.controller;


import cn.hutool.log.LogFactory;
import com.zzz.lotterysystem.common.pojo.CommonResult;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.controller.param.CreatePrizeParam;
import com.zzz.lotterysystem.service.PictureService;
import com.zzz.lotterysystem.service.PrizeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PrizeController {

    private static final Logger logger = LoggerFactory.getLogger(PrizeController.class);

    @Autowired
    private PictureService pictureService;
    @Autowired
    private PrizeService prizeService;

    @RequestMapping("/pic/upload")
    public String uploadPic(MultipartFile file){
        return pictureService.savePicture(file);
    }

    /**
     * 奖品创建
     *      RequestPart : 接受表单数据 multipart/form-data
     *
     * @param param
     * @param picFile
     * @return
     */
    @RequestMapping("/prize/create")

    public CommonResult<Long> createPrize(@Valid @RequestPart("param") CreatePrizeParam param,
                                          @RequestPart("prizePic") MultipartFile picFile){
        logger.info("createPrize CreatePrizeParam:{}",
                JacksonUtil.writeValueAsString(param));
        return CommonResult.success(
                prizeService.createPrize(param,picFile));

    }

}
