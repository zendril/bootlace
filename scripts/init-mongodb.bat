@echo off
echo Initializing MongoDB for Bootlace application...
echo.

REM Check if MongoDB is running
echo Checking if MongoDB is running...
netstat -an | findstr :27017 >nul
if errorlevel 1 (
    echo ERROR: MongoDB is not running on port 27017
    echo Please start MongoDB service first
    echo.
    echo To start MongoDB:
    echo   net start MongoDB
    echo   or
    echo   mongod --dbpath "C:\data\db"
    pause
    exit /b 1
)

echo MongoDB is running on port 27017
echo.

REM Run the initialization script
echo Running MongoDB initialization script...
mongo bootlace src/main/resources/scripts/init-mongodb.js

if errorlevel 1 (
    echo ERROR: Failed to initialize MongoDB
    pause
    exit /b 1
)

echo.
echo MongoDB initialization completed successfully!
echo.
echo Default accounts that will be created by the application:
echo   - admin/admin (ADMIN, USER roles)
echo   - mark/bimble (ADMIN, USER roles)  
echo   - user/user (USER role)
echo.
pause