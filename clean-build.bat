@echo off
setlocal
cd /d "%~dp0"
REM Compilacion completa desde cero (mas lenta pero segura)
echo [CLEAN BUILD] Limpiando y compilando todo desde cero...
mvn clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo [OK] Clean build exitoso!
    echo [JAR] Listado de artefactos en out\:
    dir /b out\*.jar
) else (
    echo [ERROR] Fallo la compilacion
)
echo.
pause
