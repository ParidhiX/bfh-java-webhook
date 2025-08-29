# BFH JAVA Webhook – Spring Boot (Java 17)

On startup, the app:
1) Calls the BFHL `generateWebhook` endpoint with your name/regNo/email,
2) Determines assigned question by regNo (odd/even),
3) Reads the final SQL from `src/main/resources/final_query.sql`,
4) Submits `{ "finalQuery": "..." }` to the returned webhook using the provided JWT token.
   If that fails, it posts to the fallback testWebhook URL.

## Prerequisites
- Java 17 (LTS)
- Maven
- Git

## Build and Package
### Windows
Double-click **build.bat** (or run it in Terminal). It will:
- Run `mvn clean package -DskipTests`
- Copy `target/bfh-java-webhook-1.0.0.jar` to `dist/bfh-java-webhook.jar`

### macOS/Linux
Run:
```bash
bash build.sh
```

## Run locally (optional)
```bash
java -jar target/bfh-java-webhook-1.0.0.jar
```

## GitHub Submission
1. Create a **public** GitHub repo (e.g., `bfh-java-webhook`).
2. Push this folder:
```bash
git init
git add .
git commit -m "Initial commit: app + dist jar"
git branch -M main
git remote add origin https://github.com/<your-username>/bfh-java-webhook.git
git push -u origin main
```
3. **Public GitHub repo link (code):**  
   `https://github.com/<your-username>/bfh-java-webhook`
4. **Publicly downloadable GitHub RAW link (JAR):**
   - In GitHub, open `dist/bfh-java-webhook.jar` → click **Raw** → copy the URL, which looks like:  
     `https://raw.githubusercontent.com/<your-username>/bfh-java-webhook/main/dist/bfh-java-webhook.jar`

## Configure
- Edit `src/main/resources/application.properties` with your real details.
- Put your solution SQL in `src/main/resources/final_query.sql`.
- If you see 401 on submission, set `submission.useBearerPrefix=true` and rebuild.

## Notes
- Use **Java 17** (Java 24 will not be compatible with some plugins).
