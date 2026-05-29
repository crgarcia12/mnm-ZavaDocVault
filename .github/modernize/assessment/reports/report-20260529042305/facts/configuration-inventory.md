# Configuration Inventory

| Key | Source | Purpose |
|---|---|---|
| `DB_HOST` / `db.host` | Environment / properties | SQL Server host |
| `DB_PORT` / `db.port` | Environment / properties | SQL Server port |
| `DB_NAME` / `db.name` | Environment / properties | Database name |
| `DB_USER` / `db.user` | Environment / properties | DB username |
| `DB_PASSWORD` / `db.password` | Environment / properties | DB password |
| `encrypt=false` | JDBC URL option | Disables transport encryption |
| `trustServerCertificate=true` | JDBC URL option | Trusts SQL cert without chain validation |

## Deployment Configuration

- Container image serves WAR on Tomcat 9.
- Port `8080` is exposed.
- Upload size limits are configured in `web.xml` multipart settings.
