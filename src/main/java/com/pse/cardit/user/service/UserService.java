package com.pse.cardit.user.service;

import com.pse.cardit.security.auth.RegisterRequest;
import com.pse.cardit.security.config.JwtService;
import com.pse.cardit.user.model.User;
import com.pse.cardit.user.model.UserType;
import com.pse.cardit.user.repository.IUserRepository;
import com.pse.cardit.user.service.controll.DeleteRequest;
import com.pse.cardit.user.service.request.ChangeCardSkinRequest;
import com.pse.cardit.user.service.request.ChangeDiceSkinRequest;
import com.pse.cardit.user.service.request.ChangePasswordRequest;
import com.pse.cardit.user.service.request.ChangeProfilePictureRequest;
import com.pse.cardit.user.service.request.ChangeUsernameRequest;
import com.pse.cardit.user.service.response.ChangeCardSkinResponse;
import com.pse.cardit.user.service.response.ChangeDiceSkinResponse;
import com.pse.cardit.user.service.response.ChangeProfilePictureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.pse.cardit.user.model.User.MISSING_FEATURE_ERROR;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private static final String USER_NOT_FOUND = "Error, the user specified by '%s' could not be found!";
    private static final String USER_TAKEN = "Error, the username '%s' has already been given to another user";
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public User getUser(long userId) {
        if (!this.contains(userId)) {
            throw new IllegalArgumentException(String.format(USER_NOT_FOUND, "ID:" + userId));
        }
        Optional<User> userOptional = userRepository.findUserByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new AssertionError("Should be present since they are contained!");
        }
        return userOptional.get();
    }

    @Override
    public User getUser(String username) {
        if (!this.contains(username)) {
            throw new IllegalArgumentException(String.format(USER_NOT_FOUND, "NAME:" + username));
        }
        for (User u : userRepository.findUsersByUsername(username)) {
            if (u.getUserType() != UserType.BOT) {
                return u;
            }
        }
        throw new AssertionError("User is contained but not found?! This must be an significant error!");

    }

    @Override
    public User addUser(RegisterRequest request) {
        if (request.getPassword().isEmpty()) {
            addUser(request.getUsername());
        }
        if (this.contains(request.getUsername())) {
            throw new IllegalArgumentException(String.format(USER_TAKEN, request.getUsername()));
        }
        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public User addUser(String username) {
        if (this.contains(username)) {
            throw new IllegalArgumentException(String.format(USER_TAKEN, username));
        }
        User user = new User(username);
        userRepository.save(user);
        return user;
    }

    @Override
    public User addBot(String botName) {
        User user = new User(botName);
        user.setUserType(UserType.BOT);
        userRepository.save(user);
        return user;
    }

    @Override
    public void deleteUser(DeleteRequest request) {
        User user = this.getUser(request.getAuthToken());
        deleteUser(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public ChangeProfilePictureResponse changeProfilePicture(ChangeProfilePictureRequest request, String authToken) {
        throw new UnsupportedOperationException(String.format(MISSING_FEATURE_ERROR, "Shop"));
    }

    @Override
    public void changeUsername(ChangeUsernameRequest request, String authToken) {
        User user = userRepository.findUserByUserId(getUserFromToken(authToken).getUserId()).orElseThrow();
        user.setUsername(request.getNewUsername());
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request, String authToken) {
        User user = userRepository.findUserByUserId(getUserFromToken(authToken).getUserId()).orElseThrow();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public ChangeDiceSkinResponse changeDiceSkin(ChangeDiceSkinRequest request, String authToken) {
        throw new UnsupportedOperationException(String.format(MISSING_FEATURE_ERROR, "Shop"));
    }

    @Override
    public ChangeCardSkinResponse changeCardSkin(ChangeCardSkinRequest request, String authToken) {
        throw new UnsupportedOperationException(String.format(MISSING_FEATURE_ERROR, "Shop"));
    }

    @Override
    public User getUserFromToken(String authToken) {
        return this.getUser(jwtService.extractUserId(authToken));
    }

    private boolean contains(long userIdentifier) {
        return this.userRepository.findUserByUserId(userIdentifier).isPresent();
    }

    private boolean contains(String username) {
        for (User user : this.userRepository.findUsersByUsername(username)) {
            if (user.getUserType() != UserType.BOT) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is used for testing purposes exclusively. It will reset the user repository to an empty state again.
     */
    //TODO: Is there some way to ensure that its not used elsewhere?
    public void resetRepo() {
        this.userRepository.deleteAll();
    }

    @Override
    public String toString() {
        return "UserService{"
                + "userRepository=" + this.userRepository.findAll()
                + '}';
    }
}
