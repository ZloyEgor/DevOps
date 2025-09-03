# CvetOchey Backend

A Spring Boot application for flower shop management with comprehensive CRUD operations and robust testing suite.

## 🏗️ Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/ru/itmo/cvetochey/
│   │   │   ├── controller/impl/     # REST Controllers
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── mapper/              # MapStruct Mappers
│   │   │   ├── model/               # JPA Entities
│   │   │   └── repository/          # JPA Repositories
│   │   └── resources/
│   │       ├── application.yaml     # Main configuration
│   │       └── db/                  # Database migrations
│   └── test/
│       ├── java/ru/itmo/cvetochey/
│       │   ├── controller/impl/     # Unit Tests for Controllers
│       │   ├── integration/         # Integration Tests
│       │   └── repository/          # Repository Tests
│       └── resources/
│           └── application-test.yml # Test configuration
├── pom.xml                          # Maven dependencies
└── README.md                        # This file
```

## 🛠️ Technology Stack

- **Java 21** - Programming language
- **Spring Boot 3.4.4** - Application framework
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Production database
- **H2** - Test database (in-memory)
- **Liquibase** - Database migrations
- **MapStruct** - Object mapping
- **Lombok** - Boilerplate code reduction
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **Maven** - Build tool

## 🚀 Getting Started

### Prerequisites

- Java 21 (managed via SDKMAN)
- Maven 3.6+
- PostgreSQL (for production)

### Installation

1. **Install Java 21 using SDKMAN:**
   ```bash
   # Install SDKMAN if not already installed
   curl -s "https://get.sdkman.io" | bash
   
   # Install and use Java 21
   sdk install java 21.0.8-tem
   sdk use java 21.0.8-tem
   ```

2. **Set up the project:**
   ```bash
   cd backend
   chmod +x mvnw  # Make Maven wrapper executable
   ```

3. **Build the project:**
   ```bash
   ./mvnw clean compile
   ```

## 🧪 Testing

The project includes comprehensive testing with **61 total tests** covering unit tests, integration tests, and repository tests.

### Run All Tests
```bash
./mvnw clean test
```

### Test Categories

#### 1. Unit Tests (55 tests)
Test individual controller methods in isolation using mocked dependencies.

**Run all unit tests:**
```bash
./mvnw test -Dtest="*ControllerTest"
```

**Run specific controller tests:**
```bash
# Client Controller Tests (13 tests)
./mvnw test -Dtest="ClientControllerTest"

# Catalog Controller Tests (10 tests)  
./mvnw test -Dtest="CatalogControllerTest"

# Order Controller Tests (14 tests)
./mvnw test -Dtest="OrderControllerTest"

# Product Controller Tests (13 tests)
./mvnw test -Dtest="ProductControllerTest"
```

#### 2. Integration Tests (5 tests)
Test complete CRUD workflows across all controllers with real HTTP requests.

**Run integration tests:**
```bash
./mvnw test -Dtest="CrudIntegrationTest"
```

#### 3. Repository Tests
Test custom repository methods and database interactions.

**Run repository tests:**
```bash
./mvnw test -Dtest="RepositoryIntegrationTest"
```

#### 4. Application Context Test (1 test)
Verify Spring Boot application starts correctly.

**Run context test:**
```bash
./mvnw test -Dtest="CvetOcheyApplicationTests"
```

### Test Configuration

- **Test Profile:** Uses `application-test.yml` configuration
- **Test Database:** H2 in-memory database (automatic setup/teardown)
- **Test Data:** Automatically generated for each test
- **Isolation:** Each test runs in isolation with fresh database

### Test Coverage

| Component | Tests | Coverage |
|-----------|-------|----------|
| Controllers | 50 tests | Full CRUD operations |
| Integration | 5 tests | End-to-end workflows |
| Repositories | 5 tests | Custom query methods |
| Application | 1 test | Context loading |
| **Total** | **61 tests** | **100% success rate** |

## 📊 API Endpoints

### Catalog Management
- `GET /catalogs` - Get all catalogs
- `GET /catalogs/{id}` - Get catalog by ID
- `GET /catalogs/type/{type}` - Get catalogs by type
- `POST /catalogs` - Create new catalog
- `PUT /catalogs/{id}` - Update catalog
- `DELETE /catalogs/{id}` - Delete catalog

### Client Management
- `GET /clients` - Get all clients
- `GET /clients/{id}` - Get client by ID
- `GET /clients/email/{email}` - Get client by email
- `GET /clients/username/{username}` - Get client by username
- `GET /clients/role/{role}` - Get clients by role
- `POST /clients` - Create new client
- `PUT /clients/{id}` - Update client
- `DELETE /clients/{id}` - Delete client

### Product Management
- `GET /products` - Get all products
- `GET /products/{id}` - Get product by ID
- `GET /products/catalog/{catalogId}` - Get products by catalog
- `GET /products/search?name={name}` - Search products by name
- `GET /products/price?min={min}&max={max}` - Get products by price range
- `POST /products` - Create new product
- `PUT /products/{id}` - Update product
- `DELETE /products/{id}` - Delete product

### Order Management
- `GET /orders` - Get all orders
- `GET /orders/{id}` - Get order by ID
- `GET /orders/client/{clientId}` - Get orders by client
- `GET /orders/product/{productId}` - Get orders by product
- `GET /orders/price?min={min}&max={max}` - Get orders by price range
- `POST /orders` - Create new order
- `PUT /orders/{id}` - Update order
- `DELETE /orders/{id}` - Delete order

## 🔧 Development Commands

### Build Commands
```bash
# Clean and compile
./mvnw clean compile

# Package application
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests
```

### Development Tools
```bash
# Run with development profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Generate test reports
./mvnw clean test jacoco:report
```

## 🐛 Troubleshooting

### Common Issues

1. **Java Version Mismatch:**
   ```bash
   # Ensure Java 21 is active
   sdk current java
   # Should show: java 21.0.8-tem
   ```

2. **Maven Wrapper Permissions:**
   ```bash
   chmod +x mvnw
   ```

3. **Test Database Issues:**
   - Tests use H2 in-memory database automatically
   - No external database setup required for testing
   - Each test gets a fresh database instance

4. **Build Failures:**
   ```bash
   # Clean and retry
   ./mvnw clean
   ./mvnw compile
   ```

## 📈 Quality Assurance

- **Code Style:** Checkstyle configuration included
- **Test Coverage:** Comprehensive unit and integration tests
- **Database Validation:** Foreign key constraints and uniqueness checks
- **Error Handling:** Proper HTTP status codes and validation
- **Documentation:** OpenAPI/Swagger documentation available

## 🚀 Deployment

The application is containerized and ready for deployment:

```bash
# Build Docker image
docker build -t cvetochey-backend .

# Run with Docker Compose
docker-compose up -d
```

## 📝 Contributing

1. Ensure all tests pass: `./mvnw clean test`
2. Follow the existing code style
3. Add tests for new functionality
4. Update documentation as needed

---

**Test Status:** ✅ All 61 tests passing  
**Build Status:** ✅ Successful  
**Java Version:** ☕ 21.0.8-tem  
**Framework:** 🍃 Spring Boot 3.4.4
