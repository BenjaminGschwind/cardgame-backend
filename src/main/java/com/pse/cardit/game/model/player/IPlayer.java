package com.pse.cardit.game.model.player;

import com.pse.cardit.game.model.holdable.IHoldable;

import java.util.List;

/**
 * IPlayer is an interface that provides methods to interact with objects implementing this interface.
 */
public interface IPlayer {
    /**
     * Put an item from type IHoldable in the inventory.
     *
     * @param item The item.
     */
    void putInInventory(IHoldable item);

    /**
     * Take the item from type IHoldable out of the inventory.
     *
     * @param itemToGet The item to take.
     * @return The item.
     */
    IHoldable takeFromInventory(IHoldable itemToGet);

    /**
     * Gets inventory size.
     *
     * @return The inventory size.
     */
    int getInvSize();

    /**
     * Change the counter by the given value.
     *
     * @param value The value.
     */
    void changeCounter(int value);


    /**
     * Clear inventory.
     */
    void clearInventory();


    // Default Getter & Setter


    /**
     * Gets user id.
     *
     * @return The user id.
     */
    long getUserID();


    /**
     * Gets inventory.
     *
     * @return The inventory.
     */
    List<IHoldable> getInventory();

    /**
     * Gets inGame counter.
     *
     * @return The inGame counter.
     */
    int getInGameCounter();


}
