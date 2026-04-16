@echo off
echo =========================================
echo Starting Frontend Server on Port 8091
echo =========================================
echo.
echo Browser auto-open disabled. Visit http://localhost:8091 manually.
cd frontend
python -m http.server 8091

