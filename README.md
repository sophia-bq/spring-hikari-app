# Spring HikariCP Aurora App

Minimal Spring Boot application demonstrating HikariCP connection pooling with AWS Advanced JDBC Wrapper and MariaDB driver for Aurora MySQL clusters.

## Features

- HikariCP connection pool with persistent connections
- AWS Advanced JDBC Wrapper with multiple plugins:
  - `auroraConnectionTracker` - Tracks Aurora connections
  - `failover` - Automatic failover handling
  - `efm` - Enhanced failure monitoring
  - `readWriteSplitting` - Automatic read/write splitting
- MariaDB JDBC driver as underlying driver
- Read-write splitting demonstration using `Connection.setReadOnly()`
- Comprehensive logging for connection debugging

## Configuration

Update `src/main/resources/application.yml` with your Aurora cluster details:

```yaml
spring:
  datasource:
    url: jdbc:aws-wrapper:mariadb://your-cluster.cluster-xxx.region.rds.amazonaws.com:3306/database
    username: your-username
    password: your-password
```

## Running

```bash
./gradlew bootRun
```

## Endpoints

### Basic Operations
- `GET /test` - Execute simple query (returns "Query result: 1")

### Bulk Operations
- `GET /bulk/{count}` - Execute multiple queries alternating between read and write operations
  - Even iterations: SELECT queries (intended for readers)
  - Odd iterations: SET + SELECT queries (intended for writers)

### Read-Write Splitting Demo
- `GET /read-write-test/{count}` - Demonstrates read-write splitting using `Connection.setReadOnly()`
  - Even iterations: `setReadOnly(true)` - routes to reader instances
  - Odd iterations: `setReadOnly(false)` - routes to writer instance
  - Returns Aurora server IDs showing which instance handled each request

## Plugin Configuration

The application uses the following wrapper plugins in order:
1. `auroraConnectionTracker` - Must be first for proper connection tracking
2. `readWriteSplitting` - Must be before failover for proper exception handling
3. `failover2` - Handles connection failures and topology changes
4. `efm2` - Enhanced failure monitoring

## Logging

Detailed logging is enabled for:
- `software.amazon.jdbc: TRACE` - AWS JDBC Wrapper operations
- `com.zaxxer.hikari: DEBUG` - HikariCP connection pool operations

## Dependencies

- Spring Boot 3.2.0
- AWS Advanced JDBC Wrapper 2.6.3
- MariaDB Java Client 3.3.2
- HikariCP (included with Spring Boot JDBC starter)
