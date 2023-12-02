package com.pse.cardit.game.service.executor;

import com.pse.cardit.game.model.player.Bot;
import com.pse.cardit.game.model.player.IPlayer;
import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.GameType;
import com.pse.cardit.game.service.IGameAdapter;
import com.pse.cardit.game.service.command.ICommand;
import com.pse.cardit.game.service.controll.InteractResponse;
import com.pse.cardit.game.service.exceptions.UnsupportedCommandException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com.pse.cardit.game.service.command.AInGameCommand.KEYWORD_ARGS_SEPARATOR;

public abstract class AExecutor {
    protected static final String GAME_ENDED_ERROR = "Error - The game has already ended! Start a new round or quit!";
    protected static final String NOT_ACTIVE_ERROR = "Player %s is not active! Instead Player %s is.";
    private static final String UNKNOWN_COMMAND = "Error, %s is not a supported command";
    private static final String ILLEGAL_ARGS_ERROR = "Error, the command %s should look like '%s'. '%s' does nt match"
            + " this!";
    private final Collection<ICommand> commands;
    private final String identifier;

    protected AExecutor(String identifier) {
        this.commands = new HashSet<>();
        this.identifier = identifier;
    }


    /**
     * Die codierte Interaktion zwischen einem Spieler und dem vom Executor verwalteten Spiel Objekt. Der Rückgabe
     * String kann eine codierte Antwort enthalten, welche bei der Ausführung entsteht, etwa die Karte, welche gezogen
     * wurde.
     * Falls der Spieler nicht am Zug im Spiel des Executors ist, wird ein Fehler geworfen. Ebenso, falls die
     * Interaktion fehlerhaft bzw unbekannt codiert ist.
     * Note: Some games may require certain actions to be taken during another players turn. They need to overwrite
     * this method.
     *
     * @param player      the player
     * @param interaction the interaction
     * @return the string
     */
    public InteractResponse interact(IPlayer player, String interaction) {
        if (getAdapter().hasEnded()) {
            return new InteractResponse(GameStatus.GAME_ALREADY_ENDED, GAME_ENDED_ERROR);
        }
        Logger.getLogger(this.getClass().getName()).log(new LogRecord(Level.INFO,
                "Attempting " + interaction + " for " + player.getUserID()));
        if (!getAdapter().isPlayerActive(player)) {
            return new InteractResponse(GameStatus.FORBIDDEN, String.format(NOT_ACTIVE_ERROR, player.getUserID(),
                    getAdapter().getActivePlayer().getUserID()));
        }
        ICommand command;
        try {
            command = getMatchingCommand(interaction);
        }
        catch (UnsupportedCommandException e) {
            return new InteractResponse(GameStatus.BAD_REQUEST, e.getMessage());
        }
        return command.apply(splitArgs(interaction));
    }

    public String getGameState() {
        return getAdapter().getState();
    }

    public List<IPlayer> getPlayers() {
        return getAdapter().getPlayers();
    }

    /**
     * Gets winners of the game. This collection should only change exactly once when all winners have been
     * determined. If the winner is still unknown the collection should be empty.
     *
     * @return the winners
     */
    public Collection<IPlayer> getWinners() {
        return getAdapter().getWinners();
    }

    public IPlayer getActivePlayer() {
        return getAdapter().getActivePlayer();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public GameType getGameType() {
        return this.getAdapter().getGameType();
    }

    protected void addCommands(Collection<ICommand> commands) {
        this.commands.addAll(commands);
    }

    public String getBotInteraction() {
        if (getAdapter().hasEnded()) {
            return "";
        }
        if (getAdapter().getActivePlayer().getClass() != Bot.class) {
            return "";
        }
        return this.getAdapter().getNextAction();
    }

    protected ICommand getMatchingCommand(String interaction) {
        for (ICommand cmd : commands) {
            if (interaction.matches(cmd.getRegex())) {
                return cmd;
            }
        }
        for (ICommand cmd : commands) {
            if (interaction.startsWith(cmd.getPrefix())) {
                throw new UnsupportedCommandException(String.format(ILLEGAL_ARGS_ERROR, cmd.getPrefix(),
                        cmd.getRegex(), interaction));
            }
        }
        throw new UnsupportedCommandException(
                String.format(UNKNOWN_COMMAND, interaction.split(KEYWORD_ARGS_SEPARATOR)[0]));
    }

    protected List<String> splitArgs(String args) {
        List<String> arguments = new ArrayList<>(Arrays.asList(args.split(KEYWORD_ARGS_SEPARATOR)));
        arguments.remove(0);
        return arguments;
    }

    protected abstract IGameAdapter getAdapter();
}
