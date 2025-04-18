package GUI;

import Model.User;

import javax.swing.*;
import java.awt.*;
import javax.swing.JFrame; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuAttendee extends JFrame {
    private final User loginUser;
    private JButton buttonGoToExamViewMenuAttendee;
    private JButton buttonViewResultsViewMenuAttendee;
    private JButton buttonLogoutViewMenuAttendee;
    private JPanel panelViewMenuAttendee;

    public MenuAttendee(User user) {
        this.loginUser = user;
        initComponents();
        addActionEvent();
        this.setTitle("Menu Người dự thi");
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelViewMenuAttendee);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User attendee = null;

        try {
            String DB_URL = "jdbc:mysql://localhost:3306/ql_thitracnghiem";
            String DB_USER = "root";
            String DB_PASSWORD = "Thanh@1810";

            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String query = "SELECT UserID, Name, Email, Password, Role, ClassID FROM users WHERE Role = 'Student' LIMIT 1";
            preparedStatement = connection.prepareStatement(query);
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
            // Đóng kết nối, preparedStatement và resultSet trong khối finally
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        if (attendee != null) {
            User finalAttendee = attendee;
            EventQueue.invokeLater(() -> new MenuAttendee(finalAttendee));
        } else {
            JOptionPane.showMessageDialog(null, "Không tìm thấy tài khoản học sinh", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        buttonGoToExamViewMenuAttendee = new JButton("Vào Kỳ Thi");
        buttonViewResultsViewMenuAttendee = new JButton("Xem Kết Quả");
        buttonLogoutViewMenuAttendee = new JButton("Đăng Xuất");
        panelViewMenuAttendee = new JPanel(new GridLayout(3, 1, 10, 10));
        panelViewMenuAttendee.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelViewMenuAttendee.add(buttonGoToExamViewMenuAttendee);
        panelViewMenuAttendee.add(buttonViewResultsViewMenuAttendee);
        panelViewMenuAttendee.add(buttonLogoutViewMenuAttendee);
    }

    private void addActionEvent() {
        buttonGoToExamViewMenuAttendee.addActionListener(event -> {
            this.dispose();
            new RoomAttendee(loginUser); 
        });
        buttonViewResultsViewMenuAttendee.addActionListener(event -> {
            this.dispose();
            try {
                new ResultAttendee(loginUser); 
            } catch (SQLException ex) {
                Logger.getLogger(MenuAttendee.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        buttonLogoutViewMenuAttendee.addActionListener(event -> {
            this.dispose();
            new Login();
        });
    }

    private void createUIComponents() {
    }
}
