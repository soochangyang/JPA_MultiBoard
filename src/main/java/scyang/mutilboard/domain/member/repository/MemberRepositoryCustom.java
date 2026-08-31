package scyang.mutilboard.domain.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import scyang.mutilboard.domain.member.dto.MemberResponse;
import scyang.mutilboard.domain.member.dto.MemberSearchCondition;

public interface MemberRepositoryCustom {
    Page<MemberResponse> searchMemberPage(MemberSearchCondition condition, Pageable pageable);
}
