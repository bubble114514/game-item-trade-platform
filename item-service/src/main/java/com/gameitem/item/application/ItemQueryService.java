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
    
    public Page<ItemListing> listActiveListings(Pageable pageable) {
        return itemListingRepository.findByStatus(ItemListing.ListingStatus.ACTIVE, pageable);
    }
    
    public Page<ItemListing> listListingsByItem(Long itemId, Pageable pageable) {
        return itemListingRepository.findByItemIdAndStatus(itemId, ItemListing.ListingStatus.ACTIVE, pageable);
    }
    
    public Page<ItemListing> listListingsBySeller(Long sellerId, Pageable pageable) {
        return itemListingRepository.findBySellerId(sellerId, pageable);
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
