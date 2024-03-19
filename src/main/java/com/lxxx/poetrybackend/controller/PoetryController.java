package com.lxxx.poetrybackend.controller;

import com.lxxx.poetrybackend.common.model.EsParams;
import com.lxxx.poetrybackend.common.model.ResponseResult;
import com.lxxx.poetrybackend.common.model.ResponseVo;
import com.lxxx.poetrybackend.entity.Poetry;
import com.lxxx.poetrybackend.service.PoetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/poetry")
public class PoetryController {

    @Autowired
    private PoetryService poetryService;


    @GetMapping("/list")
    private ResponseResult<List<Poetry>> poetryList() {
        return poetryService.list();
    }

    @GetMapping("/queryByDynasty")
    private ResponseResult<ResponseVo> queryByDynasty(@RequestParam(value = "dynasty", defaultValue = "唐") String dynasty,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) throws IOException {
        return poetryService.queryByDynasty(dynasty, pageNum, pageSize);
    }

    @GetMapping("/queryByAuthorOrTitle")
    private ResponseResult<ResponseVo> queryByAuthorOrTitle(@RequestParam(value = "key") String key,
                                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return poetryService.queryByAuthorOrTitle(key, pageNum, pageSize);
    }

    @PostMapping("/esSearch")
    private ResponseResult<ResponseVo> esSearch(@RequestBody EsParams esParams) throws IOException {
        return poetryService.esSearch(esParams);
    }

}
