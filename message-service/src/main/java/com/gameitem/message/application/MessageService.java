package com.gameitem.message.application;

import com.gameitem.message.domain.model.UserMessage;
import com.gameitem.message.domain.repository.UserMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final UserMessageRepository userMessageRepository;

    public MessageService(UserMessageRepository userMessageRepository) {
        this.userMessageRepository = userMessageRepository;
    }

    @Transactional
    public UserMessage sendMessage(Long userId, String type, String title, String content,
                                   Long orderId, Long relatedUserId) {
        UserMessage message = new UserMessage();
        message.setUserId(userId);
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setOrderId(orderId);
        message.setRelatedUserId(relatedUserId);
        message.setIsRead(false);

        UserMessage saved = userMessageRepository.save(message);
        log.info("发送消息给用户 {}: {}", userId, title);
        return saved;
    }

    public Page<UserMessage> getUserMessages(Long userId, Pageable pageable) {
        return userMessageRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<UserMessage> getUnreadMessages(Long userId, Pageable pageable) {
        return userMessageRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false, pageable);
    }

    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        userMessageRepository.findById(messageId).ifPresent(msg -> {
            if (msg.getUserId().equals(userId)) {
                msg.setIsRead(true);
                userMessageRepository.save(msg);
                log.info("用户 {} 已读消息 {}", userId, messageId);
            }
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        Page<UserMessage> unread = getUnreadMessages(userId, Pageable.unpaged());
        unread.forEach(msg -> msg.setIsRead(true));
        userMessageRepository.saveAll(unread.getContent());
        log.info("用户 {} 已读所有消息", userId);
    }

    public Long getUnreadCount(Long userId) {
        return userMessageRepository.countByUserIdAndIsRead(userId, false);
    }
}