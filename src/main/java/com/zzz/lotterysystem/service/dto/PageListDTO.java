package com.zzz.lotterysystem.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageListDTO<T> {

    /**
     * 总量
     */
    private Integer total;
    /**
     * 当前页列表
     */
    private List<T> records;

    public PageListDTO(List<T> records, Integer total) {
        this.records = records;
        this.total = total;
    }
}
