package com.pse.cardit.game.model.player;

import com.pse.cardit.game.model.holdable.IHoldable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Aplayer is an abstract class that implements the IPlayer interface.
 * The class provides the basic attributes and methods every class that inherits by APlayer needs to have.
 */
public abstract class APlayer implements IPlayer {

    private final long userID;
    private final List<IHoldable> inventory;
    private int inGameCounter;

    /**
     * Instantiates a new APlayer object that is identified by the given id.
     *
     * @param userID The identifier.
     */
    protected APlayer(long userID) {
        this.userID = userID;
        inventory = new ArrayList<>();
    }

    @Override
    public void putInInventory(IHoldable item) {
        inventory.add(item);
    }

    @Override
    public IHoldable takeFromInventory(IHoldable itemToGet) {
        IHoldable item = inventory.get(inventory.indexOf(itemToGet));
        inventory.remove(item);
        return item;
    }

    @Override
    public int getInvSize() {
        return inventory.size();
    }

    @Override
    public void changeCounter(int value) {
        inGameCounter += value;
    }

    @Override
    public void clearInventory() {
        inventory.clear();
    }


    // Default Getter & Setter

    @Override
    public long getUserID() {
        return userID;
    }

    @Override
    public List<IHoldable> getInventory() {
        return inventory;
    }

    @Override
    public int getInGameCounter() {
        return inGameCounter;
    }

    @Override
    public String toString() {
        return "Player " + userID + " {"
            + ", inventory=" + inventory
            + ", inGameCounter=" + inGameCounter
            + '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        APlayer player = (APlayer) o;
        return userID == player.userID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }
}
