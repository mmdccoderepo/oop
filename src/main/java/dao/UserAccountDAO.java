package dao;

import java.util.Map;

public interface UserAccountDAO {
    Map<String, String> getAll();

    boolean create(String username, String password); 
}
