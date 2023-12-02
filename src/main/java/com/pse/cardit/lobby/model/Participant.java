package com.pse.cardit.lobby.model;

import com.pse.cardit.user.model.IUser;

import java.util.Objects;

public class Participant {
    private final IUser user;
    private Rank rank;
    private ReadyState readyState;

    public Participant(IUser user) {
        this.user = user;
        this.rank = Rank.NONE;
        this.readyState = ReadyState.NOT_READY;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public int getImageId() {
        return user.getImageId();
    }

    public IUser getUser() {
        return user;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public ReadyState getReadyState() {
        return readyState;
    }

    public boolean setReadyState(ReadyState readyState) {
        if (this.readyState == readyState) {
            return false;
        }
        this.readyState = readyState;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public String toString() {
        return "Participant{"
                + "user=" + user.getUsername()
                + ", rank=" + rank
                + ", readyState=" + readyState
                + '}';
    }
}
