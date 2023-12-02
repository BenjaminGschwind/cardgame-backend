package com.pse.cardit.user.model;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Die Schnittstelle IUser erbt von der Schnittstelle UserDetails aus der Spring-Security-Dependency. Die Schnittstelle
 * wird vom User implementiert, um sicherzustellen, dass man zusätzlich zu den Methoden aus dem UserDetails zusätzlich
 * die userId abfragen kann.
 */
public interface IUser extends UserDetails {
    /**
     * Methode um die userId des Users zu erfragen.
     *
     * @return the user id
     */
    long getUserId();

    /**
     * Methode um die imageId des Users zu erfragen.
     *
     * @return the image id
     */
    int getImageId();

}
