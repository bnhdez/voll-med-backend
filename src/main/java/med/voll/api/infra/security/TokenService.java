package med.voll.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import med.voll.api.domain.usuarios.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.secret}")
    private String apiSecret; // La clave secreta para firmar el token, extraída de la configuración

    // Método para generar el token JWT
    public String generarToken(Usuario usuario) {
        try {
            // Algoritmo HMAC con la clave secreta
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);

            // Creación del JWT con el usuario como sujeto y otros datos como la expiración
            return JWT.create()
                    .withIssuer("voll med") // Emisor del token
                    .withSubject(usuario.getLogin()) // Login del usuario como sujeto
                    .withClaim("id", usuario.getId()) // Incluimos el ID del usuario como claim
                    .withExpiresAt(generarFechaExpiracion()) // Fecha de expiración del token
                    .sign(algorithm); // Firmamos el token
        } catch (JWTCreationException exception){
            // Si ocurre un error al crear el token, lanzamos una excepción
            throw new RuntimeException();
        }
    }

    // Genera la fecha de expiración del token (2 horas a partir del momento actual)
    private Instant generarFechaExpiracion(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-05:00"));
    }

    // Método para validar un token JWT
    public String validarToken(String token) {
        if (token == null){
            throw new RuntimeException("Token no puede ser nulo");
        }
        DecodedJWT verifier = null;
        try {
            // Utilizamos el mismo algoritmo de firma para verificar el token
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            verifier = JWT.require(algorithm)
                    .withIssuer("voll med")
                    .build()
                    .verify(token);
        } catch (Exception e) {
            // Si el token no es válido, devolvemos null
            return null;
        }

        // Si no encontramos el sujeto del token, devolvemos un error
        if (verifier.getSubject() == null){
            throw new RuntimeException("Token inválido");
        }

        // Si todo es correcto, devolvemos el login del usuario (sujeto del token)
        return verifier.getSubject();
    }

}