package scyang.mutilboard.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.global.common.Role;

public class MemberRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update{
        private String nickname;
        private String email;
        private Role role;
        private String password;
    }
}
