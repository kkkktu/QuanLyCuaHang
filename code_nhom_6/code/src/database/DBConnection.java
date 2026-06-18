package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:sqlserver://localhost;"
            + "instanceName=SQLEXPRESS;"
            + "databaseName=BiluxuryDB;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "loginTimeout=5";

    private static final String USER = "sa";
    private static final String PASSWORD = "123";

    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "Chưa thêm JDBC Driver SQL Server. Hãy copy mssql-jdbc-xx.x.x.jre11.jar hoặc jre17.jar vào thư mục lib và add vào Referenced Libraries.",
                    e);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Kết nối SQL Server thất bại. Kiểm tra SQL Server đang chạy, TCP/IP port 1433, database BiluxuryDB, user sa và password 123.",
                    e);
        }
    }
}