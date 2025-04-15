package com.mycompany.quanlybaithi;

import BLL.BaiThiBLL;
import BLL.LopHocBLL;
import Models.BaiThi;
import Models.CauHoi;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Date;
import java.util.List;


public class SuaBaiThi extends JFrame {

    private final int examId;
    private final JTextField examNameField;
    private final JComboBox<String> classComboBox;
    private final JSpinner examDateField;
    private final JComboBox<String> examTypeComboBox;
    private final JSpinner examTimeField;
    private final JList<String> questionList;
    private final DefaultListModel<String> listModel;

    public SuaBaiThi(int examId) {
        this.examId = examId;
        setTitle("Sửa bài thi");
        setSize(600, 400);
        setLayout(new GridLayout(7, 2));

        examNameField = new JTextField();
        classComboBox = new JComboBox<>();
        examDateField = new JSpinner(new SpinnerDateModel());
        examTypeComboBox = new JComboBox<>(new String[]{"Kiểm tra 15 phút", "Kiểm tra 45 phút", "Thi học kì 1", "Thi học kì 2"});
        examTimeField = new JSpinner(new SpinnerDateModel());
        listModel = new DefaultListModel<>();
        questionList = new JList<>(listModel);

        loadClasses();
        loadExamData();
        loadQuestions();

        add(new JLabel("Tên bài thi:"));
        add(examNameField);
        add(new JLabel("Lớp:"));
        add(classComboBox);
        add(new JLabel("Ngày thi:"));
        add(examDateField);
        add(new JLabel("Loại bài thi:"));
        add(examTypeComboBox);
        add(new JLabel("Thời gian:"));
        add(examTimeField);
        add(new JLabel("Câu hỏi:"));
        add(new JScrollPane(questionList));

        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> luuBaiThi());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel);

        setVisible(true);
    }

    SuaBaiThi(QuanLyBaiThi aThis, BaiThi baiThi) {
        throw new UnsupportedOperationException("Not supported yet.");    }

    private void loadClasses() {
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ql_thitracnghiem","root", "Thanh@1810")) {
                String query = "SELECT ClassName FROM classes";
                try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
                    
                    while (resultSet.next()) {
                        classComboBox.addItem(resultSet.getString("ClassName"));
                    }
                    
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadExamData() {
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ql_thitracnghiem","root", "Thanh@1810")) {
                String query = "SELECT ExamName, ClassID, ExamDate, ExamType, ExamTime FROM exams WHERE ExamID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, examId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            examNameField.setText(resultSet.getString("ExamName"));
                            classComboBox.setSelectedItem(resultSet.getString("ClassID"));
                            examDateField.setValue(resultSet.getDate("ExamDate"));
                            examTypeComboBox.setSelectedItem(resultSet.getString("ExamType"));
                            examTimeField.setValue(resultSet.getTime("ExamTime"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadQuestions() {
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ql_thitracnghiem","root", "Thanh@1810")) {
                String query = "SELECT QuestionText FROM questionbank JOIN examquestions ON questionbank.QuestionID = examquestions.QuestionID WHERE examquestions.ExamID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, examId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            listModel.addElement(resultSet.getString("QuestionText"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void luuBaiThi() {
        String examName = examNameField.getText();
        String className = (String) classComboBox.getSelectedItem();
        java.util.Date examDate = (java.util.Date) examDateField.getValue();
        String examType = (String) examTypeComboBox.getSelectedItem();
        java.util.Date examTime = (java.util.Date) examTimeField.getValue();

        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ql_thitracnghiem","root", "Thanh@1810")) {
                String query = "UPDATE exams SET ExamName = ?, ClassID = ?, ExamDate = ?, ExamType = ?, ExamTime = ? WHERE ExamID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, examName);
                    preparedStatement.setString(2, className);
                    preparedStatement.setDate(3, new java.sql.Date(examDate.getTime()));
                    preparedStatement.setString(4, examType);
                    preparedStatement.setTime(5, new java.sql.Time(examTime.getTime()));
                    preparedStatement.setInt(6, examId);
                    preparedStatement.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, "Bài thi đã được cập nhật.");
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật bài thi.");
        }
    }
}