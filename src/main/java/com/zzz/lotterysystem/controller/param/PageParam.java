package com.zzz.lotterysystem.controller.param;

import lombok.Data;


@Data
public class PageParam {

    /**
     * 当前页
     */
    private Integer currentPage = 1;


    /**
     * 每页奖品数
     */
    private Integer pageSize = 10;

    /**
     * 偏移量
     * @return
     */
    public Integer offset() {
        return (currentPage-1) * pageSize;
    }


}
