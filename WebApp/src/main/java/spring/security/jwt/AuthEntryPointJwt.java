package spring.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LogManager.getLogger(AuthEntryPointJwt.class);
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
     logger.error(e.getMessage());
     httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
     httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

     final Map<String, Object> body = new HashMap<>();
     body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
     body.put("error", "Unauthorized");
     body.put("message", e.getMessage());
     body.put("path", httpServletRequest.getServletPath());

     final ObjectMapper mapper = new ObjectMapper();
     mapper.writeValue(httpServletResponse.getOutputStream(), body);
    }
}
