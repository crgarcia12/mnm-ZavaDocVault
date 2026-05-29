# Business Workflows

## Core Workflow: Document Upload

```mermaid
sequenceDiagram
    participant User
    participant UI as upload.jsp
    participant API as DocumentUploadServlet
    participant DB as SQL Server

    User->>UI: Select file and submit
    UI->>API: POST multipart upload
    API->>DB: Insert document row
    DB-->>API: Generated DocumentID
    API-->>User: Upload confirmation JSON
```

## Core Workflow: Document Retrieval

1. Client requests `/api/documents/customer/{customerId}` to list available documents.
2. Client selects a document id and requests `/api/documents/{id}`.
3. Service streams file bytes from SQL Server to the client.
