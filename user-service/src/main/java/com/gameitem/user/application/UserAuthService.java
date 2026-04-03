package com.gameitem.user.application;

import cn.dev33.satoken.stp.StpUtil;
import com.gameitem.common.exception.BizException;
import com.gameitem.user.domain.model.UserAccount;
import com.gameitem.user.domain.repository.UserAccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserAuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserAuthService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public void register(String username, String password, String nickname, String role) {
        if (userAccountRepository.findByUsername(username).isPresent()) {
            throw new BizException(40001, "用户名已存在");
        }
        UserAccount u = new UserAccount();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setNickname(nickname != null ? nickname : username);
        
        // 设置角色
        if (role != null) {
            try {
                u.setRole(UserAccount.UserRole.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException e) {
                u.setRole(UserAccount.UserRole.USER);
            }
        } else {
            u.setRole(UserAccount.UserRole.USER);
        }
        
        userAccountRepository.save(u);
    }

    public Map<String, Object> login(String username, String password) {
        UserAccount u = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new BizException(40002, "用户不存在或密码错误"));
        if (!passwordEncoder.matches(password, u.getPasswordHash())) {
            throw new BizException(40002, "用户不存在或密码错误");
        }
        if (u.getStatus() == 0) {
            throw new BizException(40003, "账号已被禁用");
        }
        StpUtil.login(u.getId());
        Map<String, Object> body = new HashMap<>();
        body.put("token", StpUtil.getTokenValue());
        body.put("userId", u.getId());
        body.put("nickname", u.getNickname());
        body.put("role", u.getRole().name());
        return body;
    }
    
    public UserAccount getCurrentUser(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new BizException(40004, "用户不存在"));
    }
}
