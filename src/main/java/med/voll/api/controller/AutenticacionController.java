package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.domain.usuarios.DatosAutenticacionUsuario;
import med.voll.api.domain.usuarios.Usuario;
import med.voll.api.infra.security.DatosJWTToken;
import med.voll.api.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager; // Encargado de autenticar las credenciales de usuario

    @Autowired
    private TokenService tokenService; // Servicio encargado de la generación de JWT

    // Endpoint para la autenticación de usuarios y generación de JWT
    @PostMapping
    public ResponseEntity autenticarUsuario(@RequestBody @Valid DatosAutenticacionUsuario datosAutenticacionUsuario){
        // Creamos un objeto de autenticación con las credenciales enviadas por el usuario
        Authentication authToken = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(),
                datosAutenticacionUsuario.clave());

        // Autenticamos al usuario
        var usuarioAutenticado = authenticationManager.authenticate(authToken);

        // Generamos el JWT para el usuario autenticado
        var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());

        // Devolvemos el token en la respuesta
        return ResponseEntity.ok(new DatosJWTToken(JWTtoken));
    }

}
