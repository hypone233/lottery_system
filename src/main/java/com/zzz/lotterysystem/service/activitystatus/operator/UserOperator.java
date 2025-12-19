package com.zzz.lotterysystem.service.activitystatus.operator;

import com.zzz.lotterysystem.service.dto.ConvertActivityStatusDTO;
import org.springframework.stereotype.Component;

@Component
public class UserOperator extends AbstractActivityOperator{


    @Override
    public Integer sequence() {
        return 1;
    }

    @Override
    public Boolean needConvert(ConvertActivityStatusDTO convertActivityStatusDTO) {
        return null;
    }

    @Override
    public void convert(ConvertActivityStatusDTO convertActivityStatusDTO) {

    }
}
