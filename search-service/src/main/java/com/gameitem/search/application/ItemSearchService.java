package com.gameitem.search.application;

import com.gameitem.search.domain.ItemDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public ItemSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<ItemDocument> searchByKeyword(String keyword, BigDecimal minPrice, BigDecimal maxPrice, int size) {
        Criteria c = new Criteria();

        if (keyword != null && !keyword.isBlank()) {
            c = new Criteria("name").matches(keyword)
                    .or(new Criteria("game").matches(keyword))
                    .or(new Criteria("category").matches(keyword));
        }

        if (minPrice != null || maxPrice != null) {
            Criteria priceCriteria = new Criteria("referencePrice");
            if (minPrice != null && maxPrice != null) {
                priceCriteria = priceCriteria.between(minPrice, maxPrice);
            } else if (minPrice != null) {
                priceCriteria = priceCriteria.greaterThanEqual(minPrice);
            } else if (maxPrice != null) {
                priceCriteria = priceCriteria.lessThanEqual(maxPrice);
            }
            c = c.and(priceCriteria);
        }

        CriteriaQuery query = new CriteriaQuery(c).setPageable(PageRequest.of(0, size));
        SearchHits<ItemDocument> hits = elasticsearchOperations.search(query, ItemDocument.class);
        return hits.getSearchHits().stream().map(SearchHit::getContent).toList();
    }
}
