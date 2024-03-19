package com.lxxx.poetrybackend.service.impl;

import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxxx.poetrybackend.common.model.EsParams;
import com.lxxx.poetrybackend.common.model.ResponseResult;
import com.lxxx.poetrybackend.common.model.ResponseVo;
import com.lxxx.poetrybackend.entity.Poetry;
import com.lxxx.poetrybackend.mapper.PoetryMapper;
import com.lxxx.poetrybackend.service.PoetryService;
import net.sf.jsqlparser.expression.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PoetryServiceImpl implements PoetryService {

    @Autowired
    private PoetryMapper poetryMapper;

    @Autowired
    private ElasticsearchClient esClient;

    @Override
    public ResponseResult list() {
        Page<Poetry> page = new Page<>(1, 10);
        Page<Poetry> poetryPage = poetryMapper.selectPage(page, null);
        List<Poetry> records = poetryPage.getRecords();
        return ResponseResult.success(records);
    }

    @Override
    public ResponseResult<ResponseVo> queryByDynasty(String dynasty, Integer pageNum, Integer pageSize) throws IOException {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        if (StrUtil.isEmpty(dynasty)) {
            return ResponseResult.fail(null, "请选择朝代");
        }
        boolQueryBuilder.must(f -> f.term(t -> t.field("dynasty").value(dynasty)));
        Integer from = (pageNum - 1) * pageSize;

        SearchRequest request = new SearchRequest.Builder().index("poetry").query(boolQueryBuilder.build()._toQuery()).from(from).size(pageSize).build();
        request.trackTotalHits();
        return esSearch(request);
    }

    @Override
    public ResponseResult<ResponseVo> queryByAuthorOrTitle(String key, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Poetry> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isEmpty(key)) {
            return ResponseResult.fail(null, "请输入关键词");
        }
        queryWrapper = queryWrapper.eq(Poetry::getAuthor, key).or().eq(Poetry::getTitle, key);
        Page<Poetry> page = new Page<>(pageNum, pageSize);
        Page<Poetry> poetryPage = poetryMapper.selectPage(page, queryWrapper);
        List<Poetry> records = poetryPage.getRecords();
        ResponseVo responseVo = new ResponseVo();
        responseVo.setTotal(poetryPage.getTotal());
        responseVo.setList(records);
        return ResponseResult.success(responseVo);
    }

    @Override
    public ResponseResult<ResponseVo> esSearch(EsParams esParams) throws IOException {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        if (StrUtil.isEmpty(esParams.getKeyword())) {
            return ResponseResult.fail(null, "请输入关键词");
        }
        if (!esParams.getIsAuthor() && !esParams.getIsTitle() && !esParams.getIsContent()) {
            return ResponseResult.fail(null, "请选择搜索范围");
        }
        if (esParams.getIsAuthor()) {
            boolQueryBuilder.should(s -> s.match(m -> m.field("author").query(esParams.getKeyword())));
        }
        if (esParams.getIsTitle()) {
            boolQueryBuilder.should(s -> s.match(m -> m.field("title").query(esParams.getKeyword())));
        }
        if (esParams.getIsContent()) {
            boolQueryBuilder.should(s -> s.match(m -> m.field("content").query(esParams.getKeyword())));
        }

        Integer pageNum = esParams.getPageNum();
        Integer pageSize = esParams.getPageSize();
        Integer from = (pageNum - 1) * pageSize;
        SearchRequest request = new SearchRequest.Builder().index("poetry").query(boolQueryBuilder.build()._toQuery()).from(from).size(pageSize).build();
        return esSearch(request);
    }

    // 提取公共方法
    private ResponseResult<ResponseVo> esSearch(SearchRequest request) throws IOException {
        // 去除10000条的查询限制
        SearchResponse<Poetry> response = esClient.search(request, Poetry.class);
        List<Hit<Poetry>> hits = response.hits().hits();
        ResponseVo responseVo = new ResponseVo();
        responseVo.setTotal(response.hits().total().value());
        // 把hits转换成poetry
        List<Poetry> poetryList = hits.stream().map(Hit::source).collect(Collectors.toList());
        responseVo.setList(poetryList);
        return ResponseResult.success(responseVo);
    }


}
