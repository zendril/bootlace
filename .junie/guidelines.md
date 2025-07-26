# Spring Boot Application Guidelines

This project uses Spring Boot. 

- **Startup Marker:** When the Spring Boot application successfully starts, it typically logs a line containing "Started [YourApplicationName] in". For example, "Started DemoApplication in 5.123 seconds".
- **Default Port:** The application usually runs on port 8080 unless configured otherwise.
- **Process Termination:** To gracefully shut down a Spring Boot application running on a specific port, you can find its PID and then use `kill <PID>` (on Linux/macOS) or `taskkill /PID <PID> /F` (on Windows).

# Common Commands

- **Find PID by Port (Linux/macOS):** `lsof -t -i :<port>`
- **Find PID by Port (Windows):** `netstat -ano | findstr :<port>` (then parse output for PID)
- **Kill Process (Linux/macOS):** `kill <PID>`
- **Kill Process (Windows):** `taskkill /PID <PID> /F`
- **Start task in background (Windows):** `mvn spring-boot:run > spring-boot.log 2>&1 &`

# Build & Testing

- **Building:** Do NOT use 'Build entire project'. Do use `mvn clean compile`.
- **Verifying:** 
  - Launch `mvn spring-boot:run` using a script like:
    ```
    Start-Process -FilePath "cmd" -ArgumentList "/c", "mvn org.springframework.boot:spring-boot-maven-plugin:run > spring-boot-fixed.log 2>&1" -WindowStyle Hidden
    ```
  - then check the outputs for success or failure
  - then find the process and kill it.