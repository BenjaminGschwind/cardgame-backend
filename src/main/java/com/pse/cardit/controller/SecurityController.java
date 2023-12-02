package com.pse.cardit.controller;

import com.pse.cardit.security.auth.AuthenticationResponse;
import com.pse.cardit.security.auth.ISecurityService;
import com.pse.cardit.security.auth.LoginRequest;
import com.pse.cardit.security.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Security controller.
 */
@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final ISecurityService service;

    /**
     * Will attempt to register a new user with user details from the {@link RegisterRequest}. If successful the user
     * is stored in the data base with an encrypted password.
     *
     * @param request the {@link RegisterRequest}
     * @return <strong>200</strong> the {@link AuthenticationResponse}
     *          <strong>400</strong> No token available
     *          <strong>409</strong> The username is already taken
     */
    @PostMapping(value = "/security/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse res;
        try {
            res = service.register(request);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        if (res.getToken().isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * Will attempt to get an json web token for authentication for the provided user details.
     * TODO this should not be a post request?! Nothing is changed as far as I know.
     *
     * @param request the request
     * @return  <strong>200</strong> the {@link AuthenticationResponse}
     *          <strong>404</strong> User not found
     */
    @PostMapping("/security/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse res = service.login(request);
        if (!res.getToken().isBlank()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
