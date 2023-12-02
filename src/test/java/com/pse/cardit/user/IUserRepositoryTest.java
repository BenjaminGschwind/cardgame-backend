package com.pse.cardit.user;

import com.pse.cardit.user.model.User;
import com.pse.cardit.user.repository.IUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IUserRepositoryTest {
    @Autowired
    private IUserRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("getNames")
    void findUserByUsername(String name) {
        repository.save(new User(name));
        List<User> users = repository.findUsersByUsername(name);
        assertEquals(1, users.size(), "I am resetting, why should there be more or less then?");
        System.out.println(users.get(0));
    }

    @ParameterizedTest
    @MethodSource("getNames")
    void findUserByIdentifier(String name) {
        repository.save(new User(name));
        List<User> users = repository.findUsersByUsername(name);
        long id = users.get(0).getUserId();
        assertDoesNotThrow(() -> repository.getReferenceById(id), "Should find user, he was just added");
        assertDoesNotThrow(() -> repository.findUserByUserId(id), "Should find user, he was just added");
        System.out.println(users.get(0));
    }

    private static List<String> getNames() {
        return List.of("Hans", "Dieter", "Amogus", "K1LL3R");
    }
}