package com.pse.cardit.security;

import com.pse.cardit.security.auth.LoginRequest;
import com.pse.cardit.security.auth.RegisterRequest;
import com.pse.cardit.security.auth.SecurityService;
import com.pse.cardit.user.model.UserType;
import com.pse.cardit.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityServiceTest {

    @Autowired
    SecurityService service;

    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        userService.resetRepo();
    }
    @AfterEach
    void tearDown() {
        userService.resetRepo();
    }

    @ParameterizedTest
    @MethodSource("getValidCredentials")
    void register(String name, String password) {
        assertDoesNotThrow(() -> service.register(new RegisterRequest(name, password)).getToken(),
                "Should be able to register. Did you check if JWT stuff has correct scope set?");
        if (password.isEmpty()) {
            assertEquals(UserType.GUEST, userService.getUser(name).getUserType(),
                    "Should be GUEST user without password");
            return;
        }
        assertEquals(UserType.REGISTERED, userService.getUser(name).getUserType(),
                "Should be REGISTERED user with password");
        assertNotEquals(password, userService.getUser(name).getPassword(), "Passwords need to be encrypted!");
    }

    @ParameterizedTest
    @MethodSource("getValidCredentials")
    void login(String name, String password) {
        if (password.isEmpty()) {
            System.out.println("Consider removing inputs with that case?");
            return;
        }
        String token = service.register(new RegisterRequest(name,password)).getToken();
        assertEquals(token, service.login(new LoginRequest(name, password)).getToken(),
                "If this fails I need to consider that this assertion is actually false. Might be just " +
                        "implementation though");
    }

    private static List<Arguments> getValidCredentials() {
        return List.of(
                Arguments.of("Hans", "aASDsdVolkherXCAS4123"),
                Arguments.of("", " "),
                Arguments.of("\uD83D\uDC3B\uD83C\uDF3B", "\uD83D\uDE03\uD83D\uDC81"),
                Arguments.of("Guest", "")
        );
    }
}