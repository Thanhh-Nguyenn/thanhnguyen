package DAO;

import JDBC.DatabaseConnection;
import Model.Exam;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {
    public static List<Exam> selectAll() {
        var list = new ArrayList<Exam>();
        var query = "SELECT ExamID, ClassID, ExamName, ExamDate, ExamTime, ExamType FROM exams";
        try (var statement = DatabaseConnection.getConnection().createStatement()) {
            var resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                list.add(
                        new Exam(
                                resultSet.getInt("ExamID"),
                                resultSet.getString("ClassID"),
                                resultSet.getString("ExamName"),
                                resultSet.getDate("ExamDate"),
                                resultSet.getTime("ExamTime"),
                                resultSet.getString("ExamType")
                        )
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static Exam selectByID(int examID) {
        var query = "SELECT ExamID, ClassID, ExamName, ExamDate, ExamTime, ExamType FROM exams WHERE ExamID = ?";
        try (var ps = DatabaseConnection.getConnection().prepareStatement(query)) {
            ps.setInt(1, examID);
            var resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return new Exam(
                        resultSet.getInt("ExamID"),
                        resultSet.getString("ClassID"),
                        resultSet.getString("ExamName"),
                        resultSet.getDate("ExamDate"),
                        resultSet.getTime("ExamTime"),
                        resultSet.getString("ExamType")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static boolean insert(Exam exam) {
        var query = "INSERT INTO exams (ClassID, ExamName, ExamDate, ExamTime, ExamType) VALUES (?, ?, ?, ?, ?)";
        try (var ps = DatabaseConnection.getConnection().prepareStatement(query)) {
            ps.setString(1, exam.getClassID());
            ps.setString(2, exam.getExamName());
            ps.setDate(3, exam.getExamDate());
            ps.setTime(4, exam.getExamTime());
            ps.setString(5, exam.getExamType());
            var count = ps.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean update(Exam exam) {
        var query = "UPDATE exams SET ClassID = ?, ExamName = ?, ExamDate = ?, ExamTime = ?, ExamType = ? WHERE ExamID = ?";
        try (var ps = DatabaseConnection.getConnection().prepareStatement(query)) {
            ps.setString(1, exam.getClassID());
            ps.setString(2, exam.getExamName());
            ps.setDate(3, exam.getExamDate());
            ps.setTime(4, exam.getExamTime());
            ps.setString(5, exam.getExamType());
            ps.setInt(6, exam.getExamID());
            var count = ps.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean delete(int examID) {
        var query = "DELETE FROM exams WHERE ExamID = ?";
        try (var ps = DatabaseConnection.getConnection().prepareStatement(query)) {
            ps.setInt(1, examID);
            var count = ps.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        List<Exam> exams = ExamDAO.selectAll();
        exams.forEach(System.out::println);

        Exam exam = ExamDAO.selectByID(1);
        System.out.println("Exam by ID: " + exam);
    }
}
