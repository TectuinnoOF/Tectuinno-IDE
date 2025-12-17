@echo off
setlocal
cd /d "%~dp0"
REM Compilacion rapida y ejecucion
echo [BUILD] Compilando cambios...
call mvn package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo [OK] Build exitoso!
    echo [RUN] Ejecutando IDE...
    echo.
    
    REM Buscar el JAR ejecutable mas reciente (sin sufijo -lib)
    set LATEST_JAR=
    for /f "delims=" %%i in ('dir /b /od "out\Tectuinno-IDE_*.jar" ^| findstr /vi "-lib" 2^>nul') do set LATEST_JAR=%%i

    if defined LATEST_JAR (
        echo [RUN] Ejecutando: %LATEST_JAR%
        java -jar "out\%LATEST_JAR%"
    ) else (
        echo [ERROR] No se encontro el JAR compilado en out\
    )
) else (
    echo [ERROR] Fallo la compilacion
)
echo.
pause
