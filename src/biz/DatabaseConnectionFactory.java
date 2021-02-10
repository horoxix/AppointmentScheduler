package biz;

import java.sql.*;

public class DatabaseConnectionFactory {
    public static final String className = "com.mysql.cj.jdbc.Driver";
    public static final String connectionUrl = "jdbc:mysql://wgudb.ucertify.com:3306/WJ06kZy";
    public static final String dbUserName = "U06kZy";
    public static final String dbPassword = "53688794939";

    private static DatabaseConnectionFactory connectionFactory = null;

    /**
     * Constructor, creates the Database Connection Factory
     */
    private DatabaseConnectionFactory() {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets connection from database connection factory
     * @return Connection object
     * @throws SQLException thrown if connection info invalid
     */
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        conn = DriverManager.getConnection(connectionUrl, dbUserName, dbPassword);
        return conn;
    }

    /**
     * Returns instance of database connection factory
     * @return DatabaseConnectionFactory instance object
     */
    public static DatabaseConnectionFactory getInstance() {
        if (connectionFactory == null) {
            connectionFactory = new DatabaseConnectionFactory();
        }
        return connectionFactory;
    }

    /**
     * Gets the next available primary key ID for table and column
     * @param tableColumn the columnName of the primary key id
     * @param tableName the tableName of the table
     * @return int of next available primary key
     * @throws SQLException thrown if invalid sql connection
     */
    public static int getNextAvailableId(String tableColumn, String tableName) throws SQLException{
        String query = "SELECT MAX(" + tableColumn + ") FROM " + tableName;
        Connection conn;
        conn = getInstance().getConnection();
        ResultSet resultSet = null;
        PreparedStatement ptmt = null;
        int id = 0;

        try {
            ptmt = conn.prepareStatement(query);
            resultSet = ptmt.executeQuery();
            while(resultSet.next()) {
                id = resultSet.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (ptmt != null)
                    ptmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return id;
    }
}
