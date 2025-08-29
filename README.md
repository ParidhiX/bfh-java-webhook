# BFH JAVA Webhook – Spring Boot

This project is the submission for the BFHL Java assignment.

On startup, the application:
1. Calls the BFHL `generateWebhook` endpoint with the candidate details (name, regNo, email).
2. Determines the assigned question based on the last two digits of the regNo (odd → Question 1, even → Question 2).
3. Reads the final SQL query from `src/main/resources/final_query.sql`.
4. Submits `{ "finalQuery": "..." }` to the provided webhook using the JWT token.  
   If that fails, it posts to the fallback `testWebhook` URL.

---

## Build Instructions

### Prerequisites
- **Java 17 (LTS)**
- **Maven**
- **Git**

### Build the JAR
On Windows:
```bat
build.bat
