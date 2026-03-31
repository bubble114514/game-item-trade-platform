package com.gameitem.item.domain.repository;

import com.gameitem.item.domain.model.GameItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameItemRepository extends JpaRepository<GameItem, Long> {
}
