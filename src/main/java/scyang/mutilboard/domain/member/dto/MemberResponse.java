package scyang.mutilboard.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.global.common.Role;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberResponse {

    private Long memberId;
    private String email;
    private String nickname;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    @QueryProjection
    public MemberResponse(Long memberId, String email,
                          String nickname, Role role,
                          LocalDateTime createdAt, LocalDateTime updatedAt,
                          String createdBy, String updatedBy) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public MemberResponse(Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.role = member.getRole();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
        this.createdBy = member.getCreatedBy();
        this.updatedBy = member.getUpdatedBy();
    }
}
