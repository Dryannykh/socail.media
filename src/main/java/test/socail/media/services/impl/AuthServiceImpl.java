package test.socail.media.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import test.socail.media.db.model.User;
import test.socail.media.db.repository.UserRepository;
import test.socail.media.error.EntityNotFoundError;
import test.socail.media.error.ValidationDataError;
import test.socail.media.services.AuthService;
import test.social.media.dto.RegisterRequest;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository repository;

    @Override
    public void registerUser(RegisterRequest registerRequest) {
        User user = new User();
        String emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(!Pattern.compile(emailPattern).matcher(registerRequest.getEmail()).matches()) {
            throw new ValidationDataError("Invalid email");
        }
        if (repository.existsByUsername(registerRequest.getUsername())) {
            throw new ValidationDataError("A user with this username already exists");
        }
        if (repository.existsByEmail(registerRequest.getEmail())) {
            throw new ValidationDataError("A user with this email already exists");
        }
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        repository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user;
        String emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(Pattern.compile(emailPattern).matcher(username).matches()) {
            user = repository.findByEmail(username).orElseThrow(() -> new EntityNotFoundError("User with email " + username + " not found"));
        }
        else {
            user = repository.findByUsername(username).orElseThrow(() -> new EntityNotFoundError("User with username " + username + " not found"));
        }
        return UserDetailsImpl.build(user);
    }
}
