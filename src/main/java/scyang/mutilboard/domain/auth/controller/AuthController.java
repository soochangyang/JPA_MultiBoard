package scyang.mutilboard.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import scyang.mutilboard.domain.auth.dto.AuthRequest;
import scyang.mutilboard.domain.auth.service.AuthService;
import scyang.mutilboard.global.common.ApiResponse;

import static scyang.mutilboard.global.common.MessageUtil.getMessage;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<Long> signUp(@RequestBody @Valid AuthRequest.SignUp request){
        Long memberId = authService.signUp(request);
        return ApiResponse.created(memberId, getMessage("signup.success"));
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody @Valid AuthRequest.Login request){
        String token = authService.login(request);
        return ApiResponse.success(token, getMessage("login.success"));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            authService.logout(accessToken);
        }

        return ApiResponse.success(null, getMessage("logout.success"));
    }

    @PutMapping("/password")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid AuthRequest.ResetPassword request){
        authService.resetPassword(request);
        return ApiResponse.success(null, getMessage("password.reset.success"));
    }

}
