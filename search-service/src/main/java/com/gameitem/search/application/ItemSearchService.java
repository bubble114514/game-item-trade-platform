package com.gameitem.search.application;

import com.gameitem.search.domain.ItemDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public ItemSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<ItemDocument> searchByKeyword(String keyword, int size) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        Criteria c = new Criteria("name").matches(keyword)
                .or(new Criteria("game").matches(keyword))
                .or(new Criteria("category").matches(keyword));
        CriteriaQuery query = new CriteriaQuery(c).setPageable(PageRequest.of(0, size));
        SearchHits<ItemDocument> hits = elasticsearchOperations.search(query, ItemDocument.class);
        return hits.getSearchHits().stream().map(SearchHit::getContent).toList();
    }
}
