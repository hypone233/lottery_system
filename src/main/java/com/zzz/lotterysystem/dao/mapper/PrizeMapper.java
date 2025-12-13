package com.zzz.lotterysystem.dao.mapper;

import com.zzz.lotterysystem.dao.dataobject.PrizeDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface PrizeMapper {
    @Insert("insert into prize(name,image_url,price,description)" +
            " values (#{name}, #{imageUrl},#{price},#{description})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int insert(PrizeDO prizeDO);
}
