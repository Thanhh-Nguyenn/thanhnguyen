package com.mycompany.quanlybaithi;

import BLL.BaiThiBLL;
import BLL.LopHocBLL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaoBaiThi extends JFrame {

    private final JTextField examNameField;
    private final JComboBox<String> classComboBox;
    private final JSpinner examDateField;
    private final JComboBox<String> examTypeComboBox;
    private final JSpinner examTimeField;
    private final JTextArea questionTextArea;
    private final DefaultListModel<String> listModel;
    private final JList<String> questionList;

    public TaoBaiThi() {
        setTitle("Tạo bài thi mới");
        setSize(600, 400);
        setLayout(new GridLayout(8, 2)); 
        
        examNameField = new JTextField();
        classComboBox = new JComboBox<>();
        examDateField = new JSpinner(new SpinnerDateModel());
        examTypeComboBox = new JComboBox<>(new String[]{"Kiểm tra 15 phút", "Kiểm tra 45 phút", "Thi học kì 1", "Thi học kì 2"});
        examTimeField = new JSpinner(new SpinnerDateModel());
        listModel = new DefaultListModel<>();
        questionList = new JList<>(listModel);
        questionTextArea = new JTextArea(); 

       
        loadClasses();

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
        add(new JScrollPane(questionTextArea)); 

        JButton addQuestionButton = new JButton("Thêm câu hỏi");
        addQuestionButton.addActionListener(e -> addQuestionToList());
        add(addQuestionButton);

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

    TaoBaiThi(QuanLyBaiThi aThis) {
        throw new UnsupportedOperationException("Not supported yet.");   }

    private void loadClasses() {
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ql_thitracnghiem", "root", "Thanh@1810")) {
                String query = "SELECT ClassName FROM classes";
                try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        classComboBox.addItem(resultSet.getString("ClassName"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu lớp học: " + e.getMessage());
        }
    }

    private void addQuestionToList() {
        String questionText = questionTextArea.getText().trim();
        if (!questionText.isEmpty()) {
            listModel.addElement(questionText);
            questionTextArea.setText(""); 
        }
    }

    private void luuBaiThi() {
        String examName = examNameField.getText();
        String className = (String) classComboBox.getSelectedItem();
        Date examDate = (Date) examDateField.getValue();
        String examType = (String) examTypeComboBox.getSelectedItem();
        Date examTime = (Date) examTimeField.getValue();

        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ql_thitracnghiem", "root", "Thanh@1810")) {
             
                if (examName.isEmpty() || className == null || examDate == null || examType == null || examTime == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bài thi.");
                    return;
                }
     
                String classIdQuery = "SELECT ClassID FROM classes WHERE ClassName = ?";
                String classId;
                try (PreparedStatement classIdStatement = connection.prepareStatement(classIdQuery)) {
                    classIdStatement.setString(1, className);
                    try (ResultSet classIdResultSet = classIdStatement.executeQuery()) {
                        if (classIdResultSet.next()) {
                            classId = classIdResultSet.getString("ClassID");
                        } else {
                            JOptionPane.showMessageDialog(this, "Không tìm thấy lớp học.");
                            return;
                        }
                    }
                }

                
                String examQuery = "INSERT INTO exams (ExamName, ClassID, ExamDate, ExamType, ExamTime) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement examStatement = connection.prepareStatement(examQuery)) {
                    examStatement.setString(1, examName);
                    examStatement.setString(2, classId);
                    examStatement.setDate(3, new java.sql.Date(examDate.getTime()));
                    examStatement.setString(4, examType);
                    examStatement.setTime(5, new java.sql.Time(examTime.getTime()));
                    examStatement.executeUpdate();
                }

                int examId;
                try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()")) {
                    resultSet.next();
                    examId = resultSet.getInt(1);
                }

                for (int i = 0; i < listModel.size(); i++) {
                    String questionText = listModel.getElementAt(i);
                    
                    String questionQuery = "INSERT INTO questionbank (QuestionText, QuestionType) VALUES (?, ?)"; 
                    try (PreparedStatement questionStatement = connection.prepareStatement(questionQuery, Statement.RETURN_GENERATED_KEYS)) {
                        questionStatement.setString(1, questionText);
                        questionStatement.setString(2, "Grammar"); 
                        questionStatement.executeUpdate();

                        
                        try (ResultSet generatedKeys = questionStatement.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int questionId = generatedKeys.getInt(1);
                                
                                String examQuestionQuery = "INSERT INTO examquestions (ExamID, QuestionID) VALUES (?, ?)";
                                try (PreparedStatement examQuestionStatement = connection.prepareStatement(examQuestionQuery)) {
                                    examQuestionStatement.setInt(1, examId);
                                    examQuestionStatement.setInt(2, questionId);
                                    examQuestionStatement.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Bài thi đã được lưu.");
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu bài thi: " + e.getMessage());
        }
    }
}