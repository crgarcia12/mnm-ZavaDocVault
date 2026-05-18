package com.zavabank.docvault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class DocVaultBootstrapServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DocVaultConnectionFactory.openConnection();
            statement = connection.prepareStatement(
                "IF OBJECT_ID('Documents', 'U') IS NULL " +
                    "CREATE TABLE Documents (" +
                    "DocumentID BIGINT IDENTITY(1,1) PRIMARY KEY, " +
                    "CustomerID INT NOT NULL, " +
                    "DocumentType NVARCHAR(80) NOT NULL, " +
                    "FileName NVARCHAR(255) NOT NULL, " +
                    "ContentType NVARCHAR(150) NULL, " +
                    "DocumentData VARBINARY(MAX) NOT NULL, " +
                    "UploadedDate DATETIME NOT NULL DEFAULT GETDATE()" +
                    ");"
            );
            statement.execute();
        } catch (SQLException exception) {
            throw new ServletException("DocVault bootstrap failed.", exception);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
