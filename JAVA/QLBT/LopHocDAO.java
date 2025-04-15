package DAO;

import DAL.JDBC;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LopHocDAO {

    public List<String> getAllTenLop() throws SQLException {
        List<String> tenLops = new ArrayList<>();
        String sql = "SELECT ClassName FROM classes";
        try (Connection conn = JDBC.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tenLops.add(rs.getString("ClassName"));
            }
        }
        return tenLops;
    }

    public String getMaLopTheoTen(String tenLop) throws SQLException {
        String maLop = null;
        String sql = "SELECT ClassID FROM classes WHERE ClassName = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tenLop);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    maLop = rs.getString("ClassID");
                }
            }
        }
        return maLop;
    }
}