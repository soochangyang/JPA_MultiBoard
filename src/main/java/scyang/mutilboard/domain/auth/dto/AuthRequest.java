package scyang.mutilboard.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.global.common.Role;

public class AuthRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUp{
        @NotBlank(message = "required.email")
        @Email(message = "invalid.format.email")
        private String email;

        @NotBlank(message = "required.password")
        private String password;

        @NotBlank(message = "required.name")
        private String nickname;

        public Member toEntity(String encodedPassword){
            return Member.builder()
                    .email(this.email)
                    .password(encodedPassword)
                    .nickname(this.nickname)
                    .role(Role.ROLE_USER)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Login{

        @NotBlank(message = "required.email")
        @Email(message = "invalid.format.email")
        private String email;

        @NotBlank(message = "required.password")
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPassword{
        @NotBlank(message = "required.email")
        @Email(message = "invalid.format.email")
        private String email;

        @NotBlank(message = "required.newpassword")
        private String newPassword;
    }
}
