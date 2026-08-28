package scyang.mutilboard.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scyang.mutilboard.domain.auth.service.AuthService;
import scyang.mutilboard.domain.auth.dto.AuthRequest;
import scyang.mutilboard.global.common.ApiResponse;
import scyang.mutilboard.global.common.MessageUtil;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signUp(@RequestBody @Valid AuthRequest.SignUp request){
        Long memberId = authService.signUp(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse
                        .success(memberId, MessageUtil.getMessage("signup.success")));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody @Valid AuthRequest.Login request){
        String token = authService.login(request);
        return ResponseEntity
                .ok(ApiResponse
                        .success(token, MessageUtil.getMessage("login.success")));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(){
        /**
         * ToDo Redis blackList add
         */
        return ResponseEntity
                .ok(ApiResponse
                        .success(null, MessageUtil.getMessage("logout.success")));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid AuthRequest.ResetPassword request){
        authService.resetPassword(request);
        return ResponseEntity
                .ok(ApiResponse
                        .success(null, MessageUtil.getMessage("password.reset.success")));
    }

}
