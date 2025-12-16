package com.zzz.lotterysystem.dao.mapper;

import com.zzz.lotterysystem.dao.dataobject.ActivityPrizeDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityPrizeMapper {

    @Insert("<script>"
            + " insert into activity_prize(activity_id, prize_id, prize_amount, prize_tiers, status)"
            + " values <foreach collection='items' item='item' index='index' separator=','>"
            + " (#{item.activityId}, #{item.prizeId}, #{item.prizeAmount}, # {item.prizeTiers}, #{item.status})"
            + " </foreach>"
            + " </script>")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int batchInsert(@Param("items") List<ActivityPrizeDO> activityPrizeDOList);

}
