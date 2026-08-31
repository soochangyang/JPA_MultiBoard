package scyang.mutilboard.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scyang.mutilboard.domain.auth.dto.AuthRequest;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.domain.member.repository.MemberRepository;
import scyang.mutilboard.global.common.MessageUtil;
import scyang.mutilboard.global.jwt.JwtTokenProvider;

import java.util.concurrent.TimeUnit;

import static scyang.mutilboard.global.common.MessageUtil.*;

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
    public Long signUp(AuthRequest.SignUp request){
        if (memberRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException(getMessage("duplicated.email"));
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = request.toEntity(encodedPassword);
        Member savedMember = memberRepository.save(member);

        return savedMember.getId();
    }

    public String login(AuthRequest.Login request){
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(()->
                    new IllegalArgumentException(getMessage("login.fail")));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())){
            throw new IllegalArgumentException(getMessage("login.fail"));
        }

        return jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());
    }

    @Transactional
    public void resetPassword(AuthRequest.ResetPassword request){
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(()->
                        new IllegalArgumentException(getMessage("no.member")));

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.updatePassword(encodedPassword);
    }

    @Transactional
    public void logout(String accessToken) throws IllegalArgumentException {

        boolean isValid = jwtTokenProvider.validateToken(accessToken);

        // 1 valid token
        if (!isValid){
            throw new IllegalArgumentException(getMessage("error.unauthorized"));
        }
        // 2 Redis TTL calcurate
        Long expiration = jwtTokenProvider.getExpiration(accessToken);


        // 3 add blacklist (key: token, value: "logout", TTL:valid time
        stringRedisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);


        //Check redis
        //String checkValue = stringRedisTemplate.opsForValue().get(accessToken);


    }
}
