package scyang.mutilboard.global.error;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import scyang.mutilboard.global.common.ApiResponse;
import scyang.mutilboard.global.common.MessageUtil;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // 1 Set HTTP status code
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 2 Set content type and encoding (JSON, UTF-8)
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 3 Fetch i18n message
        //Assuming "error.unauthorized" is defined in messages.properties
        String errorMessage = MessageUtil.getMessage("error.unauthorized");

        // 4 Create ApiResponse object
        //
        ApiResponse errorResponse = ApiResponse.error(errorMessage, HttpStatus.UNAUTHORIZED);


        // 5 Convert to JSON  and write to response
        String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody());
        response.getWriter().write(jsonResponse);
    }
}
