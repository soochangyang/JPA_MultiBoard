package scyang.mutilboard.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scyang.mutilboard.global.common.BaseEntity;
import scyang.mutilboard.global.common.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    //@Column(unique = true, nullable = false)
    //private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public Member(String email, String password, String nickname, Role role) {
        this.email = email;
        //this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.role = role != null ? role : Role.ROLE_USER ;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateInfo(String email, String nickname, String encodedPassword, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.password = encodedPassword;
    }
}
