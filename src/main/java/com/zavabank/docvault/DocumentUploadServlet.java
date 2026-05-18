package com.zavabank.docvault;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.JSONObject;

@MultipartConfig
public class DocumentUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int customerId;
        try {
            customerId = Integer.parseInt(valueOrEmpty(request.getParameter("customerId")));
        } catch (NumberFormatException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"customerId is required.\"}");
            return;
        }

        String documentType = valueOrEmpty(request.getParameter("documentType"));
        if (documentType.length() == 0) {
            documentType = "General";
        }

        Part filePart;
        try {
            filePart = request.getPart("file");
        } catch (ServletException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"file part is required.\"}");
            return;
        }
        if (filePart == null || filePart.getSize() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"A non-empty file is required.\"}");
            return;
        }

        byte[] fileBytes;
        InputStream inputStream = null;
        try {
            inputStream = filePart.getInputStream();
            fileBytes = readBytes(inputStream);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet keys = null;
        try {
            connection = DocVaultConnectionFactory.openConnection();
            statement = connection.prepareStatement(
                "INSERT INTO Documents (CustomerID, DocumentType, FileName, ContentType, DocumentData, UploadedDate) " +
                    "VALUES (?, ?, ?, ?, ?, GETDATE())",
                Statement.RETURN_GENERATED_KEYS
            );
            statement.setInt(1, customerId);
            statement.setString(2, documentType);
            statement.setString(3, safeFileName(filePart.getSubmittedFileName()));
            statement.setString(4, filePart.getContentType());
            statement.setBytes(5, fileBytes);
            statement.executeUpdate();

            long documentId = 0;
            keys = statement.getGeneratedKeys();
            if (keys.next()) {
                documentId = keys.getLong(1);
            }

            JSONObject result = new JSONObject();
            result.put("status", "UPLOADED");
            result.put("documentId", documentId);
            result.put("customerId", customerId);
            result.put("documentType", documentType);
            result.put("fileName", safeFileName(filePart.getSubmittedFileName()));
            response.getWriter().write(result.toString());
        } catch (SQLException exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"ERROR\",\"message\":\"Unable to save document.\"}");
        } finally {
            closeQuietly(keys);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read = inputStream.read(buffer);
        while (read != -1) {
            outputStream.write(buffer, 0, read);
            read = inputStream.read(buffer);
        }
        return outputStream.toByteArray();
    }

    private String safeFileName(String original) {
        String fileName = valueOrEmpty(original);
        if (fileName.length() == 0) {
            return "upload.bin";
        }
        return fileName.replace("\\", "_").replace("/", "_");
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
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
