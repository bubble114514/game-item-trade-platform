package com.gameitem.trade.interfaces.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "item-service", url = "http://localhost:8082")
public interface ItemServiceClient {

    @GetMapping("/api/item/listings/{id}")
    ListingApiResponse getListing(@PathVariable("id") Long listingId);

    @PostMapping("/api/item/listings/{listingId}/reduce")
    void reduceInventory(@PathVariable("listingId") Long listingId, @RequestParam Integer quantity);

    class ListingApiResponse {
        private int code;
        private String message;
        private ItemListingDTO data;

        public int getCode() { return code; }
        public String getMessage() { return message; }
        public ItemListingDTO getData() { return data; }
    }

    class ItemListingDTO {
        private Long id;
        private Long itemId;
        private Long sellerId;
        private java.math.BigDecimal price;
        private Integer quantity;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Long getSellerId() { return sellerId; }
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
        public java.math.BigDecimal getPrice() { return price; }
        public void setPrice(java.math.BigDecimal price) { this.price = price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
