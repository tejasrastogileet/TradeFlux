package com.tradeflux.portfolio_service.auth;



public class UserContextHolder {

    private static final ThreadLocal<AuthenticatedUser>
            currentUser = new ThreadLocal<>();

    public static AuthenticatedUser getCurrentUser() {
        return currentUser.get();
    }

    public static void setCurrentUser(
            AuthenticatedUser user
    ) {
        currentUser.set(user);
    }

    public static void clear() {
        currentUser.remove();
    }
}