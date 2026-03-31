package com.gameitem.search.domain;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemSearchRepository extends ElasticsearchRepository<ItemDocument, Long> {
}
