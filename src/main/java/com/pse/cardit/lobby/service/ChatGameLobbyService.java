package com.pse.cardit.lobby.service;

import com.pse.cardit.chat.service.IChatService;
import com.pse.cardit.game.service.GameType;
import com.pse.cardit.game.service.IGameService;
import com.pse.cardit.security.config.JwtService;
import com.pse.cardit.user.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class ChatGameLobbyService extends GameLobbyService {
    private final IChatService chatService;

    public ChatGameLobbyService(IUserService userService, IGameService gameService, JwtService jwtService,
                                IChatService chatService) {
        super(userService, gameService, jwtService);
        this.chatService = chatService;
    }

    @Override
    public String createLobby(String token, GameType gameType) {
        String lobbyCode = super.createLobby(token, gameType);
        chatService.createChatroom(lobbyCode);
        return lobbyCode;
    }
}
