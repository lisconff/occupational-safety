@echo off
echo =========================================
echo Starting Frontend Server on Port 8091
echo =========================================
echo.
echo Opening browser...
start http://localhost:8091/login.html
cd frontend
python -m http.server 8091

