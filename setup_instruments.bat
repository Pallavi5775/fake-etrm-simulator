@echo off
REM Database Setup Script Runner
REM Usage: setup_instruments.bat [db_host] [db_port] [db_name] [db_user] [db_password]

if "%~1"=="" (
    set DB_HOST=localhost
) else (
    set DB_HOST=%~1
)

if "%~2"=="" (
    set DB_PORT=5432
) else (
    set DB_PORT=%~2
)

if "%~3"=="" (
    set DB_NAME=ctrm_db
) else (
    set DB_NAME=%~3
)

if "%~4"=="" (
    set DB_USER=ctrm_user
) else (
    set DB_USER=%~4
)

if "%~5"=="" (
    set DB_PASSWORD=ctrm_pass
) else (
    set DB_PASSWORD=%~5
)

echo Setting up instruments in database %DB_NAME% on %DB_HOST%:%DB_PORT% as user %DB_USER%
echo This will truncate all data and insert new commodities and instruments.
pause

psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f setup_instruments.sql

if %ERRORLEVEL% EQU 0 (
    echo Database setup completed successfully. IDs start from 1.
) else (
    echo Error during database setup.
)

pause