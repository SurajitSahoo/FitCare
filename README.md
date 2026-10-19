# 🏋️ FitCare – AI Fitness Tracker

An AI-powered fitness tracking application built using **Spring Boot Microservices** that helps users track their physical activities and receive personalized fitness recommendations using Google Gemini AI. The application uses Apache Kafka for asynchronous communication between services, API Gateway for centralized routing, and Eureka for service discovery.

## 🚀 Features

### 🔐 User Management & Security

- User registration and authentication.
- User profile and account management.
- REST API-based communication between services.
- Centralized API routing through Spring Cloud Gateway.
- JWT-based security integration.

### 🏃 Activity Tracking

- Record and manage physical activities.
- Track activity type, duration, and calories burned.
- Store activity timestamps and additional metrics.
- Associate activities with individual users.
- Maintain activity records using MongoDB.

### 🤖 AI-Powered Fitness Recommendations

- Generate personalized fitness insights using Google Gemini AI.
- Analyze activity details to provide recommendations.
- Receive improvement suggestions based on workout information.
- Process AI-generated responses in JSON format.
- Support future enhancements for personalized fitness planning.

### ⚡ Event-Driven Microservices

- Use Apache Kafka for asynchronous communication.
- Publish activity events from the Activity Service.
- Consume activity events in the AI Service.
- Process fitness data independently from the activity request.
- Improve service decoupling and scalability.

### 🌐 Service Discovery & API Gateway

- Use Netflix Eureka for service registration and discovery.
- Route client requests through Spring Cloud Gateway.
- Communicate between independently deployed microservices.
- Simplify service access through a centralized entry point.

## 🛠️ Tech Stack

| Technology           | Purpose                                                                |
| -------------------- | ---------------------------------------------------------------------- |
| Java                 | Backend programming                                                    |
| Spring Boot          | Microservices and REST API development                                 |
| Spring Web / WebFlux | HTTP API development and reactive gateway communication, as applicable |
| Spring Security      | Authentication and authorization                                       |
| Oauth2 & JWT         | Token-based authentication                                             |
| Spring Data JPA      | Relational database operations                                         |
| PostgreSQL           | User data storage                                                      |
| MongoDB              | Activity and AI recommendation data storage                            |
| Apache Kafka         | Asynchronous event-driven communication                                |
| Spring Cloud Gateway | Centralized API routing                                                |
| Netflix Eureka       | Service discovery                                                      |
| Google Gemini AI     | AI-powered fitness recommendations                                     |
| Maven                | Dependency management                                                  |
| Postman              | API testing                                                            |
| Git & GitHub         | Version control                                                        |

## 🏗️ Microservices Architecture

The application is divided into independent services, each responsible for a specific business function.

```text
                   Client / Frontend
                          |
                          v
                 API Gateway :8080
                          |
              +-----------+-----------+
              |                       |
              v                       v
       User Service :8081      Activity Service :8082
              |                       |
              v                       v
          PostgreSQL               MongoDB
                                      |
                                      v
                                 Kafka Topic
                               activity-events
                                      |
                                      v
                               AI Service :8083
                                      |
                                      v
                                Gemini AI API
                                      |
                                      v
                           Fitness Recommendations

                 Eureka Server :8761
                    Service Discovery
```

### Microservices Overview

| Service          | Port | Responsibility                                             |
| ---------------- | ---: | ---------------------------------------------------------- |
| Gateway Service  | 8080 | Routes incoming client requests                            |
| User Service     | 8081 | Manages user-related operations                            |
| Activity Service | 8082 | Records and manages fitness activities                     |
| AI Service       | 8083 | Processes activity events and generates AI recommendations |
| Eureka Server    | 8761 | Registers and discovers services                           |
| Config Server    | 8880 | Centralized configuration management, if enabled           |

*Ports and responsibilities reflect the project's development configuration and may vary by environment.*

## 🔄 How the Application Works

1. **User management:** The user registers and accesses the application through the API Gateway.
2. **Activity submission:** The user submits fitness activity details to the Activity Service.
3. **Event publishing:** The Activity Service publishes an activity event to the Kafka topic `activity-events`.
4. **Event consumption:** The AI Service consumes the activity event using its configured consumer group.
5. **AI analysis:** The AI Service sends relevant activity information to the Gemini AI API.
6. **Recommendation generation:** Gemini returns fitness analysis and improvement suggestions, which the AI Service processes and stores or returns as configured.
7. **Service discovery:** Eureka helps services discover registered service instances.

## 📂 Project Structure

```text
FitCare/
├── backend/
│   ├── gateway-service/
│   ├── user-service/
│   ├── activity-service/
│   ├── ai-service/
│   ├── eureka-server/
│   └── config-server/
├── README.md
└── .gitignore
```

*Note: Adjust directory names to match the actual folder structure in your repository.*

