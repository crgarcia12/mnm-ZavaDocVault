package com.zavabank.docvault;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DocVaultConnectionFactory {
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("SQL Server JDBC driver not found", exception);
        }
    }

    private DocVaultConnectionFactory() {
    }

    public static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(
            DocVaultConfig.getDbUrl(),
            DocVaultConfig.getDbUser(),
            DocVaultConfig.getDbPassword()
        );
    }
}
