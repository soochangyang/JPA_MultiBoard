package scyang.mutilboard.domain.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import scyang.mutilboard.global.common.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSearchCondition {
    private String nickname;
    private String email;
    private Role role;
}
