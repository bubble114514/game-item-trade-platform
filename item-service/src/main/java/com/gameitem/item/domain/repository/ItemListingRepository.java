package com.gameitem.item.domain.repository;

import com.gameitem.item.domain.model.ItemListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemListingRepository extends JpaRepository<ItemListing, Long> {
    
    Page<ItemListing> findByStatus(ItemListing.ListingStatus status, Pageable pageable);
    
    Page<ItemListing> findByItemIdAndStatus(Long itemId, ItemListing.ListingStatus status, Pageable pageable);
    
    Page<ItemListing> findBySellerId(Long sellerId, Pageable pageable);
    
    List<ItemListing> findBySellerIdAndStatus(Long sellerId, ItemListing.ListingStatus status);
    
    // 根据游戏和分类过滤，使用JPQL关联查询
    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.game = :game AND gi.category = :category")
    Page<ItemListing> findByGameAndCategoryAndStatus(@Param("game") String game, @Param("category") String category, @Param("status") ItemListing.ListingStatus status, Pageable pageable);
    
    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.game = :game")
    Page<ItemListing> findByGameAndStatus(@Param("game") String game, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.category = :category")
    Page<ItemListing> findByCategoryAndStatus(@Param("category") String category, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND il.price >= :minPrice")
    Page<ItemListing> findByPriceGreaterThanEqual(@Param("minPrice") java.math.BigDecimal minPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND il.price <= :maxPrice")
    Page<ItemListing> findByPriceLessThanEqual(@Param("maxPrice") java.math.BigDecimal maxPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND il.price BETWEEN :minPrice AND :maxPrice")
    Page<ItemListing> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, @Param("maxPrice") java.math.BigDecimal maxPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.game = :game AND il.price >= :minPrice")
    Page<ItemListing> findByGameAndPriceGreaterThanEqual(@Param("game") String game, @Param("minPrice") java.math.BigDecimal minPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.game = :game AND il.price <= :maxPrice")
    Page<ItemListing> findByGameAndPriceLessThanEqual(@Param("game") String game, @Param("maxPrice") java.math.BigDecimal maxPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.game = :game AND il.price BETWEEN :minPrice AND :maxPrice")
    Page<ItemListing> findByGameAndPriceRange(@Param("game") String game, @Param("minPrice") java.math.BigDecimal minPrice, @Param("maxPrice") java.math.BigDecimal maxPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.category = :category AND il.price >= :minPrice")
    Page<ItemListing> findByCategoryAndPriceGreaterThanEqual(@Param("category") String category, @Param("minPrice") java.math.BigDecimal minPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.category = :category AND il.price <= :maxPrice")
    Page<ItemListing> findByCategoryAndPriceLessThanEqual(@Param("category") String category, @Param("maxPrice") java.math.BigDecimal maxPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.category = :category AND il.price BETWEEN :minPrice AND :maxPrice")
    Page<ItemListing> findByCategoryAndPriceRange(@Param("category") String category, @Param("minPrice") java.math.BigDecimal minPrice, @Param("maxPrice") java.math.BigDecimal maxPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.game = :game AND gi.category = :category AND il.price >= :minPrice")
    Page<ItemListing> findByGameAndCategoryAndPriceGreaterThanEqual(@Param("game") String game, @Param("category") String category, @Param("minPrice") java.math.BigDecimal minPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.game = :game AND gi.category = :category AND il.price <= :maxPrice")
    Page<ItemListing> findByGameAndCategoryAndPriceLessThanEqual(@Param("game") String game, @Param("category") String category, @Param("maxPrice") java.math.BigDecimal maxPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);

    @Query("SELECT il FROM ItemListing il JOIN com.gameitem.item.domain.model.GameItem gi ON il.itemId = gi.id WHERE il.status = :status AND gi.game = :game AND gi.category = :category AND il.price BETWEEN :minPrice AND :maxPrice")
    Page<ItemListing> findByGameAndCategoryAndPriceRange(@Param("game") String game, @Param("category") String category, @Param("minPrice") java.math.BigDecimal minPrice, @Param("maxPrice") java.math.BigDecimal maxPrice, @Param("status") ItemListing.ListingStatus status, Pageable pageable);
}
