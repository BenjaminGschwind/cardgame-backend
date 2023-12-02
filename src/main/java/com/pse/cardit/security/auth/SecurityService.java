package com.pse.cardit.security.auth;

import com.pse.cardit.security.config.JwtService;
import com.pse.cardit.user.model.User;
import com.pse.cardit.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService implements ISecurityService {
    private final IUserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) throws AuthenticationException {
        if (request.getPassword().length() == 0) {
            return new AuthenticationResponse(jwtService.generateToken(
                    this.userService.addUser(request.getUsername())));

        }
        else {
            this.userService.addUser(request);
        }
        LoginRequest loginRequest = LoginRequest.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        return login(loginRequest);
    }

    @Override
    public AuthenticationResponse login(LoginRequest request) throws AuthenticationException {
        User user;

        try {
            user = this.userService.getUser(request.getUsername());
        }
        catch (UsernameNotFoundException e) {
            throw new AuthenticationException("User not found") {
            };
        }

        long userId = user.getUserId();
        String password = request.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userId, password));
        String jwtToken = this.jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
