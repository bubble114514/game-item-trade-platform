package com.gameitem.item.interfaces.rest;

import com.gameitem.common.api.ApiResult;
import com.gameitem.common.exception.BizException;
import com.gameitem.item.application.ItemQueryService;
import com.gameitem.item.domain.model.GameItem;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final ItemQueryService itemQueryService;

    public ItemController(ItemQueryService itemQueryService) {
        this.itemQueryService = itemQueryService;
    }

    @GetMapping("/{id}")
    public ApiResult<GameItem> get(@PathVariable Long id) {
        return ApiResult.ok(itemQueryService.getById(id));
    }

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult<Void> handleBiz(BizException e) {
        return ApiResult.fail(e.getCode(), e.getMessage());
    }
}
