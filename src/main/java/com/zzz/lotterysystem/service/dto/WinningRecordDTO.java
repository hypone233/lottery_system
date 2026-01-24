package com.zzz.lotterysystem.service.dto;

import com.zzz.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityPrizeTiersEnum;
import lombok.Data;

import java.util.Date;

@Data
public class WinningRecordDTO {
    private Long winnerId;

    private String winnerName;

    private String prizeName;

    private ActivityPrizeTiersEnum prizeTier;

    private Date winningTime;

}
