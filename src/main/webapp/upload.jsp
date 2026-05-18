<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ZavaDocVault Upload</title>
</head>
<body>
<h1>ZavaDocVault - Upload Document</h1>
<form action="<%= request.getContextPath() %>/api/documents/upload" method="post" enctype="multipart/form-data">
    <label for="customerId">Customer ID:</label>
    <input type="text" id="customerId" name="customerId" required />
    <br/>
    <label for="documentType">Document Type:</label>
    <input type="text" id="documentType" name="documentType" value="General" />
    <br/>
    <label for="file">Document File:</label>
    <input type="file" id="file" name="file" required />
    <br/>
    <button type="submit">Upload</button>
</form>
</body>
</html>
