package org.example.andensemesterproeve_thesemicolons.domain;

import org.example.andensemesterproeve_thesemicolons.domain.enums.Title_ENUM;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private Title_ENUM title;
    private List<Card> cards;
    private List<Deck> decks;

    public User() {    }

    public User(int id, String username, String email, Title_ENUM title) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.title = title;
    }

    public User(String username, Title_ENUM title) {
        this.username = username;
        this.title = title;
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Title_ENUM getTitle() {
        return title;
    }

    public void setTitle(Title_ENUM title) { this.title = title; }

    public List<Card> getCards() { return cards; }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Deck> getDecks() {
        return decks;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    public void validateValues() {
        if (username == null || username.isEmpty() || username.trim().isEmpty() ||
                username.contains("slettet") || username.contains("deleted") ||
                username.contains("anonym") || username.contains("anonymous")) {
            throw new IllegalArgumentException("Username " + username + " is not valid");
        }

        String emailRegexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern emailPattern = Pattern.compile(emailRegexPattern); //creates pattern
        Matcher emailMatcher = emailPattern.matcher(email); //returns a Matcher object
        boolean emailMatchFound = emailMatcher.find();

        if (email == null || email.isEmpty() || email.trim().isEmpty() || !emailMatchFound) {
            throw new IllegalArgumentException("Email " + email + " is not valid");
        }

        //password has to have at least 1 number, 1 uppercase and 1 lowercase letter, no whitespaces and at least 8 characters (max 24)
        String passwordRegexPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,24}$";
        Pattern passwordPattern = Pattern.compile(passwordRegexPattern); //creates pattern
        Matcher passwordMatcher = passwordPattern.matcher(password); //returns a Matcher object
        boolean passwordMatchFound = passwordMatcher.find();

        if (password == null || password.isEmpty() || password.trim().isEmpty() || !passwordMatchFound) {
            throw new IllegalArgumentException("Password is not valid");
        }
    }
}
