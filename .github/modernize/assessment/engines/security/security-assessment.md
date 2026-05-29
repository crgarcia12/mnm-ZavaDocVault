# Security Assessment Report

**Generated:** 2026-05-29T04:27:16.0000000Z

## Summary

| Metric | Count |
|--------|-------|
| Total Findings | 5 |
| CVE Vulnerabilities | 0 |
| CWE Vulnerabilities | 5 |
| Total Rules Assessed | 59 |
| Rules Passed | 54 |

### By Severity

| Severity | Count |
|----------|-------|
| mandatory | 1 |
| optional | 3 |
| potential | 1 |

## CWE Findings (Code-Level Vulnerabilities)

### CWE-477: Use of Obsolete Function
- **Category:** Code Quality
- **Severity:** optional
- **Story Points:** 1
- **Files:** build.gradle

The build uses legacy Gradle configuration (`apply plugin` and `sourceCompatibility` project properties) that is obsolete with current Gradle behavior and indicates outdated build maintenance patterns.

### CWE-789: Memory Allocation with Excessive Size Value
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 5
- **Files:** src/main/java/com/zavabank/docvault/DocumentUploadServlet.java

DocumentUploadServlet.readBytes reads uploaded file input stream into ByteArrayOutputStream without validating maximum in-code size (lines 108-117), allowing large memory allocation from request-controlled content.

### CWE-259: Use of Hard-coded Password
- **Category:** Credentials & Secrets
- **Severity:** optional
- **Story Points:** 5
- **Files:** src/main/resources/doc-vault.properties, Dockerfile

Hard-coded database password value `YourStrong!Passw0rd` is present in `src/main/resources/doc-vault.properties` and as `DB_PASSWORD` default in `Dockerfile`.

### CWE-798: Use of Hard-coded Credentials
- **Category:** Credentials & Secrets
- **Severity:** optional
- **Story Points:** 5
- **Files:** src/main/resources/doc-vault.properties, Dockerfile

The application includes hard-coded database credentials (`db.user=sa`, `db.****** in resource properties and mirrored container defaults in Dockerfile environment variables.

### CWE-434: Unrestricted Upload of File with Dangerous Type
- **Category:** File & Path Security
- **Severity:** mandatory
- **Story Points:** 8
- **Files:** src/main/java/com/zavabank/docvault/DocumentUploadServlet.java

DocumentUploadServlet.doPost accepts multipart file uploads via request.getPart("file") and persists bytes/content type without extension or MIME allowlist validation (lines 40-83), enabling upload of dangerous file types.

