package DAO;

import DAL.JDBC;
import Models.BaiThi;
import Models.CauHoi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaiThiDAO {

    public List<BaiThi> getAllBaiThi() throws SQLException {
       //System.out.println("BaiThiDAO.getAllBaiThi() started");
        List<BaiThi> baiThis = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
    
        try {
            conn = JDBC.getJDBCConnection();
            String sql = "SELECT ExamID, ExamName, ClassName, ExamDate, ExamType, ExamTime FROM BaiThi";
           // System.out.println("Executing SQL: " + sql);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                BaiThi baiThi = new BaiThi();
                baiThi.setExamID(rs.getInt("ExamID"));
                baiThi.setExamName(rs.getString("ExamName"));
                baiThi.setClassName(rs.getString("ClassName"));
                baiThi.setExamDate(rs.getDate("ExamDate"));
                baiThi.setExamType(rs.getString("ExamType"));
                baiThi.setExamTime(rs.getTime("ExamTime"));
                baiThis.add(baiThi);
            }
            System.out.println("BaiThiDAO.getAllBaiThi() finished, retrieved " + baiThis.size() + " records.");
        } catch (SQLException e) {
            System.err.println("loi sql trong baithidao(): " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            JDBC.closeConnection(conn); 
        }
        return baiThis;
    }

    public BaiThi getBaiThiById(int examId) throws SQLException {
        BaiThi baiThi = null;
        String sql = "SELECT ExamName, ClassID, ExamDate, ExamType, ExamTime FROM exams WHERE ExamID = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    baiThi = new BaiThi();
                    baiThi.setExamID(examId);
                    baiThi.setExamName(rs.getString("ExamName"));
                    baiThi.setClassId(rs.getString("ClassID"));
                    baiThi.setExamDate(rs.getDate("ExamDate"));
                    baiThi.setExamType(rs.getString("ExamType"));
                    baiThi.setExamTime(rs.getTime("ExamTime"));
                }
            }
        }
        return baiThi;
    }

    public void addBaiThi(BaiThi baiThi) throws SQLException {
        String sql = "INSERT INTO exams (ExamName, ClassID, ExamDate, ExamType, ExamTime) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, baiThi.getExamName());
            pstmt.setString(2, baiThi.getClassId());
            pstmt.setDate(3, new Date(baiThi.getExamDate().getTime()));
            pstmt.setString(4, baiThi.getExamType());
            pstmt.setTime(5, new Time(baiThi.getExamTime().getTime()));
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    baiThi.setExamID(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateBaiThi(BaiThi baiThi) throws SQLException {
        String sql = "UPDATE exams SET ExamName = ?, ClassID = ?, ExamDate = ?, ExamType = ?, ExamTime = ? WHERE ExamID = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, baiThi.getExamName());
            pstmt.setString(2, baiThi.getClassId());
            pstmt.setDate(3, new Date(baiThi.getExamDate().getTime()));
            pstmt.setString(4, baiThi.getExamType());
            pstmt.setTime(5, new Time(baiThi.getExamTime().getTime()));
            pstmt.setInt(6, baiThi.getExamID());
            pstmt.executeUpdate();
        }
    }

    public void deleteBaiThi(int examId) throws SQLException {
        String sql = "DELETE FROM exams WHERE ExamID = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            pstmt.executeUpdate();
        }
    }

    public List<CauHoi> getCauHoiByBaiThiId(int examId) throws SQLException {
        List<CauHoi> cauHois = new ArrayList<>();
        String sql = "SELECT q.QuestionID, q.QuestionText FROM questionbank q " +
                     "JOIN examquestions eq ON q.QuestionID = eq.QuestionID WHERE eq.ExamID = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CauHoi cauHoi = new CauHoi();
                    cauHoi.setQuestionID(rs.getInt("QuestionID"));
                    cauHoi.setQuestionText(rs.getString("QuestionText"));
                    cauHois.add(cauHoi);
                }
            }
        }
        return cauHois;
    }

    public void addCauHoiToBaiThi(int examId, int questionId) throws SQLException {
        String sql = "INSERT INTO examquestions (ExamID, QuestionID) VALUES (?, ?)";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            pstmt.setInt(2, questionId);
            pstmt.executeUpdate();
        }
    }

    public void addCauHoiToBaiThi(int examId, String questionText) throws SQLException {
        String insertQuestionSQL = "INSERT INTO questionbank (QuestionText, QuestionType) VALUES (?, ?)";
        String getQuestionIdSQL = "SELECT LAST_INSERT_ID()";
        String linkQuestionToExamSQL = "INSERT INTO examquestions (ExamID, QuestionID) VALUES (?, ?)";

        try (Connection conn = JDBC.getConnection()) {
            conn.setAutoCommit(false); 

            int questionId;
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuestionSQL, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, questionText);
                insertStmt.setString(2, "Grammar"); 
                insertStmt.executeUpdate();
                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        questionId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Không thể lấy ID câu hỏi sau khi thêm.");
                    }
                }
            }

            try (PreparedStatement linkStmt = conn.prepareStatement(linkQuestionToExamSQL)) {
                linkStmt.setInt(1, examId);
                linkStmt.setInt(2, questionId);
                linkStmt.executeUpdate();
            }

            conn.commit(); 
        } catch (SQLException e) {
            if (JDBC.getConnection() != null) {
                try {
                    JDBC.getConnection().rollback(); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            JDBC.getConnection().setAutoCommit(true); 
        }
    }
}
