package com.sope.domain.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;

import com.sope.domain.CommonTableProperties;

@Entity
public class User extends CommonTableProperties {

    @Column(nullable = false)
    @ColumnDefault("''")
    private String firstname = "";

    @Column(nullable = false)
    @ColumnDefault("''")
    private String lastname = "";

    @Column(nullable = false)
    @ColumnDefault("''")
    private String username;

    // jos antaa syntymäajan niin voidaan antaa syntymäpäivinä lahjarahaa tai
    // jotain
    private Date birthDate;

    @Column(nullable = false)
    private String email = "";

    @Column(nullable = false)
    private String password; // crypted
    @Transient
    private String uncrypted;

    @ColumnDefault("''")
    private String firebaseToken;

    @Embedded
    private UserPermissions permission = new UserPermissions();

    @ColumnDefault("0")
    private int coins = 0;

    private AccountType accountType = AccountType.BASIC;

    // Pidetään kirjaa siitä, mitä ohjelmia käyttäjä on katsonut. Saadaan
    // jonkinlaista tilastoa.
    // Sisäiseen käyttöön.
//    @OneToMany
//    private List<TvShow> history;

    private Date lastUsageDate;

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserPermissions getPermission() {
        return permission;
    }

    public void setPermission(UserPermissions permission) {
        this.permission = permission;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getLastUsageDate() {
        return lastUsageDate;
    }

    public void setLastUsageDate(Date lastUsageDate) {
        this.lastUsageDate = lastUsageDate;
    }

    public boolean hasInvalidToken() {
        return getFirebaseToken() == null || getFirebaseToken() == "";
    }

    public String getUncrypted() {
        return uncrypted;
    }

    public void setUncrypted(String uncrypted) {
        this.uncrypted = uncrypted;
    }

}
