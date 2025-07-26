---
trigger: always_on
---

- **Building:** 
  - Use `npm run build` for generating the ui
  - Use `mvn clean compile` to validate the java build
  - Use `mvn spring-boot:run` but you will have to start this in the background with no window and sending the logs to a file for you to read.