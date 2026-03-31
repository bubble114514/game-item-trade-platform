package com.gameitem.user.interfaces.rest;

import com.gameitem.common.api.ApiResult;
import com.gameitem.common.exception.BizException;
import com.gameitem.user.application.UserAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user/auth")
public class AuthController {

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    public record RegisterReq(String username, String password, String nickname) {}

    public record LoginReq(String username, String password) {}

    @PostMapping("/register")
    public ApiResult<Void> register(@RequestBody RegisterReq req) {
        userAuthService.register(req.username(), req.password(), req.nickname());
        return ApiResult.ok(null);
    }

    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody LoginReq req) {
        return ApiResult.ok(userAuthService.login(req.username(), req.password()));
    }

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleBiz(BizException e) {
        return ApiResult.fail(e.getCode(), e.getMessage());
    }
}
