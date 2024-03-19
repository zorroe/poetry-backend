package com.lxxx.poetrybackend;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.cat.IndicesResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexState;
import com.lxxx.poetrybackend.entity.Poetry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
@Slf4j
class PoetryBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private ElasticsearchClient esClient;

    /**
     * 获取索引
     */
    @Test
    void getIndex() throws IOException {
        SearchResponse<Poetry> response = esClient.search(s -> s
                        .index("poetry")
                        .query(q -> q
                                .match(t -> t
                                        .field("author")
                                        .query("李白")
                                )
                        ),
                Poetry.class
        );
        for (Hit<Poetry> hit : response.hits().hits()) {
            log.info(hit.source().toString());
        }
    }


    @Test
    void testQuery() throws IOException {

        String keyword = "李白";

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.should(s -> s.match(m -> m.field("author").query(keyword)));
        boolQueryBuilder.should(s -> s.match(m -> m.field("title").query(keyword)));
        boolQueryBuilder.should(s -> s.match(m -> m.field("content").query(keyword)));
        // 分页查询

        SearchRequest request = new SearchRequest.Builder().index("poetry").query(boolQueryBuilder.build()._toQuery()).from(5).size(10).build();
        // 添加分页参数
        SearchResponse<Poetry> response = esClient.search(request, Poetry.class);

        for (Hit<Poetry> hit : response.hits().hits()) {
            log.info(hit.source().toString());
        }

    }

}
