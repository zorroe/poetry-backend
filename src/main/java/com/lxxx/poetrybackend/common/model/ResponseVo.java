package com.lxxx.poetrybackend.common.model;

import com.lxxx.poetrybackend.entity.Poetry;
import lombok.Data;

import java.util.List;

@Data
public class ResponseVo {
    private List<Poetry> list;

    private Long total;
}
