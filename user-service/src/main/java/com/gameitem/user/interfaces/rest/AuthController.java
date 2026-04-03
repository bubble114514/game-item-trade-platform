package com.gameitem.user.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import com.gameitem.common.api.ApiResult;
import com.gameitem.common.exception.BizException;
import com.gameitem.user.application.UserAuthService;
import com.gameitem.user.domain.model.UserAccount;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/auth")
public class AuthController {

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    public record RegisterReq(String username, String password, String nickname, String role) {}

    public record LoginReq(String username, String password) {}

    @PostMapping("/register")
    public ApiResult<Void> register(@RequestBody RegisterReq req) {
        userAuthService.register(req.username(), req.password(), req.nickname(), req.role());
        return ApiResult.ok(null);
    }

    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody LoginReq req) {
        return ApiResult.ok(userAuthService.login(req.username(), req.password()));
    }
    
    @PostMapping("/logout")
    public ApiResult<Void> logout() {
        StpUtil.logout();
        return ApiResult.ok(null);
    }
    
    @GetMapping("/current")
    public ApiResult<Map<String, Object>> getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserAccount user = userAuthService.getCurrentUser(userId);
        Map<String, Object> body = Map.of(
            "userId", user.getId(),
            "username", user.getUsername(),
            "nickname", user.getNickname(),
            "role", user.getRole().name()
        );
        return ApiResult.ok(body);
    }

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleBiz(BizException e) {
        return ApiResult.fail(e.getCode(), e.getMessage());
    }
}
