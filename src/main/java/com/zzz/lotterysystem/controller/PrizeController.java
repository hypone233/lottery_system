package com.zzz.lotterysystem.controller;


import cn.hutool.log.LogFactory;
import com.zzz.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ControllerException;
import com.zzz.lotterysystem.common.pojo.CommonResult;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.controller.param.CreatePrizeParam;
import com.zzz.lotterysystem.controller.param.PageParam;
import com.zzz.lotterysystem.controller.result.FindPrizeListResult;
import com.zzz.lotterysystem.service.PictureService;
import com.zzz.lotterysystem.service.PrizeService;
import com.zzz.lotterysystem.service.dto.PageListDTO;
import com.zzz.lotterysystem.service.dto.PrizeDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

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

    @RequestMapping("/prize/find-list")
    public CommonResult<FindPrizeListResult> findPrizeList(PageParam param){
        logger.info("findPrizeList PageParam:{}",
                JacksonUtil.writeValueAsString(param));
        PageListDTO<PrizeDTO> pageListDTO = prizeService.findPrizeList(param);
        return CommonResult.success(converToFindPrizeListResult(pageListDTO));

    }

    private FindPrizeListResult converToFindPrizeListResult(PageListDTO<PrizeDTO> pageListDTO) {
        if(null == pageListDTO){
            throw new ControllerException(ControllerErrorCodeConstants.FIND_PRIZE_LIST_ERROR);
        }
        FindPrizeListResult result = new FindPrizeListResult();
        result.setTotal(pageListDTO.getTotal());
        result.setRecords(
                pageListDTO.getRecords().stream()
                        .map(prizeDTO -> {
                            FindPrizeListResult.PrizeInfo prizeInfo = new FindPrizeListResult.PrizeInfo();
                            prizeInfo.setPrizeId(prizeDTO.getPrizeId());
                            prizeInfo.setPrizeName(prizeDTO.getName());
                            prizeInfo.setDescription(prizeDTO.getDescription());
                            prizeInfo.setImageUrl(prizeDTO.getImageUrl());
                            prizeInfo.setPrice(prizeDTO.getPrice());
                            return prizeInfo;
                        }).collect(Collectors.toList())
        );
        return result;

    }

}
