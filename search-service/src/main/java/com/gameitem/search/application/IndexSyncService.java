package com.gameitem.search.application;

import com.gameitem.search.domain.ItemDocument;
import com.gameitem.search.domain.ItemSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class IndexSyncService {

    private static final Logger log = LoggerFactory.getLogger(IndexSyncService.class);

    private final ElasticsearchOperations elasticsearchOperations;
    private final ItemSearchRepository itemSearchRepository;
    private final RestTemplate restTemplate;

    public IndexSyncService(
            ElasticsearchOperations elasticsearchOperations,
            @Lazy ItemSearchRepository itemSearchRepository,
            RestTemplateBuilder restTemplateBuilder) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.itemSearchRepository = itemSearchRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Scheduled(fixedDelay = 60000)
    public void syncItemsToElasticsearch() {
        log.info("开始同步道具数据到Elasticsearch...");
        try {
            String itemServiceUrl = "http://localhost:8082/api/item";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(itemServiceUrl + "?_=sync", Map.class);

            if (response != null && response.get("code") != null && ((Number) response.get("code")).intValue() == 0) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("data");
                if (items != null) {
                    int count = 0;
                    for (Map<String, Object> item : items) {
                        try {
                            ItemDocument doc = convertToDocument(item);
                            itemSearchRepository.save(doc);
                            count++;
                        } catch (Exception e) {
                            log.error("同步道具失败: {}", item, e);
                        }
                    }
                    log.info("成功同步 {} 个道具到Elasticsearch", count);
                }
            }
        } catch (Exception e) {
            log.error("同步道具数据失败", e);
        }
    }

    public void syncSingleItem(Long itemId) {
        try {
            String url = "http://localhost:8082/api/item/" + itemId;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.get("code") != null && ((Number) response.get("code")).intValue() == 0) {
                @SuppressWarnings("unchecked")
                Map<String, Object> item = (Map<String, Object>) response.get("data");
                if (item != null) {
                    ItemDocument doc = convertToDocument(item);
                    itemSearchRepository.save(doc);
                    log.info("同步单个道具成功: {}", itemId);
                }
            }
        } catch (Exception e) {
            log.error("同步单个道具失败: {}", itemId, e);
        }
    }

    public void deleteFromIndex(Long itemId) {
        try {
            itemSearchRepository.deleteById(itemId);
            log.info("从索引中删除道具: {}", itemId);
        } catch (Exception e) {
            log.error("从索引中删除道具失败: {}", itemId, e);
        }
    }

    private ItemDocument convertToDocument(Map<String, Object> item) {
        ItemDocument doc = new ItemDocument();
        doc.setId(((Number) item.get("id")).longValue());
        doc.setName((String) item.get("name"));
        doc.setGame((String) item.get("game"));
        doc.setCategory((String) item.get("category"));

        Object priceObj = item.get("referencePrice");
        if (priceObj != null) {
            if (priceObj instanceof BigDecimal) {
                doc.setReferencePrice((BigDecimal) priceObj);
            } else if (priceObj instanceof Number) {
                doc.setReferencePrice(BigDecimal.valueOf(((Number) priceObj).doubleValue()));
            } else if (priceObj instanceof String) {
                doc.setReferencePrice(new BigDecimal((String) priceObj));
            }
        }

        doc.setDescription((String) item.get("description"));
        doc.setIconUrl((String) item.get("iconUrl"));

        Object activeObj = item.get("active");
        if (activeObj instanceof Boolean) {
            doc.setActive((Boolean) activeObj);
        } else {
            doc.setActive(true);
        }

        return doc;
    }
}