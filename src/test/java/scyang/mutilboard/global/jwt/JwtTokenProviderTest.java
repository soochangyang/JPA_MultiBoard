package scyang.mutilboard.global.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.util.Predicates.isTrue;


@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void createAndValidateTokenForPostman() {
        // given
        String email = "test@example.com";
        String role = "USER";

        // when
        String token = jwtTokenProvider.createToken(email, role);

        // Postman 테스트를 위해 콘솔에 토큰 출력 (Print token for Postman testing)
        System.out.println("\n=======================================================");
        System.out.println("Postman Authorization 헤더에 복사해서 넣으세요:");
        System.out.println("Bearer " + token);
        System.out.println("=======================================================\n");

        // then
        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateInvalidToken(){
        //given
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.payload.signature";

        //when
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        //then
        assertThat(isValid).isFalse();
    }

    @Test
    void getAuthentication(){
        //given
        String email = "admin@example.com";
        String role = "ADMIN";
        String token = jwtTokenProvider.createToken(email, role);

        //when
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        //then
        assertThat(authentication.getName()).isEqualTo(email);
        assertThat(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))).isTrue();
    }
}