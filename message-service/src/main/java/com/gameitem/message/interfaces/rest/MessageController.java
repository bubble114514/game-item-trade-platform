package com.gameitem.message.interfaces.rest;

import com.gameitem.common.api.ApiResult;
import com.gameitem.message.application.MessageService;
import com.gameitem.message.domain.model.UserMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/health")
    public ApiResult<Map<String, String>> health() {
        return ApiResult.ok(Map.of("module", "message-service"));
    }

    @GetMapping("/users/{userId}/messages")
    public ApiResult<Page<UserMessage>> getUserMessages(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResult.ok(messageService.getUserMessages(userId, pageable));
    }

    @GetMapping("/users/{userId}/messages/unread")
    public ApiResult<Page<UserMessage>> getUnreadMessages(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResult.ok(messageService.getUnreadMessages(userId, pageable));
    }

    @GetMapping("/users/{userId}/messages/unread/count")
    public ApiResult<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        return ApiResult.ok(Map.of("count", messageService.getUnreadCount(userId)));
    }

    @PostMapping("/users/{userId}/messages/{messageId}/read")
    public ApiResult<Void> markAsRead(
            @PathVariable Long userId,
            @PathVariable Long messageId) {
        messageService.markAsRead(messageId, userId);
        return ApiResult.ok(null);
    }

    @PostMapping("/users/{userId}/messages/read-all")
    public ApiResult<Void> markAllAsRead(@PathVariable Long userId) {
        messageService.markAllAsRead(userId);
        return ApiResult.ok(null);
    }
}