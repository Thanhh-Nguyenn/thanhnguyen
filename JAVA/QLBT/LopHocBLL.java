package BLL;

import DAO.LopHocDAO;
import java.sql.SQLException;
import java.util.List;

public class LopHocBLL {
    private final LopHocDAO lopHocDAO;

    public LopHocBLL() {
        this.lopHocDAO = new LopHocDAO();
    }

    public List<String> getAllTenLop() throws SQLException {
        return lopHocDAO.getAllTenLop();
    }
}