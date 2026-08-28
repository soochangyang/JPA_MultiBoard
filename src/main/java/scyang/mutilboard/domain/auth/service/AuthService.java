package scyang.mutilboard.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scyang.mutilboard.domain.auth.dto.AuthRequest;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.domain.member.repository.MemberRepository;
import scyang.mutilboard.global.common.MessageUtil;
import scyang.mutilboard.global.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Long signUp(AuthRequest.SignUp request){
        if (memberRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException(MessageUtil.getMessage("duplicated.email"));
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = request.toEntity(encodedPassword);
        Member savedMember = memberRepository.save(member);

        return savedMember.getId();
    }

    public String login(AuthRequest.Login request){
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(()->
                    new IllegalArgumentException(MessageUtil.getMessage("login.fail")));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())){
            throw new IllegalArgumentException(MessageUtil.getMessage("login.fail"));
        }

        return jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());
    }

    @Transactional
    public void resetPassword(AuthRequest.ResetPassword request){
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(()->
                        new IllegalArgumentException(MessageUtil.getMessage("no.member")));

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.updatePassword(encodedPassword);
    }

}
