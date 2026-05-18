package com.zavabank.docvault;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class CustomerDocumentsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.trim().length() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Expected /api/documents/customer/{customerId}.\"}");
            return;
        }

        String[] segments = pathInfo.split("/");
        if (segments.length != 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Expected /api/documents/customer/{customerId}.\"}");
            return;
        }

        int customerId;
        try {
            customerId = Integer.parseInt(segments[1]);
        } catch (NumberFormatException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"customerId must be numeric.\"}");
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DocVaultConnectionFactory.openConnection();
            statement = connection.prepareStatement(
                "SELECT DocumentID, DocumentType, FileName, ContentType, UploadedDate " +
                    "FROM Documents WHERE CustomerID = ? ORDER BY UploadedDate DESC, DocumentID DESC"
            );
            statement.setInt(1, customerId);
            resultSet = statement.executeQuery();

            JSONArray rows = new JSONArray();
            while (resultSet.next()) {
                JSONObject row = new JSONObject();
                row.put("documentId", resultSet.getLong("DocumentID"));
                row.put("customerId", customerId);
                row.put("documentType", resultSet.getString("DocumentType"));
                row.put("fileName", resultSet.getString("FileName"));
                row.put("contentType", resultSet.getString("ContentType"));
                row.put("uploadedDate", resultSet.getTimestamp("UploadedDate"));
                rows.put(row);
            }

            JSONObject responseBody = new JSONObject();
            responseBody.put("customerId", customerId);
            responseBody.put("count", rows.length());
            responseBody.put("documents", rows);
            response.getWriter().write(responseBody.toString());
        } catch (SQLException exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Unable to list documents.\"}");
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }
}
