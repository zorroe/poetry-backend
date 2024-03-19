package com.lxxx.poetrybackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("poetry")
public class Poetry {

    private Integer id;

    private String title;

    private String dynasty;

    private String author;

    private String content;
}
