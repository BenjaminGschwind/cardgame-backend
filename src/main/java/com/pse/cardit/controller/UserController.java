package com.pse.cardit.controller;

import com.pse.cardit.user.service.IUserService;
import com.pse.cardit.user.service.request.ChangeCardSkinRequest;
import com.pse.cardit.user.service.request.ChangeDiceSkinRequest;
import com.pse.cardit.user.service.request.ChangePasswordRequest;
import com.pse.cardit.user.service.request.ChangeProfilePictureRequest;
import com.pse.cardit.user.service.request.ChangeUsernameRequest;
import com.pse.cardit.user.service.response.ChangeCardSkinResponse;
import com.pse.cardit.user.service.response.ChangeDiceSkinResponse;
import com.pse.cardit.user.service.response.ChangePasswordResponse;
import com.pse.cardit.user.service.response.ChangeProfilePictureResponse;
import com.pse.cardit.user.service.response.ChangeUsernameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final IUserService service;


    @PutMapping("/user/change/picture")
    public ResponseEntity<ChangeProfilePictureResponse> changeProfilePicture(
            @RequestBody ChangeProfilePictureRequest request, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @PutMapping("/user/change/password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request,
                                                                 @RequestHeader("Authorization") String token) {
        service.changePassword(request,token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/user/change/username")
    public ResponseEntity<ChangeUsernameResponse> changeUsername(@RequestBody ChangeUsernameRequest request,
                                                                 @RequestHeader("Authorization") String token) {
        service.changeUsername(request, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/user/change/skin/dice")
    public ResponseEntity<ChangeDiceSkinResponse> changeDiceSkin(@RequestBody ChangeDiceSkinRequest request,
                                                                 @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @PutMapping("/user/change/skin/card")
    public ResponseEntity<ChangeCardSkinResponse> changeCardSkin(@RequestBody ChangeCardSkinRequest request,
                                                                 @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
