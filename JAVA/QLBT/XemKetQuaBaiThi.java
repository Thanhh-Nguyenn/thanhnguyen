package com.mycompany.quanlybaithi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class XemKetQuaBaiThi extends JFrame {

    private final int examId;
    private final JTable resultTable;
    private final DefaultTableModel tableModel;

    public XemKetQuaBaiThi(int examId) {
        this.examId = examId;
        setTitle("Xem kết quả bài thi");
        setSize(800, 600);
        setLayout(new BorderLayout());

        String[] columnNames = {"SubmissionID", "Tên học sinh", "Thời gian bắt đầu", "Thời gian kết thúc", "Điểm", "Số câu đúng", "Tổng số câu"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);

        add(scrollPane, BorderLayout.CENTER);

        loadData();

        setVisible(true);
    }

    private void loadData() {
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ql_thitracnghiem","root", "Thanh@1810")) {
                String query = "SELECT SubmissionID, Name, StartTime, EndTime, Score, CorrectAnswers, TotalQuestions FROM studentsubmissions JOIN users ON studentsubmissions.StudentID = users.UserID WHERE ExamID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, examId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Object[] row = {resultSet.getInt("SubmissionID"), resultSet.getString("Name"), resultSet.getTimestamp("StartTime"), resultSet.getTimestamp("EndTime"), resultSet.getDouble("Score"), resultSet.getInt("CorrectAnswers"), resultSet.getInt("TotalQuestions")};
                            tableModel.addRow(row);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
