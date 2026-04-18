package com.gameitem.item.application;

import com.gameitem.common.exception.BizException;
import com.gameitem.item.domain.model.GameItem;
import com.gameitem.item.domain.model.ItemListing;
import com.gameitem.item.domain.repository.GameItemRepository;
import com.gameitem.item.domain.repository.ItemListingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemQueryService {

    private final GameItemRepository gameItemRepository;
    private final ItemListingRepository itemListingRepository;

    public ItemQueryService(GameItemRepository gameItemRepository, ItemListingRepository itemListingRepository) {
        this.gameItemRepository = gameItemRepository;
        this.itemListingRepository = itemListingRepository;
    }

    public GameItem getById(Long id) {
        return gameItemRepository.findById(id)
                .orElseThrow(() -> new BizException(40401, "道具不存在"));
    }
    
    public List<GameItem> listAll() {
        return gameItemRepository.findAll();
    }
    
    public Page<GameItem> listByPage(Pageable pageable) {
        return gameItemRepository.findAll(pageable);
    }
    
    @Transactional
    public GameItem createItem(String name, String game, String category,
                               BigDecimal referencePrice, String description, String iconUrl) {
        GameItem item = new GameItem();
        item.setName(name);
        item.setGame(game);
        item.setCategory(category);
        item.setReferencePrice(referencePrice);
        item.setDescription(description);
        item.setIconUrl(iconUrl);
        return gameItemRepository.save(item);
    }

    @Transactional
    public GameItem updateItem(Long id, String name, String game, String category,
                               BigDecimal referencePrice, String description, String iconUrl) {
        GameItem item = getById(id);
        if (name != null && !name.isBlank()) {
            item.setName(name);
        }
        if (game != null && !game.isBlank()) {
            item.setGame(game);
        }
        if (category != null) {
            item.setCategory(category);
        }
        if (referencePrice != null) {
            item.setReferencePrice(referencePrice);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (iconUrl != null) {
            item.setIconUrl(iconUrl);
        }
        return gameItemRepository.save(item);
    }

    @Transactional
    public ItemListing updateListing(Long id, BigDecimal price, Integer quantity, String description) {
        ItemListing listing = getListingById(id);
        if (listing.getStatus() != ItemListing.ListingStatus.ACTIVE) {
            throw new BizException(40001, "只能编辑活跃状态的挂牌");
        }
        if (price != null) {
            listing.setPrice(price);
        }
        if (quantity != null && quantity > 0) {
            listing.setQuantity(quantity);
        }
        if (description != null) {
            listing.setDescription(description);
        }
        return itemListingRepository.save(listing);
    }
    
    // 挂牌相关
    @Transactional
    public ItemListing createListing(Long itemId, Long sellerId, BigDecimal price, 
                                     Integer quantity, String description) {
        // 验证道具存在
        getById(itemId);
        
        ItemListing listing = new ItemListing();
        listing.setItemId(itemId);
        listing.setSellerId(sellerId);
        listing.setPrice(price);
        listing.setQuantity(quantity != null ? quantity : 1);
        listing.setDescription(description);
        listing.setStatus(ItemListing.ListingStatus.ACTIVE);
        return itemListingRepository.save(listing);
    }
    
    public ItemListing getListingById(Long id) {
        return itemListingRepository.findById(id)
                .orElseThrow(() -> new BizException(40402, "挂牌不存在"));
    }
    
    public Page<ItemListing> listActiveListings(Pageable pageable, String game, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        boolean hasGame = game != null && !game.isBlank();
        boolean hasCategory = category != null && !category.isBlank();
        boolean hasMinPrice = minPrice != null;
        boolean hasMaxPrice = maxPrice != null;

        if (hasGame && hasCategory && hasMinPrice && hasMaxPrice) {
            return itemListingRepository.findByGameAndCategoryAndPriceRange(game, category, minPrice, maxPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasGame && hasCategory && hasMinPrice) {
            return itemListingRepository.findByGameAndCategoryAndPriceGreaterThanEqual(game, category, minPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasGame && hasCategory && hasMaxPrice) {
            return itemListingRepository.findByGameAndCategoryAndPriceLessThanEqual(game, category, maxPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasGame && hasMinPrice && hasMaxPrice) {
            return itemListingRepository.findByGameAndPriceRange(game, minPrice, maxPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasGame && hasMinPrice) {
            return itemListingRepository.findByGameAndPriceGreaterThanEqual(game, minPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasGame && hasMaxPrice) {
            return itemListingRepository.findByGameAndPriceLessThanEqual(game, maxPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasCategory && hasMinPrice && hasMaxPrice) {
            return itemListingRepository.findByCategoryAndPriceRange(category, minPrice, maxPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasCategory && hasMinPrice) {
            return itemListingRepository.findByCategoryAndPriceGreaterThanEqual(category, minPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasCategory && hasMaxPrice) {
            return itemListingRepository.findByCategoryAndPriceLessThanEqual(category, maxPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasMinPrice && hasMaxPrice) {
            return itemListingRepository.findByPriceRange(minPrice, maxPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasMinPrice) {
            return itemListingRepository.findByPriceGreaterThanEqual(minPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasMaxPrice) {
            return itemListingRepository.findByPriceLessThanEqual(maxPrice, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasGame && hasCategory) {
            return itemListingRepository.findByGameAndCategoryAndStatus(game, category, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasGame) {
            return itemListingRepository.findByGameAndStatus(game, ItemListing.ListingStatus.ACTIVE, pageable);
        } else if (hasCategory) {
            return itemListingRepository.findByCategoryAndStatus(category, ItemListing.ListingStatus.ACTIVE, pageable);
        } else {
            return itemListingRepository.findByStatus(ItemListing.ListingStatus.ACTIVE, pageable);
        }
    }
    
    public Page<ItemListing> listListingsByItem(Long itemId, Pageable pageable) {
        return itemListingRepository.findByItemIdAndStatus(itemId, ItemListing.ListingStatus.ACTIVE, pageable);
    }
    
    public Page<ItemListing> listListingsBySeller(Long sellerId, Pageable pageable) {
        return itemListingRepository.findBySellerId(sellerId, pageable);
    }

    @Transactional
    public void reduceInventory(Long listingId, Integer quantity) {
        ItemListing listing = getListingById(listingId);
        if (listing.getQuantity() < quantity) {
            throw new BizException(40002, "库存不足");
        }
        int remaining = listing.getQuantity() - quantity;
        if (remaining == 0) {
            listing.setStatus(ItemListing.ListingStatus.SOLD);
        }
        listing.setQuantity(remaining);
        itemListingRepository.save(listing);
    }

    @Transactional
    public void cancelListing(Long listingId, Long sellerId) {
        ItemListing listing = getListingById(listingId);
        if (!listing.getSellerId().equals(sellerId)) {
            throw new BizException(40301, "无权操作此挂牌");
        }
        if (listing.getStatus() != ItemListing.ListingStatus.ACTIVE) {
            throw new BizException(40001, "挂牌状态不允许取消");
        }
        listing.setStatus(ItemListing.ListingStatus.CANCELLED);
        itemListingRepository.save(listing);
    }
    
    @Transactional
    public void markListingAsSold(Long listingId) {
        ItemListing listing = getListingById(listingId);
        listing.setStatus(ItemListing.ListingStatus.SOLD);
        itemListingRepository.save(listing);
    }
}
