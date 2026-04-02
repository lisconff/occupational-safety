@echo off
setlocal EnableDelayedExpansion

pushd "%~dp0"

set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [backend] JAVA_HOME=%JAVA_HOME%

powershell -NoProfile -Command "try { $r = Invoke-WebRequest -UseBasicParsing http://localhost:8090/api/health -TimeoutSec 2; if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>nul
if not errorlevel 1 (
        echo.
        echo ===================================================
        echo [backend] Backend is ALREADY running on port 8090!
        echo [backend] Health: http://localhost:8090/api/health
        echo ===================================================
        echo.
        echo [backend] Ready to accept requests.
        :: start http://localhost:8090/
        set "EXIT_CODE=0"
        goto FINISH
)

set "LOG_FILE=%~dp0backend-run.log"
if exist "%LOG_FILE%" del /f /q "%LOG_FILE%" >nul 2>nul

echo [backend] Starting Spring Boot on port 8090 in background...
start "" /b cmd /c "cd /d "%~dp0" && set "JAVA_HOME=%JAVA_HOME%" && set "PATH=%JAVA_HOME%\bin;%PATH%" && call "%~dp0mvnw.cmd" spring-boot:run > "%LOG_FILE%" 2>&1"

echo [backend] Waiting for health check...
set "READY="
for /l %%I in (1,1,30) do (
        powershell -NoProfile -Command "try { $r = Invoke-WebRequest -UseBasicParsing http://localhost:8090/api/health -TimeoutSec 2; if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>nul
        if not errorlevel 1 (
                set "READY=1"
                goto READY_OK
        )
        timeout /t 1 >nul
)

:READY_OK
if defined READY (
        echo.
        echo ===================================================
        echo [backend] Started successfully on port 8090!
        echo [backend] Health: http://localhost:8090/api/health
        echo [backend] Log: %LOG_FILE%
        echo ===================================================
        echo.
        echo [backend] Ready to accept requests.
        :: start http://localhost:8090/
        set "EXIT_CODE=0"
    ) else (
        echo [backend] Failed to become ready. See log: %LOG_FILE%
        set "EXIT_CODE=1"
)

:FINISH
popd

echo [backend] Exit code: %EXIT_CODE%
exit /b %EXIT_CODE%

