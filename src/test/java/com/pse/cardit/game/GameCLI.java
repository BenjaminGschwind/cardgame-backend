package com.pse.cardit.game;

import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.model.player.IPlayer;
import com.pse.cardit.game.model.player.Player;
import com.pse.cardit.game.service.executor.MauMauExecutor;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameCLI {
    public static void main(String[] args) {
        List<APlayer> players = List.of(new Player(0), new Player(1));
        MauMauExecutor executor = new MauMauExecutor("", players, new ArrayList<>());
        Scanner scanner = new Scanner(System.in);
        String interaction;
        InteractResponse response;
        while(executor.getWinners().isEmpty()) {
            IPlayer player = executor.getActivePlayer();
            System.out.println("> " + player.getUserID() + "'s turn.");
            System.out.println("> " + executor.interact(player, "getHand").getResponse());
            do {
                interaction = scanner.nextLine();
                if (interaction.equals("state")) {
                    System.out.println(executor.getGameState());
                }
                response = executor.interact(player, interaction);
            } while (!response.getStatus().is2xxSuccessful());
            System.out.println(response.getResponse());
        }
    }
}
