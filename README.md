# Spring Boot Background Task Runner

A database-backed system for running background tasks (like sending emails) asynchronously in Spring Boot.

Instead of running long tasks directly on the main web thread, which can slow down the system or lose data if the server crashes, this application saves tasks to a database first. It then picks them up and processes them in the background, ensuring no work is ever lost.

## How It Works

The system manages the entire lifecycle of a background task:

1. **Submission:** A client sends a request (like an email to be sent) along with a JSON payload.
2. **Database Storage:** The task is saved to a PostgreSQL database with a `PENDING` status.
3. **Polling:** The application regularly checks the database for pending tasks.
4. **Safe Execution:** To prevent two threads from running the same job, the task is marked as `RUNNING` before it is handed to an asynchronous background thread.
5. **Completion:** If successful, the task is marked as `COMPLETED`.

## Key Features

1. **Flexible JSON Payloads:** Tasks are not hardcoded. You can pass any required data (like recipient addresses and email bodies) as a generic JSON string, which the application automatically parses when the task runs.
2. **Automatic Retries:** If a task fails due to a temporary issue (like an SMTP server timeout), the system catches the error, increments a retry counter, and sets the task back to `PENDING` to try again later.
3. **Failure Limits:** To prevent infinite loops, tasks that fail repeatedly are eventually marked as `FAILED`.
4. **Stuck Task Recovery:** If the server is forcefully shut down while a task is `RUNNING`, the application will automatically detect these abandoned tasks on startup and reset them to `PENDING` so they aren't lost forever.
5. **Spring Context Integration:** Tasks are loaded from the database but can still securely access Spring tools (like `JavaMailSender` and `ObjectMapper`) to get their work done.

## Tech Stack

1. **Java, Spring Boot** (Web, Data JPA, Mail)
2. **PostgreSQL** (Database)
3. **Jackson** (JSON Parsing)
4. **Mailtrap** (Used for safe local SMTP testing)

## 🚀 How to Run Locally

### 1. Database Setup
Make sure PostgreSQL is running locally and create a database for the project. Update the `application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 2. Email Setup (Sandbox)
This project is configured to use an SMTP server to send real emails. For local testing, it is recommended to use a free tool like Mailtrap to catch emails safely. Add your credentials to `application.properties`:
```properties
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=your_username
spring.mail.password=your_password
```

### 3. Build and Run
Use the included Maven wrapper to start the application. Hibernate will automatically create the required database tables on the first run.
```bash
./mvnw clean spring-boot:run
```

### 4. Test an Email Task
Once the server is running, you can add a new task to the queue by sending a POST request to your endpoint.

**POST** `http://localhost:8080/add`
```json
{
  "type": "EMAIL",
  "priority": 1,
  "payload": "{\"recipient\": \"test@example.com\", \"subject\": \"Hello World\", \"body\": \"Testing the background task runner.\"}"
}
```
Watch your application logs to see the system pick up the task, parse the JSON, and dispatch the email.