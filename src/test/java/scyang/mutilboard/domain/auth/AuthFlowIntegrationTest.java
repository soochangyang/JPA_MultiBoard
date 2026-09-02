package scyang.mutilboard.domain.auth;


import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import scyang.mutilboard.domain.auth.dto.AuthRequest;
import scyang.mutilboard.domain.member.entity.Member;
import scyang.mutilboard.domain.member.repository.MemberRepository;
import scyang.mutilboard.global.common.Role;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private static final String TEST_EMAIL = "1080@superstar.com";
    private static final String TEST_PASSWORD = "123!@#";

    @BeforeEach
    void setUp() {
        //
        memberRepository.deleteAll();
        stringRedisTemplate.getConnectionFactory().getConnection().flushDb();

        User principal = new User(TEST_EMAIL, "", Collections.emptyList());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Member member = Member.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .nickname("Teaster")
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(member);
    }

    @Test
    void fullAuthFlowTest() throws Exception {

        //1.Login
        AuthRequest.Login loginRequest = AuthRequest.Login.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andReturn();

        // extract recived token
        String responseString = loginResult.getResponse().getContentAsString();
        String accessToken = JsonPath.parse(responseString).read("$.data.accessToken");
        String refreshToken = JsonPath.parse(responseString).read("$.data.refreshToken");

        //3. Request Member list use by Access Token
        mockMvc.perform(get("/api/members")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        //4. Refresh Tocken으로 새 토큰 재발급
        MvcResult reissueResult = mockMvc.perform(post("/api/auth/reissue")
                .header("Authorization-Refresh", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andReturn();

        String newAccessToken = JsonPath.parse(reissueResult.getResponse()
                .getContentAsString())
                .read("$.data.accessToken");

        //5. Logout (add blacklist)
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + newAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        //6. request member list use by bolcked tocken
        mockMvc.perform(get("/api/members")
                .header("Authorization", "Bearer " + newAccessToken))
                .andExpect(status().isUnauthorized()) // expect 401 error
                .andExpect(jsonPath("$.success").value(false));
    }


}
