package service;

import dao.UserAccountDAO;

import java.util.Map;

public class AuthenticationService {
    private final UserAccountDAO userAccountDAO;

    public AuthenticationService(UserAccountDAO userAccountDAO) {
        this.userAccountDAO = userAccountDAO;
    }

    public int authenticate(String username, String password) {
        if (username == null || password == null) {
            return -1;
        }
        Map<String, String> accounts = userAccountDAO.getAll();
        String stored = accounts.get(username.trim());
        if (stored != null && stored.equals(password.trim())) {
            try {
                return Integer.parseInt(stored);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }
}

