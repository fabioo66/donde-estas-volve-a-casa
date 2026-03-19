package ttps.spring.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ttps.spring.security.JwtUtils;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtener el token del header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Validar el token
                String email = jwtUtils.extractEmail(token);

                if (email != null && !jwtUtils.isTokenExpired(token)) {
                    Long userId = jwtUtils.extractUserId(token);
                    String rol = jwtUtils.extractRol(token);

                    request.setAttribute("userId", userId);
                    request.setAttribute("userEmail", email);
                    request.setAttribute("userRol", rol);
                }
            } catch (Exception e) {
                // Token inválido o expirado
                System.err.println("Error al validar token: " + e.getMessage());
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // No aplicar el filtro a las rutas de login, registro y swagger
        return path.startsWith("/usuarios/login") ||
               path.startsWith("/usuarios/registro") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs");
    }
}
