package com.gameitem.item.interfaces.rest;

import com.gameitem.common.api.ApiResult;
import com.gameitem.common.exception.BizException;
import com.gameitem.item.application.ItemQueryService;
import com.gameitem.item.domain.model.GameItem;
import com.gameitem.item.domain.model.ItemListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final ItemQueryService itemQueryService;

    public ItemController(ItemQueryService itemQueryService) {
        this.itemQueryService = itemQueryService;
    }

    // 道具基础接口
    @GetMapping("/{id}")
    public ApiResult<GameItem> get(@PathVariable Long id) {
        return ApiResult.ok(itemQueryService.getById(id));
    }
    
    @GetMapping
    public ApiResult<List<GameItem>> listAll() {
        return ApiResult.ok(itemQueryService.listAll());
    }
    
    @GetMapping("/page")
    public ApiResult<Page<GameItem>> listByPage(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResult.ok(itemQueryService.listByPage(pageable));
    }
    
    @PostMapping
    public ApiResult<GameItem> create(@RequestBody CreateItemReq req) {
        GameItem item = itemQueryService.createItem(
            req.name(), req.game(), req.category(),
            req.referencePrice(), req.description(), req.iconUrl()
        );
        return ApiResult.ok(item);
    }

    @PutMapping("/{id}")
    public ApiResult<GameItem> update(@PathVariable Long id, @RequestBody UpdateItemReq req) {
        GameItem item = itemQueryService.updateItem(
            id, req.name(), req.game(), req.category(),
            req.referencePrice(), req.description(), req.iconUrl()
        );
        return ApiResult.ok(item);
    }

    @PutMapping("/listings/{id}")
    public ApiResult<ItemListing> updateListing(@PathVariable Long id, @RequestBody UpdateListingReq req) {
        ItemListing listing = itemQueryService.updateListing(
            id, req.price(), req.quantity(), req.description()
        );
        return ApiResult.ok(listing);
    }
    
    // 挂牌接口
    @PostMapping("/listings")
    public ApiResult<ItemListing> createListing(@RequestBody CreateListingReq req) {
        ItemListing listing = itemQueryService.createListing(
            req.itemId(), req.sellerId(), req.price(), 
            req.quantity(), req.description()
        );
        return ApiResult.ok(listing);
    }
    
    @GetMapping("/listings/{id}")
    public ApiResult<ItemListing> getListing(@PathVariable Long id) {
        return ApiResult.ok(itemQueryService.getListingById(id));
    }
    
    @GetMapping("/listings")
    public ApiResult<Page<ItemListing>> listActiveListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String game,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResult.ok(itemQueryService.listActiveListings(pageable, game, category, minPrice, maxPrice));
    }
    
    @GetMapping("/{itemId}/listings")
    public ApiResult<Page<ItemListing>> listListingsByItem(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResult.ok(itemQueryService.listListingsByItem(itemId, pageable));
    }
    
    @GetMapping("/seller/{sellerId}/listings")
    public ApiResult<Page<ItemListing>> listListingsBySeller(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResult.ok(itemQueryService.listListingsBySeller(sellerId, pageable));
    }
    
    @PostMapping("/listings/{listingId}/cancel")
    public ApiResult<Void> cancelListing(@PathVariable Long listingId, @RequestParam Long sellerId) {
        itemQueryService.cancelListing(listingId, sellerId);
        return ApiResult.ok(null);
    }

    @PostMapping("/listings/{listingId}/reduce")
    public ApiResult<Void> reduceInventory(@PathVariable Long listingId, @RequestParam Integer quantity) {
        itemQueryService.reduceInventory(listingId, quantity);
        return ApiResult.ok(null);
    }

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleBiz(BizException e) {
        return ApiResult.fail(e.getCode(), e.getMessage());
    }
    
    // 请求记录
    public record CreateItemReq(String name, String game, String category,
                                BigDecimal referencePrice, String description, String iconUrl) {}

    public record UpdateItemReq(String name, String game, String category,
                                BigDecimal referencePrice, String description, String iconUrl) {}

    public record CreateListingReq(Long itemId, Long sellerId, BigDecimal price,
                                   Integer quantity, String description) {}

    public record UpdateListingReq(BigDecimal price, Integer quantity, String description) {}
}
