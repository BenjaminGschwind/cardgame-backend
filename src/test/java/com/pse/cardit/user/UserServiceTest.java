package com.pse.cardit.user;

import com.pse.cardit.security.auth.RegisterRequest;
import com.pse.cardit.user.model.IUser;
import com.pse.cardit.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService service;

    @BeforeEach
    public void setUp() {
        service.resetRepo();
    }

    @AfterEach
    public void tearDown() {
        service.resetRepo();
    }

    @ParameterizedTest
    @MethodSource("getLegalUsers")
    void testGetUserFromId(String name) {
        assertDoesNotThrow(() -> service.addUser(name), "Should add user, cause name " + name + "is legal");
        IUser user = service.getUser(name);
        System.out.println(service);
        assertDoesNotThrow(() -> service.getUser(user.getUserId()), "Should find user, he was just added...");
        System.out.println(user);
    }


    @ParameterizedTest
    @MethodSource("getLegalUsers")
    void testGetUserFromName(String name) {
        assertDoesNotThrow(() -> service.addUser(name), "Should add user, cause name " + name + " is legal");
        assertDoesNotThrow(() -> service.getUser(name), "Should find user, he was just added...");
    }

    @Test
    void testAddUser() {
        for (Pair<String, Boolean> p : getTestUsers()) {
            boolean expected = p.getSecond();
            String name = p.getFirst();
            if (expected) {
                assertDoesNotThrow(() -> service.addUser(name), "Should add user, cause name " + name + " is legal");
            }
            else {
                assertThrows(IllegalArgumentException.class, () -> service.addUser(name), "Should not add user, "
                        + "cause name " + name + " is illegal");
            }
        }
    }

    @Test
    void testAddUserWithPassword() {
        for (Pair<String, Boolean> p : getTestUsers()) {
            boolean expected = p.getSecond();
            String name = p.getFirst();
            RegisterRequest req = new RegisterRequest(name, "secure_password");
            if (expected) {
                assertDoesNotThrow(() -> service.addUser(req),
                        "Should add user, cause name " + name + " is legal");
            }
            else {
                assertThrows(IllegalArgumentException.class, () -> service.addUser(req),
                        "Should not add user, cause name " + name + " is illegal");
            }
        }
    }

    @Test
    void testToString() {
        for (String name : getLegalUsers()) {
            service.addUser(name);
            assertTrue(service.toString().contains(name), "Should contain the user that was just added");
        }
        System.out.println(service.toString());
        service.resetRepo();
        System.out.println(service.toString());
        service.addUser("name");
        System.out.println(service.toString());
    }

    private static List<Pair<String, Boolean>> getTestUsers() {
        return List.of(
                new Pair<>("Hans", true),
                new Pair<>("Hans ", true),
                new Pair<>("Hans", false),
                new Pair<>("", true)
        );
    }

    private static List<String> getLegalUsers() {
        return List.of(
                "Hans", "Dieter", "K1LL3R", "A_B", "|@0ß9#++2", "", "         ", "oiädh09qe+1209u3alkcjo9ou902kylcxnl",
                "\uD83C\uDF40", "Hans  "
        );
    }

    @Data
    @AllArgsConstructor
    private static class Pair<T,K> {
        private T first;
        private K second;
    }
}