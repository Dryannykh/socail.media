package test.socail.media.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import test.socail.media.config.jwt.JwtUtils;
import test.socail.media.db.model.User;
import test.socail.media.db.repository.UserRepository;
import test.socail.media.error.EntityNotFoundError;
import test.socail.media.error.ValidationDataError;
import test.socail.media.services.AuthService;
import test.social.media.controller.AuthApi;
import test.social.media.dto.JwtResponse;
import test.social.media.dto.LoginRequest;
import test.social.media.dto.RegisterRequest;


@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Override
    public ResponseEntity<JwtResponse> authenticateUser(LoginRequest loginRequest) {
        User user;
        if(userRepository.findByUsername(loginRequest.getLogin()).isPresent()) {
            user = userRepository.findByUsername(loginRequest.getLogin()).get();
        }
        else if(userRepository.findByEmail(loginRequest.getLogin()).isPresent()) {
            user = userRepository.findByEmail(loginRequest.getLogin()).get();
        }
        else {
            throw new EntityNotFoundError("User with login " + loginRequest.getLogin() + " does not exist");
        }
        if(!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ValidationDataError("Invalid credentials specified");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(jwt);

        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> registerUser(RegisterRequest registerRequest) {
        registerRequest.setPassword(encoder.encode(registerRequest.getPassword()));
        authService.registerUser(registerRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
