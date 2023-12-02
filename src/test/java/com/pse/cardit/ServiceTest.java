package com.pse.cardit;

import com.pse.cardit.security.auth.ISecurityService;
import com.pse.cardit.security.auth.LoginRequest;
import com.pse.cardit.security.auth.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class ServiceTest {
    public static final String SUPER_USERNAME = "Tester";
    @Autowired
    ISecurityService securityService;
    protected String token;
    @BeforeEach
    protected void setUp() {
        token = getToken(SUPER_USERNAME, "super");
    }

    protected String getToken(String username, String password) {
        String token;
        try {
            token = securityService.login(new LoginRequest(username, password)).getToken();
        } catch (Exception e) {
            token = securityService.register(new RegisterRequest(username, password)).getToken();
        }
        return token;
    }
}
