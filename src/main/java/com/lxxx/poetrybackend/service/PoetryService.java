package com.lxxx.poetrybackend.service;

import com.lxxx.poetrybackend.common.model.EsParams;
import com.lxxx.poetrybackend.common.model.ResponseResult;
import com.lxxx.poetrybackend.common.model.ResponseVo;
import com.lxxx.poetrybackend.entity.Poetry;

import java.io.IOException;
import java.util.List;

public interface PoetryService {
    ResponseResult list();

    ResponseResult<ResponseVo> queryByDynasty(String dynasty, Integer pageNum, Integer pageSize) throws IOException;

    ResponseResult<ResponseVo> queryByAuthorOrTitle(String key,Integer pageNum, Integer pageSize);

    ResponseResult<ResponseVo> esSearch(EsParams esParams) throws IOException;

}
