package com.stormpath.shiro.samples.dropwizard.model;


/**
 * Simple model to support the <code>/profile</code> resource.
 */
@SuppressWarnings("unused")
public class Profile {

    private String givenName;
    private String surname;
    private String favoriteColor;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(String favoriteColor) {
        this.favoriteColor = favoriteColor;
    }
}
