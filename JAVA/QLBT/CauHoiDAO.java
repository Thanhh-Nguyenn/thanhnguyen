package DAO;

import DAL.JDBC;
import Models.CauHoi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CauHoiDAO {
    public List<CauHoi> getAllCauHoi() throws SQLException {
        List<CauHoi> cauHois = new ArrayList<>();
        String sql = "SELECT QuestionID, QuestionText FROM questionbank";
        try (Connection conn = JDBC.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CauHoi cauHoi = new CauHoi();
                cauHoi.setQuestionID(rs.getInt("QuestionID"));
                cauHoi.setQuestionText(rs.getString("QuestionText"));
                cauHois.add(cauHoi);
            }
        }
        return cauHois;
    }

}