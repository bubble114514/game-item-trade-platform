package com.gameitem.item.domain.repository;

import com.gameitem.item.domain.model.ItemListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemListingRepository extends JpaRepository<ItemListing, Long> {
    
    Page<ItemListing> findByStatus(ItemListing.ListingStatus status, Pageable pageable);
    
    Page<ItemListing> findByItemIdAndStatus(Long itemId, ItemListing.ListingStatus status, Pageable pageable);
    
    Page<ItemListing> findBySellerId(Long sellerId, Pageable pageable);
    
    List<ItemListing> findBySellerIdAndStatus(Long sellerId, ItemListing.ListingStatus status);
}
