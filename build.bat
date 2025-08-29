\
@echo off
setlocal enabledelayedexpansion
echo === Building Spring Boot JAR with Maven ===
where mvn >nul 2>nul
if %errorlevel% neq 0 (
  echo Maven not found. Please install Maven and make sure it's on PATH.
  pause
  exit /b 1
)
mvn clean package -DskipTests
if %errorlevel% neq 0 (
  echo Maven build failed.
  pause
  exit /b 1
)
if not exist dist mkdir dist
copy /Y target\bfh-java-webhook-1.0.0.jar dist\bfh-java-webhook.jar >nul
if %errorlevel% neq 0 (
  echo Copy failed. Make sure the build produced the jar.
  pause
  exit /b 1
)
echo === Done. JAR is at dist\bfh-java-webhook.jar ===
pause
