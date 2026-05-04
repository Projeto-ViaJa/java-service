package database.model.dao;

import database.config.DB;
import database.model.dao.Impl.RegistroVooDaoJDBC;

public class DaoFactory {
    public static RegistroVooDao createRegistroVooDao () { return new RegistroVooDaoJDBC(DB.getConnection()); }

}
