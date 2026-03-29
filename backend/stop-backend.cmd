@echo off
setlocal EnableDelayedExpansion

set "PIDS= "
for /f "tokens=5" %%P in ('netstat -ano ^| findstr :8090 ^| findstr LISTENING') do (
  echo !PIDS! | findstr /C:" %%P " >nul || set "PIDS=!PIDS!%%P "
)

if "!PIDS!"==" " (
  echo [backend] No backend process on port 8090.
  exit /b 0
)

for %%P in (!PIDS!) do (
  echo [backend] Stopping PID %%P on port 8090...
  taskkill /PID %%P /F >nul 2>nul
)

timeout /t 1 >nul

set "BUSY="
for /f "tokens=5" %%P in ('netstat -ano ^| findstr :8090 ^| findstr LISTENING') do set "BUSY=1"

if defined BUSY (
  echo [backend] Warning: port 8090 is still occupied.
) else (
  echo [backend] Stopped backend on port 8090.
)

exit /b 0
