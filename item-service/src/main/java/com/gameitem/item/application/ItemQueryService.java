package com.gameitem.item.application;

import com.gameitem.common.exception.BizException;
import com.gameitem.item.domain.model.GameItem;
import com.gameitem.item.domain.repository.GameItemRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ItemQueryService {

    private final GameItemRepository gameItemRepository;

    public ItemQueryService(GameItemRepository gameItemRepository) {
        this.gameItemRepository = gameItemRepository;
    }

    @Cacheable(value = "hotItem", key = "#id", unless = "#result == null")
    public GameItem getById(Long id) {
        return gameItemRepository.findById(id).orElseThrow(() -> new BizException(40401, "道具不存在"));
    }
}