## ⚙️ Installation & Setup

### Prerequisites

- Java version configured in your project.
- Maven.
- PostgreSQL.
- MongoDB.
- Apache Kafka.
- Google Gemini API key.
- Postman (optional, for API testing).

### 1. Clone the Repository

```bash
git clone https://github.com/SurajitSahoo/REPOSITORY_NAME.git
cd REPOSITORY_NAME
```

Replace `REPOSITORY_NAME` with the actual name of your FitCare GitHub repository.

### 2. Configure Databases

Configure PostgreSQL for the User Service and MongoDB for the Activity and AI services.

Example environment variables:

```properties
DB_URL=jdbc:postgresql://localhost:8888/fitness
DB_USERNAME=your_username
DB_PASSWORD=your_password

MONGODB_URI=mongodb://localhost:27017/aiactivityfitness

GEMINI_API_KEY=your_gemini_api_key
GEMINI_URL=your_gemini_api_url
```

These are illustrative environment variable names. Map them to the actual property names used in each service.

### 3. Configure Kafka

Ensure your Kafka broker is running and the application uses the correct bootstrap server.

```properties
spring.kafka.bootstrap-servers=localhost:9092
```

The Activity Service and AI Service should use the same Kafka topic configuration.

```text
activity-events
```

### 4. Start the Services

Start the infrastructure and services in the order required by your configuration:

1. PostgreSQL and MongoDB.
2. Kafka broker.
3. Eureka Server.
4. Config Server, if enabled.
5. User Service.
6. Activity Service.
7. AI Service.
8. Gateway Service.

Use IntelliJ IDEA or run each Spring Boot service with Maven:

```bash
mvn spring-boot:run
```

Run this command from the directory containing the corresponding service's `pom.xml`.

## 🔌 API Endpoints

The following are example endpoint categories. Verify the exact mappings in your controllers before publishing this section.

| Method | Endpoint                            | Description                 |
| ------ | ----------------------------------- | --------------------------- |
| POST   | `/api/users/register`               | Register a user             |
| GET    | `/api/users/{id}`                   | Retrieve user information   |
| POST   | `/api/activities`                   | Submit a fitness activity   |
| GET    | `/api/activities`                   | Retrieve activity records   |
| GET    | `/api/recommendations/{activityId}` | Retrieve AI recommendations |

The actual paths may differ depending on your controller mappings and gateway configuration.

## 🧪 Testing

Use Postman to test the application.

- Test user registration and user retrieval.
- Verify authentication and authorization.
- Submit activities and validate stored records.
- Check Kafka event publishing and consumption.
- Verify Gemini AI responses and JSON parsing.
- Test service registration through Eureka.
- Verify API Gateway routing.
- Test error handling when Kafka or Gemini is unavailable.

## 🔒 Security & Reliability

- Oauth2-based authentication using Keycloak, where configured.
- Separation of responsibilities across microservices.
- Environment variables for sensitive configuration.
- Asynchronous event processing through Kafka.
- Structured error handling for external AI API failures.
- Retry and dead-letter handling where implemented.
- Independent service deployment and scaling.

## 🔮 Future Enhancements

- Integration with smartwatches and wearable devices.
- Heart-rate, step-count, and sleep tracking.
- Personalized workout plans based on activity history and fitness goals.
- Nutrition and hydration recommendations.
- Interactive fitness dashboards and progress charts.
- Docker containerization and Kubernetes deployment.
- Automated testing, monitoring, and distributed tracing.
- Improved Kafka retry policies and dead-letter topic monitoring.

## 👨‍💻 Author

**Surajit Sahoo**

- GitHub: [SurajitSahoo](https://github.com/SurajitSahoo)

POSTMAN OR
     FRONTEND
        │
        ▼
 ┌─────────────┐       ┌─────────────┐       ┌─────────────┐
 │             ├──────►│    USER     ├──────►│ PostgreSQL  │
 │             │       └──────▲──────┘       └─────────────┘
 │             │              │
 │             │       ┌──────┴──────┐       ┌─────────────┐
 │   Gateway   ├──────►│  Activity   ├──────►│   MongoDB   │
 │             │       └──────┬──────┘       └─────────────┘
 │             │              │
 │             │       ┌──────▼──────┐
 │             │       │    KAFKA    │
 │             │       └──────┬──────┘
 │             │              │
 │             │       ┌──────▼──────┐       ┌─────────────┐
 │             ├──────►│ AI Service  ├──────►│   MongoDB   │
 └──────┬──────┘       └─────────────┘       └─────────────┘
        │
        ▼
 ┌─────────────┐     ┌───────────────┐     ┌───────────────┐
 │  KEYCLOAK   │     │ CONFIG SERVER │     │    EUREKA     │
 └─────────────┘     └───────────────┘     └───────────────┘
