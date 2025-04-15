package com.mycompany.quanlybaithi;

import BLL.BaiThiBLL;
import Models.BaiThi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public final class QuanLyBaiThi extends JFrame {

    private final JTable examTable;
    private final DefaultTableModel tableModel;
    private final BaiThiBLL baiThiBLL;

    public QuanLyBaiThi() {
        System.out.println("QuanLyBaiThi constructor started");
        setTitle("Quản lý bài thi");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        baiThiBLL = new BaiThiBLL();

        String[] columnNames = {"ExamID", "Tên bài thi", "Lớp", "Ngày thi", "Loại bài thi", "Thời gian"};
        tableModel = new DefaultTableModel(columnNames, 0);
        examTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(examTable);

        
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Tạo bài thi mới");
        JButton editButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton viewResultButton = new JButton("Xem kết quả");

        
        createButton.addActionListener(e -> new TaoBaiThi(this));
        editButton.addActionListener(e -> suaBaiThi());
        deleteButton.addActionListener(e -> xoaBaiThi());
        viewResultButton.addActionListener(e -> xemKetQuaBaiThi());

        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewResultButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        
        loadData();
        setVisible(true);
    }

     void loadData() {
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ql_thitracnghiem","root", "Thanh@1810")) {
                String query = "SELECT ExamID, ExamName, ClassName, ExamDate, ExamType, ExamTime FROM exams JOIN classes ON exams.ClassID = classes.ClassID";
                try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
                    
                    while (resultSet.next()) {
                        Object[] row = {resultSet.getInt("ExamID"), resultSet.getString("ExamName"), resultSet.getString("ClassName"), resultSet.getDate("ExamDate"), resultSet.getString("ExamType"), resultSet.getTime("ExamTime")};
                        tableModel.addRow(row);
                    }
                    
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void suaBaiThi() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow >= 0) {
            int examId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                BaiThi baiThi = baiThiBLL.getBaiThiById(examId);
                if (baiThi != null) {
                    SuaBaiThi suaBaiThi = new SuaBaiThi(this, baiThi);
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy bài thi với ID: " + examId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin bài thi: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài thi để sửa.");
        }
    }

    private void xoaBaiThi() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow >= 0) {
            int examId = (int) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa bài thi này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    baiThiBLL.deleteBaiThi(examId);
                    loadData(); 
                    JOptionPane.showMessageDialog(this, "Bài thi đã được xóa.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa bài thi: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài thi để xóa.");
        }
    }

    private void xemKetQuaBaiThi() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow >= 0) {
            int examId = (int) tableModel.getValueAt(selectedRow, 0);
            XemKetQuaBaiThi xemKetQuaBaiThi = new XemKetQuaBaiThi(examId);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài thi để xem kết quả.");
        }
    }

   public static void main(String[] args) throws ClassNotFoundException {
        SwingUtilities.invokeLater(() -> new QuanLyBaiThi().setVisible(true));
   }
}