package com.gameitem.search.interfaces.rest;

import com.gameitem.common.api.ApiResult;
import com.gameitem.search.application.ItemSearchService;
import com.gameitem.search.domain.ItemDocument;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final ItemSearchService itemSearchService;

    public SearchController(ItemSearchService itemSearchService) {
        this.itemSearchService = itemSearchService;
    }

    @GetMapping("/items")
    public ApiResult<List<ItemDocument>> search(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(itemSearchService.searchByKeyword(q, minPrice, maxPrice, size));
    }
}
