# Security Assessment Report

**Generated:** 2026-05-28T23:09:16.0000000Z

## Summary

| Metric | Count |
|--------|-------|
| Total Findings | 10 |
| CVE Vulnerabilities | 3 |
| CWE Vulnerabilities | 7 |
| Total Rules Assessed | 59 |
| Rules Passed | 52 |

### By Severity

| Severity | Count |
|----------|-------|
| mandatory | 4 |
| optional | 3 |
| potential | 3 |

### By Category

| Category | Count |
|----------|-------|
| CVE | 3 |
| File & Path Security | 1 |
| Code Quality | 3 |
| Credentials & Secrets | 3 |

---

## CVE Findings (Dependency Vulnerabilities)

### CVE-2025-59250: JDBC Driver for SQL Server has improper input validation issue
- **Severity:** mandatory
- **Story Points:** 1
- **Files:** build.gradle:16

[CVE-2025-59250](https://github.com/advisories/GHSA-m494-w24q-6f7w): JDBC Driver for SQL Server has improper input validation issue

Severity: HIGH

Affected dependencies:
  - com.microsoft.sqlserver:mssql-jdbc:12.6.3.jre8 (declared at build.gradle:16)

Vulnerable version range: >= 12.6.0.jre11, < 12.6.5.jre11

Recommended fix:
  - Upgrade com.microsoft.sqlserver:mssql-jdbc to 12.6.5.jre8 or later

---

### CVE-2023-5072: Java: DoS Vulnerability in JSON-JAVA
- **Severity:** mandatory
- **Story Points:** 1
- **Files:** build.gradle:17

[CVE-2023-5072](https://github.com/advisories/GHSA-4jq9-2xhw-jpx7): Denial of Service vulnerability in JSON-Java (org.json)

Severity: HIGH

Affected dependencies:
  - org.json:json:20140107 (declared at build.gradle:17)

Vulnerable version range: <= 20230618

Recommended fix:
  - Upgrade org.json:json to 20231013 or later

---

### CVE-2022-45688: json stack overflow vulnerability
- **Severity:** mandatory
- **Story Points:** 1
- **Files:** build.gradle:17

[CVE-2022-45688](https://github.com/advisories/GHSA-3vqj-43w4-2q58): Stack overflow vulnerability in org.json

Severity: HIGH

Affected dependencies:
  - org.json:json:20140107 (declared at build.gradle:17)

Vulnerable version range: < 20230227

Recommended fix:
  - Upgrade org.json:json to 20230227 or later (20231013 recommended to also address CVE-2023-5072)

---

## CWE Findings (Code-Level Vulnerabilities)

### CWE-434: Unrestricted Upload of File with Dangerous Type
- **Category:** File & Path Security
- **Severity:** mandatory
- **Story Points:** 8
- **Files:** src/main/java/com/zavabank/docvault/DocumentUploadServlet.java

In DocumentUploadServlet.doPost(), the servlet accepts any file type with no content-type or file-extension allowlist validation. The file's content type is taken directly from the request part (filePart.getContentType()) without sanitization, and there is no check that restricts which file types may be uploaded. Dangerous files such as HTML, JavaScript, or executable payloads can be stored and later served back to users with their original content type, enabling drive-by download attacks or stored XSS.

---

### CWE-477: Use of Obsolete Function
- **Category:** Code Quality
- **Severity:** optional
- **Story Points:** 1
- **Files:** src/main/java/com/zavabank/docvault/DocVaultConnectionFactory.java

In DocVaultConnectionFactory's static initializer, Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver") is used to manually register the JDBC driver. This pattern has been obsolete since JDBC 4.0 (Java SE 6): drivers are automatically discovered and registered via the Java SPI mechanism (META-INF/services). Since the project targets Java 8, this manual registration is unnecessary and constitutes use of an obsolete pattern.

---

### CWE-772: Missing Release of Resource after Effective Lifetime
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 3
- **Files:** src/main/java/com/zavabank/docvault/DocVaultConfig.java

In DocVaultConfig's static initializer, the InputStream obtained from getResourceAsStream("doc-vault.properties") is closed only in the happy path (after PROPERTIES.load() succeeds). If PROPERTIES.load(inputStream) throws an IOException, the catch block ignores the exception and the InputStream is never closed, causing a resource leak. The close() call should be in a finally block or the code should use a try-with-resources statement.

---

### CWE-775: Missing Release of File Descriptor or Handle after Effective Lifetime
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 3
- **Files:** src/main/java/com/zavabank/docvault/DocVaultConfig.java

Same issue as CWE-772: In DocVaultConfig's static initializer, the InputStream (backed by a file descriptor for the classpath resource doc-vault.properties) is not closed in the exception path. If PROPERTIES.load(inputStream) throws an IOException, the underlying file handle is leaked because inputStream.close() is skipped and the catch block silently ignores the error.

---

### CWE-259: Use of Hard-coded Password
- **Category:** Credentials & Secrets
- **Severity:** optional
- **Story Points:** 5
- **Files:** src/main/resources/doc-vault.properties

The classpath resource doc-vault.properties (bundled inside the WAR artifact) contains the literal database password. DocVaultConfig.read() prefers environment variables but falls back to these property values, meaning the hard-coded password is the default credential used whenever the DB_PASSWORD environment variable is not set. Shipping a production password in the source repository and the deployable artifact is a critical exposure.

---

### CWE-778: Insufficient Logging
- **Category:** Credentials & Secrets
- **Severity:** potential
- **Story Points:** 3
- **Files:** src/main/java/com/zavabank/docvault/CustomerDocumentsServlet.java, src/main/java/com/zavabank/docvault/DocumentUploadServlet.java, src/main/java/com/zavabank/docvault/DocumentDownloadServlet.java, src/main/java/com/zavabank/docvault/DocVaultConfig.java

No logging framework is used anywhere in the application. Security-critical events are silently discarded: (1) CustomerDocumentsServlet.doGet() catches SQLException and returns a 500 response with no logging. (2) DocumentUploadServlet.doPost() catches SQLException on document storage failure with no logging. (3) DocumentDownloadServlet.doGet() catches SQLException on document retrieval with no logging. (4) DocVaultConfig's static initializer catches IOException and ignores it entirely (catch (IOException ignored)), meaning a properties-loading failure leaves the app running with null credentials and no audit trail. An attacker exploiting any of these paths would leave no server-side trace.

---

### CWE-798: Use of Hard-coded Credentials
- **Category:** Credentials & Secrets
- **Severity:** optional
- **Story Points:** 5
- **Files:** src/main/resources/doc-vault.properties

doc-vault.properties contains both a hard-coded database username (db.user=sa) and password. These credentials are packaged into the deployable WAR file and committed to the source repository. DocVaultConfig uses them as fallback values when the DB_USER / DB_PASSWORD environment variables are absent, making them the effective credentials in any environment where those variables are not explicitly overridden.
