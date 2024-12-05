package med.voll.api.infra.security;

import med.voll.api.domain.usuarios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositorio para encontrar usuarios por su login

    // Método que carga el usuario por su login
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Buscamos el usuario por su login
        return usuarioRepository.findByLogin(login);
    }
}