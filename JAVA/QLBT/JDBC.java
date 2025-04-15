package DAL;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {
    private static final String URL = "jdbc:mysql://localhost:3306/ql_thitracnghiem";
    private static final String USER = "root";
    private static final String PASSWORD = "Thanh@1810";

    public static Connection getJDBCConnection() throws SQLException {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/ql_thitracnghiem","root", "Thanh@1810");
    } catch (ClassNotFoundException e) {
        throw new SQLException("Không tìm thấy JDBC Driver", e);
    }
}

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection conn = getJDBCConnection();

        if (conn != null) {
            System.out.println("Kết nối thành công!");
            closeConnection(conn);
        } else {
            System.out.println("Kết nối thất bại!");
        }
    }

    public static Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet.");   }
}