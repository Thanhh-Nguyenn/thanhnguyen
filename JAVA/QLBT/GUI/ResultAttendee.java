package GUI;

import Model.User;

import javax.swing.*;import javax.swing.JFrame;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultAttendee extends JFrame {
    private final User loginUser;
    private JTextField textfieldFindViewResultAttendee;
    private JTable tableViewResultAttendee;
    private JButton buttonBackViewResutlAttendee;
    private JLabel labelFindViewResultAttendee;
    private JPanel panelViewResultAttendee;
    private DefaultTableModel columnModel;
    private DefaultTableModel rowModel;
    private TableRowSorter<TableModel> rowSorter;

    public ResultAttendee(User loginUser) {
        this.loginUser = loginUser;
        this.setTitle("Xem điểm thi");
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panelViewResultAttendee = new JPanel(new BorderLayout(10, 10));
        this.setContentPane(panelViewResultAttendee);
        initComponents();
        addActionEvent();
        fillDataToTable();
        makeTableSearchable();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void initComponents() {
        labelFindViewResultAttendee = new JLabel("Tìm kiếm (Mã bài thi):");
        textfieldFindViewResultAttendee = new JTextField(20);
        buttonBackViewResutlAttendee = new JButton("Quay lại");
        tableViewResultAttendee = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableViewResultAttendee);

        tableViewResultAttendee.setDefaultEditor(Object.class, null);
        tableViewResultAttendee.getTableHeader().setReorderingAllowed(false);
        columnModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Mã bài thi", "Điểm thi"}
        );
        tableViewResultAttendee.setModel(columnModel);
        rowModel = (DefaultTableModel) tableViewResultAttendee.getModel();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(labelFindViewResultAttendee);
        topPanel.add(textfieldFindViewResultAttendee);

        panelViewResultAttendee.add(topPanel, BorderLayout.NORTH);
        panelViewResultAttendee.add(scrollPane, BorderLayout.CENTER);
        panelViewResultAttendee.add(buttonBackViewResutlAttendee, BorderLayout.SOUTH);
    }

    private void addActionEvent() {
        buttonBackViewResutlAttendee.addActionListener((ActionEvent event) -> {
            ResultAttendee.this.dispose();
            new MenuAttendee(loginUser);
        });
    }

    private void fillDataToTable() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String DB_URL = "jdbc:mysql://localhost:3306/ql_thitracnghiem?serverTimezone=Asia/Ho_Chi_Minh";
            String DB_USER = "root";
            String DB_PASSWORD = "Thanh@1810";
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String query = "SELECT ExamID, Score FROM studentsubmissions WHERE StudentID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, loginUser.getUserID());
            System.out.println("Tìm kiếm điểm cho StudentID: " + loginUser.getUserID());
            resultSet = preparedStatement.executeQuery();

            rowModel.setRowCount(0);
            int rowCount = 0;
            while (resultSet.next()) {
                int examID = resultSet.getInt("ExamID");
                double score = resultSet.getDouble("Score");
                System.out.println("Đã tìm thấy: ExamID=" + examID + ", Score=" + score);
                rowModel.addRow(new Object[]{examID, score});
                rowCount++;
            }
            System.out.println("Số lượng hàng đã thêm vào bảng: " + rowCount);

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy JDBC Driver: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi truy vấn điểm thi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void makeTableSearchable() {
        rowSorter = new TableRowSorter<>(rowModel);
        rowSorter.setRowFilter(new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
                String searchText = textfieldFindViewResultAttendee.getText().trim();
                if (searchText.isEmpty()) {
                    return true;
                }
                TableModel model = entry.getModel();
                Integer row = entry.getIdentifier();
                String examID = model.getValueAt(row, 0).toString();
                return examID.toLowerCase().contains(searchText.toLowerCase());
            }
        });
        tableViewResultAttendee.setRowSorter(rowSorter);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            User attendee = null;
            int targetStudentID = 2; // **Quan trọng: Đặt UserID của học sinh bạn muốn xem (2 hoặc 3)**

            try {
                String DB_URL = "jdbc:mysql://localhost:3306/ql_thitracnghiem?serverTimezone=Asia/Ho_Chi_Minh";
                String DB_USER = "root";
                String DB_PASSWORD = "Thanh@1810";
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String query = "SELECT UserID, Name, Email, Password, Role, ClassID FROM users WHERE Role = 'Student' AND UserID = ? LIMIT 1";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, targetStudentID);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    attendee = new User(
                            resultSet.getInt("UserID"),
                            resultSet.getString("Name"),
                            resultSet.getString("Email"),
                            resultSet.getString("Password"),
                            resultSet.getString("Role"),
                            resultSet.getString("ClassID")
                    );
                    System.out.println("Thông tin attendee được lấy: " + attendee);
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy tài khoản học sinh với UserID = " + targetStudentID, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy JDBC Driver: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi kết nối hoặc truy vấn cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            } finally {
                try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
                try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
                try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
            }

            if (attendee != null) {
                new ResultAttendee(attendee);
            }
        });
    }

    private void createUIComponents() {
    }
}
