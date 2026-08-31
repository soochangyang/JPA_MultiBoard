package scyang.mutilboard.domain.member.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scyang.mutilboard.domain.member.dto.MemberRequest;
import scyang.mutilboard.domain.member.dto.MemberResponse;
import scyang.mutilboard.domain.member.dto.MemberSearchCondition;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.domain.member.repository.MemberRepository;
import scyang.mutilboard.global.common.MessageUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Member> getMember(String email) {
        return memberRepository.findByEmail(email);
    }

    public MemberResponse getMember(long id) {
        Member foundMember = memberRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(MessageUtil.getMessage("member.notfoun")));
        return new MemberResponse(foundMember);
    }

/*    public Optional<Member> findByName(String username) {
        return memberRepository.findByNickname(username);
    }*/

    @Transactional
    public void updateMember(Long id, MemberRequest.Update request) {
        Member findMember = memberRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(MessageUtil.getMessage("member.notfound")));
        findMember.updateInfo(
                request.getEmail(),
                request.getNickname(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );
    }

    public Page<MemberResponse> searchMembers(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchMemberPage(condition, pageable);
    }


}
