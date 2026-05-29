# Configuration & Externalized Settings Inventory

This inventory captures local property files, environment-variable overrides, and runtime container configuration used by the application.

## Configuration Sources

| Source | Type | Path/Location | Notes |
|---|---|---|---|
| doc-vault.properties | Application properties | `src/main/resources/doc-vault.properties` | Default DB host, port, name, user, and password |
| Environment variables | Runtime overrides | `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | Override property file values in `DocVaultConfig` |
| Dockerfile ENV | Container defaults | `Dockerfile` | Defines default DB values and exposes `8080` |
| web.xml | Servlet runtime config | `src/main/webapp/WEB-INF/web.xml` | Defines servlet mappings and multipart limits |

## Build Profiles

| Profile | Activation | Purpose | Key Dependencies/Plugins |
|---|---|---|---|
| default Gradle build | `gradle war` | Build deployable WAR package | `java` and `war` plugins |

## Runtime Profiles

| Profile | Activation Method | Config Files | Key Overrides |
|---|---|---|---|
| default | Startup defaults | `doc-vault.properties` | SQL Server connection defaults |
| environment override | Container/runtime env vars | Docker runtime environment | DB host, port, database name, username, password |

## Properties Inventory

| Property Key | Default | Profiles | Source |
|---|---|---|---|
| db.host | sqlserver | default | doc-vault.properties |
| db.port | 1433 | default | doc-vault.properties |
| db.name | ZavaBankDB | default | doc-vault.properties |
| db.user | sa | default | doc-vault.properties |
| db.password | [MASKED] | default | doc-vault.properties |
| DB_HOST | sqlserver | environment override | Dockerfile ENV / runtime env |
| DB_PORT | 1433 | environment override | Dockerfile ENV / runtime env |
| DB_NAME | ZavaBankDB | environment override | Dockerfile ENV / runtime env |
| DB_USER | sa | environment override | Dockerfile ENV / runtime env |
| DB_PASSWORD | [MASKED] | environment override | Dockerfile ENV / runtime env |

## Startup Parameters & Resource Requirements

| Service | JVM/Runtime Options | Memory | Instance Count |
|---|---|---|---|
| doc-vault-web | `catalina.sh run` (Tomcat) | Not specified | Not specified |

## Startup Dependency Chain

1. `doc-vault-web` container starts on Tomcat.
2. `DocVaultBootstrapServlet` initializes and attempts to create `Documents` table.
3. API endpoints become usable once SQL Server connectivity succeeds.

## Secrets & Sensitive Configuration

| Secret Reference | Type | Storage (masked) |
|---|---|---|
| db.password / DB_PASSWORD | Database password | Properties file or environment variable as `[MASKED]` |

### Secrets Provisioning Workflow

Secrets are currently supplied through local properties and/or container environment variables. The web service reads values during startup and uses them for JDBC connection creation. No external secret manager or managed identity flow was found.

## Feature Flags

| Flag Name | Default | Controlled By |
|---|---|---|
| None detected | n/a | n/a |

## Framework & Runtime Versions

| Component | Version | Source |
|---|---|---|
| Java | 8 | `build.gradle` sourceCompatibility/targetCompatibility |
| Servlet API | 3.1.0 | Gradle dependency |
| SQL Server JDBC | 12.6.3.jre8 | Gradle dependency |
| Tomcat base image | 9-jdk8 | Dockerfile |
| Gradle build image | 7.6-jdk8 | Dockerfile |
