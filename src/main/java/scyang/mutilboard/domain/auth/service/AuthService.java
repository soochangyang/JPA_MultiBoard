package scyang.mutilboard.domain.auth.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import scyang.mutilboard.domain.auth.dto.AuthRequest;
import scyang.mutilboard.domain.auth.dto.TokenResponse;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.domain.member.repository.MemberRepository;
import scyang.mutilboard.global.jwt.JwtTokenProvider;

import java.util.concurrent.TimeUnit;

import static scyang.mutilboard.global.common.MessageUtil.getMessage;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    //redis
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    public Long signUp(AuthRequest.SignUp request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(getMessage("duplicated.email"));
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = request.toEntity(encodedPassword);
        Member savedMember = memberRepository.save(member);

        return savedMember.getId();
    }

    public TokenResponse login(AuthRequest.Login request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException(getMessage("login.fail")));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException(getMessage("login.fail"));
        }

        String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        //Save Refresh Token
        stringRedisTemplate.opsForValue().set(
                "RT:".concat(member.getEmail()),
                refreshToken,
                7,
                TimeUnit.DAYS
        );
        //return jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());
        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        // 1 Valid refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException(getMessage("invalid.resfresh_token"));
        }

        //2 Extract email from token
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        //3 Retrieve the user's refresh token from Redis.
        String savedRefreshToken = stringRedisTemplate.opsForValue().get("RT:".concat(email));

        //4 Whitelist verification: Reject if not in Redis or different from the token sent by the frontend.
        if (ObjectUtils.isEmpty(savedRefreshToken) || !savedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException(getMessage("no.resfresh_token"));
        }

        // 5 Issue a new access token upon successful verification.
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(getMessage("no.member")));

        String newAccessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());

        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Transactional
    public void resetPassword(AuthRequest.ResetPassword request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException(getMessage("no.member")));

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.updatePassword(encodedPassword);
    }

    @Transactional
    public void logout(String accessToken) throws IllegalArgumentException {

        boolean isValid = jwtTokenProvider.validateToken(accessToken);

        // 1 valid token
        if (!isValid) {
            throw new IllegalArgumentException(getMessage("error.unauthorized"));
        }
        // 2 Redis TTL calcurate
        Long expiration = jwtTokenProvider.getExpiration(accessToken);

        // 3 add blacklist (key: token, value: "logout", TTL:valid time
        stringRedisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        // delete Refresh Token (whitelist)
        String email = jwtTokenProvider.getEmailFromToken(accessToken);
        stringRedisTemplate.delete(email);


        //Check redis
        //String checkValue = stringRedisTemplate.opsForValue().get(accessToken);


    }
}
