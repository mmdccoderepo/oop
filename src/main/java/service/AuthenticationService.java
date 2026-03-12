package service;

import dao.UserAccountDAO;

import java.util.Map;

public class AuthenticationService {
    private final UserAccountDAO userAccountDAO;

    public AuthenticationService(UserAccountDAO userAccountDAO) {
        this.userAccountDAO = userAccountDAO;
    }

    /**
     * Authenticates a user by username and password.
     * The CSV stores username -> employeeId (which is also the password).
     * Returns the employeeId if successful, or -1 if authentication fails.
     */
    public int authenticate(String username, String password) {
        if (username == null || password == null) {
            return -1;
        }
        Map<String, String> accounts = userAccountDAO.getAll();
        String storedEmployeeId = accounts.get(username.trim());
        if (storedEmployeeId != null && storedEmployeeId.trim().equals(password.trim())) {
            try {
                return Integer.parseInt(storedEmployeeId.trim());
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }
}
