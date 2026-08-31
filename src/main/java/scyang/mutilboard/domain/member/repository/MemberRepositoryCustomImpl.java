package scyang.mutilboard.domain.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import scyang.mutilboard.domain.member.dto.MemberResponse;
import scyang.mutilboard.domain.member.dto.MemberSearchCondition;
import scyang.mutilboard.domain.member.dto.QMemberResponse;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.global.common.Querydsl4RepositorySupport;
import scyang.mutilboard.global.common.Role;

import static scyang.mutilboard.domain.member.entity.QMember.member;

public class MemberRepositoryCustomImpl extends Querydsl4RepositorySupport implements MemberRepositoryCustom {

    public MemberRepositoryCustomImpl() {
        super(Member.class);
    }

    @Override
    public Page<MemberResponse> searchMemberPage(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable,
                queryFactory -> queryFactory
                        .select(new QMemberResponse(
                                member.id,
                                member.email,
                                member.nickname,
                                member.role,
                                member.createdAt,
                                member.updatedAt,
                                member.createdBy,
                                member.updatedBy
                        ))
                        .from(member)
                        .where(nicknameContains(condition.getNickname()),
                                emailContains(condition.getEmail()),
                                roleEq(condition.getRole())),
                queryFactory -> queryFactory
                        .select(member.count())
                        .from(member)
                        .where(nicknameContains(condition.getNickname()),
                                emailContains(condition.getEmail()),
                                roleEq(condition.getRole()))
        );
    }

    private BooleanExpression nicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? member.nickname.contains(nickname) : null;
    }

    private BooleanExpression emailContains(String email) {
        return StringUtils.hasText(email) ? member.email.contains(email) : null;
    }

    private BooleanExpression roleEq(Role role) {
        if (role != null) {
            return member.role.eq(role);
        }
        return null;
    }
}
