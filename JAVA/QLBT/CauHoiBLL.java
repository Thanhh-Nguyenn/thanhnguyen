
package BLL;

import DAO.CauHoiDAO;
import Models.CauHoi;
import java.sql.SQLException;
import java.util.List;

public class CauHoiBLL {
    private final CauHoiDAO cauHoiDAO;

    public CauHoiBLL() {
        this.cauHoiDAO = new CauHoiDAO();
    }

    public List<CauHoi> getAllCauHoi() throws SQLException {
        return cauHoiDAO.getAllCauHoi();
    }
}