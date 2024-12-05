package med.voll.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.api.domain.usuarios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService; // Inyectamos el servicio que maneja la validación y generación de tokens

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositorio para buscar el usuario por su login

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Obtenemos el header Authorization que contiene el token
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null) {
            // Eliminamos el prefijo "Bearer " para obtener solo el token
            String token = authHeader.replace("Bearer ", "");

            // Validamos el token y obtenemos el username asociado
            String username = tokenService.validarToken(token);

            if (username != null) {
                // Si el token es válido, obtenemos el usuario desde la base de datos
                var userDetails = usuarioRepository.findByLogin(username);

                // Creamos un objeto de autenticación con el usuario autenticado
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No necesitamos la contraseña aquí
                        userDetails.getAuthorities() // Establecemos las autoridades del usuario
                );

                // Establecemos la autenticación en el contexto de seguridad para que esté disponible durante el ciclo de vida de la petición
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuamos con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}