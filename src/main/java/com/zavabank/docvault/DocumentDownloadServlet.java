package com.zavabank.docvault;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocumentDownloadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.trim().length() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Expected /api/documents/{id}.\"}");
            return;
        }

        String[] segments = pathInfo.split("/");
        if (segments.length != 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Expected /api/documents/{id}.\"}");
            return;
        }

        long documentId;
        try {
            documentId = Long.parseLong(segments[1]);
        } catch (NumberFormatException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Document id must be numeric.\"}");
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        OutputStream outputStream = null;
        try {
            connection = DocVaultConnectionFactory.openConnection();
            statement = connection.prepareStatement(
                "SELECT FileName, ContentType, DocumentData FROM Documents WHERE DocumentID = ?"
            );
            statement.setLong(1, documentId);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"status\":\"NOT_FOUND\",\"message\":\"Document not found.\"}");
                return;
            }

            String fileName = resultSet.getString("FileName");
            String contentType = resultSet.getString("ContentType");
            byte[] fileData = resultSet.getBytes("DocumentData");

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(contentType == null || contentType.trim().length() == 0 ? "application/octet-stream" : contentType);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentLength(fileData.length);
            outputStream = response.getOutputStream();
            outputStream.write(fileData);
            outputStream.flush();
        } catch (SQLException exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Unable to load document.\"}");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
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
