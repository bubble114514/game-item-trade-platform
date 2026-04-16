package com.gameitem.message.domain.repository;

import com.gameitem.message.domain.model.UserMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {

    Page<UserMessage> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<UserMessage> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead, Pageable pageable);

    Long countByUserIdAndIsRead(Long userId, Boolean isRead);
}