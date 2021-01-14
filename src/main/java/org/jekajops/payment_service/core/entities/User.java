package org.jekajops.payment_service.core.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jekajops.payment_service.core.context.Context;
import org.jekajops.payment_service.core.database.Database;

import java.sql.SQLException;
import java.util.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
public class User {
    private int id, userId, pranksAvailable;
    double balance;
    private String userName;
    private static final int PRANK_COST = Context.SETTINGS.PRANK_COST.getDATA();
    private Set<Role> roles;
    public User(int id, String userName, int userId, int pranksAvailable, double balance, Collection<Role> roles) {
        this(id, userName, userId, pranksAvailable, balance);
        this.roles.addAll(roles);
    }

    public User(int id, String userName, int userId, int pranksAvailable, double balance) {
        this.roles = new HashSet<>();
        this.roles.add(Role.USER);
        this.id = id;
        this.userId = userId;
        this.pranksAvailable = pranksAvailable;
        this.balance = balance;
        this.userName = userName;
    }

    public void updatePayment(double amount) {
        changeBalance(amount);
        try {
            new Database().updateUserBalance(userId, balance);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean buyPrank() {
        return buy(0, 1, PRANK_COST);
    }

    public boolean buyPrivilegePrank() {
        int cost = Context.SETTINGS.DISCOUNT_PRANK_COST.getDATA();
        int pranks = cost > 0 ? 1 : 0;
        return buy(pranks-1, pranks, cost);
    }

    public boolean buy(int lowLimit, int pranks, int prankCost) {
        if (pranksAvailable > lowLimit && balance >= prankCost) {
            changeBalance(-prankCost);
            try {
                new Database().updateUserBalance(userId, balance);
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    private void changePranksAvailable(int pranksAvailable) {
        this.pranksAvailable += pranksAvailable;
    }

    private void changeBalance(double amount) {
        this.balance += amount;
    }

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = new HashSet<>(roles);
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    public enum Role  {
        USER,
        ADMIN,
        PRIVILEGE,
        SUBSCRIBED
    }
}
