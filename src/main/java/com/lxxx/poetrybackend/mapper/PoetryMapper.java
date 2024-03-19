package com.lxxx.poetrybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxxx.poetrybackend.entity.Poetry;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PoetryMapper extends BaseMapper<Poetry> {
}
