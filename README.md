# Messaging App - Spring Boot

A full-stack messaging application built with Spring Boot, featuring user authentication, real-time messaging, and comprehensive user management capabilities.

## 🚀 Features

### Core Functionality
- **User Authentication**: JWT-based login/logout system with role-based access control
- **Messaging System**: Send and receive messages between users
- **Inbox/Outbox Management**: Organized message viewing with pagination
- **User Management**: Complete CRUD operations for user accounts (admin only)
- **Search & Filter**: Advanced filtering and sorting for both messages and users
- **Username Autocomplete**: Dynamic username suggestions while composing messages

### User Roles
- **Regular Users**: Can send/receive messages, view inbox/outbox
- **Admin Users**: Full user management capabilities + all regular user features

### Frontend Features
- **Responsive Web Interface**: Modern, clean UI built with HTML5, CSS3, and JavaScript
- **Real-time Search**: Dynamic username filtering and suggestions
- **Pagination**: Efficient handling of large datasets
- **Sorting & Filtering**: Multiple criteria for organizing data
- **Admin Dashboard**: Comprehensive user management interface

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.3.1** - Main framework
- **Spring Data JPA** - Database abstraction layer
- **Spring Web** - REST API development
- **PostgreSQL** - Primary database
- **JWT (JSON Web Tokens)** - Authentication and authorization
- **Maven** - Dependency management and build tool
- **Java 22** - Programming language

### Frontend
- **HTML5** - Structure and markup
- **CSS3** - Styling and responsive design
- **Vanilla JavaScript** - Interactive functionality
- **REST API Integration** - Backend communication

### Database
- **PostgreSQL** - Relational database for persistent storage
- **Hibernate** - ORM for database operations

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

- **Java 22** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Git** (for cloning the repository)

## 🔧 Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd messaging-app-spring-boot-main/messaging-app-2
```

### 2. Database Setup
1. Install and start PostgreSQL
2. Create a database named `postgres` (or modify `application.properties`)
3. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access the Application
- Open your web browser and navigate to `http://localhost:8080`
- The frontend will be served from the `/static` directory

## 📚 API Documentation

### Authentication Endpoints

#### Login
```http
POST /api/user/login
Content-Type: application/json

{
  "username": "your_username",
  "password": "your_password"
}
```

#### Logout
```http
POST /api/user/logout
Authorization: Bearer <token>
```

### Message Endpoints

#### Get Messages (Inbox/Outbox)
```http
GET /api/message?inout={in|out}&page=0&size=10&field=sender&value=john
Authorization: Bearer <token>
```

#### Send Message
```http
POST /api/message
Authorization: Bearer <token>
Content-Type: application/json

{
  "receiver": "recipient_username",
  "content": "Your message content"
}
```

### User Management Endpoints (Admin Only)

#### List Users
```http
GET /api/user?page=0&size=10&field=name&value=john
Authorization: Bearer <admin_token>
```

#### Create User
```http
POST /api/user
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "name": "John",
  "surname": "Doe",
  "birthdate": "1990-01-01",
  "gender": "Male",
  "email": "john@example.com",
  "location": "New York",
  "admin": false
}
```

#### Update User
```http
PUT /api/user/{username}?field=email&value=newemail@example.com
Authorization: Bearer <admin_token>
```

#### Delete User
```http
DELETE /api/user/{username}
Authorization: Bearer <admin_token>
```

#### Search Usernames
```http
GET /api/user/search?username=jo
Authorization: Bearer <token>
```

## 🏗️ Project Structure

```
messaging-app-2/
├── src/
│   ├── main/
│   │   ├── java/com/srdc/hw2/
│   │   │   ├── config/
│   │   │   │   └── WebConfig.java          # CORS and web configuration
│   │   │   ├── controller/
│   │   │   │   ├── MessageController.java  # Message-related endpoints
│   │   │   │   └── UserController.java     # User management endpoints
│   │   │   ├── model/
│   │   │   │   ├── Message.java           # Message entity
│   │   │   │   └── User.java              # User entity
│   │   │   ├── repository/
│   │   │   │   ├── MessageRepository.java # Message data access
│   │   │   │   └── UserRepository.java    # User data access
│   │   │   ├── security/
│   │   │   │   └── AuthService.java       # JWT authentication service
│   │   │   └── Hw2Application.java        # Main application class
│   │   └── resources/
│   │       ├── application.properties     # Configuration file
│   │       └── static/
│   │           ├── index.html            # Main frontend page
│   │           ├── scripts.js            # JavaScript functionality
│   │           └── styles.css            # CSS styling
│   └── test/
│       └── java/com/srdc/hw2/
│           └── Hw2ApplicationTests.java   # Test class
├── target/                               # Compiled classes
├── pom.xml                              # Maven configuration
└── mvnw, mvnw.cmd                       # Maven wrapper scripts
```

## 🔐 Security Features

- **JWT Authentication**: Secure token-based authentication system
- **Role-based Access Control**: Admin and regular user permissions
- **CORS Configuration**: Proper cross-origin resource sharing setup
- **Password Protection**: Secure password handling (Note: Consider implementing password hashing for production)

## 🎯 Usage Guide

### For Regular Users
1. **Login**: Use your credentials to access the application
2. **View Messages**: Check your inbox and outbox with sorting/filtering options
3. **Send Messages**: Compose and send messages to other users
4. **Search Users**: Find recipients using the username search feature

### For Admin Users
1. **User Management**: Create, update, and delete user accounts
2. **View All Users**: Browse and filter the complete user directory
3. **System Administration**: Manage user roles and permissions

## 🔧 Configuration

### Database Configuration
Modify `src/main/resources/application.properties` to customize:
- Database URL and credentials
- Hibernate settings
- Application name

### CORS Configuration
Update `src/main/java/com/srdc/hw2/config/WebConfig.java` to modify allowed origins for frontend access.

## 🚦 Development

### Building the Project
```bash
mvn clean compile
```

### Running Tests
```bash
mvn test
```

### Creating a Production Build
```bash
mvn clean package
```

## 📝 Future Enhancements

- **Password Encryption**: Implement bcrypt or similar for password security
- **Real-time Messaging**: Add WebSocket support for instant messaging
- **File Attachments**: Support for sending files and images
- **Message Status**: Read receipts and delivery confirmations
- **Email Notifications**: Email alerts for new messages
- **Profile Pictures**: User avatar support
- **Message Threading**: Conversation grouping
- **Advanced Search**: Full-text search in message content
