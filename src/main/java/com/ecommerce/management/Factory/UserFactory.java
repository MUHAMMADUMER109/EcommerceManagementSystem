package com.ecommerce.management.Factory;

import com.ecommerce.management.Model.UserModel;

// FACTORY PATTERN


// Abstract class
abstract class BaseUser {
    public abstract String getRole();
    public abstract String getPermissions();
}

class AdminUser extends BaseUser {
    private final UserModel user;

    public AdminUser(UserModel user) {
        this.user = user;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getPermissions() {
        return "Can manage products, view all orders, apply discounts, delete orders";
    }

    public UserModel getUser() {
        return user;
    }
}

// Customer user
class CustomerUser extends BaseUser {
    private final UserModel user;

    public CustomerUser(UserModel user) {
        this.user = user;
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    @Override
    public String getPermissions() {
        return "Can browse products, place orders, view order history";
    }

    public UserModel getUser() {
        return user;
    }
}

// Factory class
public class UserFactory {

    public static UserModel createUser(String name, String email,
                                       String password, String role) {
        UserModel user = new UserModel();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        // if role == "ADMIN" -> AdminUser
        // if role == "CUSTOMER" -> CustomerUser
        switch (role.toUpperCase()) {
            case "ADMIN":
                user.setRole("ADMIN");
                AdminUser adminUser = new AdminUser(user);
                System.out.println("Factory created: " + adminUser.getRole());
                System.out.println("Permissions: "    + adminUser.getPermissions());
                break;

            case "CUSTOMER":
            default:
                user.setRole("CUSTOMER");
                CustomerUser customerUser = new CustomerUser(user);
                System.out.println("Factory created: " + customerUser.getRole());
                System.out.println("Permissions: "    + customerUser.getPermissions());
                break;
        }

        return user;
    }

    // Helper methods
    public static UserModel createCustomer(String name, String email, String password) {
        return createUser(name, email, password, "CUSTOMER");
    }

    public static UserModel createAdmin(String name, String email, String password) {
        return createUser(name, email, password, "ADMIN");
    }
}