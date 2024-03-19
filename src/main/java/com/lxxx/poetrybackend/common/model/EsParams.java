package com.lxxx.poetrybackend.common.model;

import lombok.Data;

@Data
public class EsParams {

    private String keyword;

    private Boolean isAuthor;

    private Boolean isTitle;

    private Boolean isContent;

    private Integer pageNum;

    private Integer pageSize;
}
