package site.psi.ads3.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import site.psi.ads3.repository.UserRepository;
import site.psi.ads3.security.JWTService;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JWTService jwtService;

    public String login(String login, String password) {
        UserDetails user = loadUserByUsername(login);
        if (user == null) throw new RuntimeException("Usuário não encontrado");
        if (!encoder.matches(password, user.getPassword())) throw new RuntimeException("Senha inválida");
        return jwtService.generateToken(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username);
    }
}