package dao;

import model.TaxBracket;

import java.util.List;

public interface TaxDAO {
    List<TaxBracket> getAll();
}
