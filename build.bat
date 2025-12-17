@echo off
setlocal
cd /d "%~dp0"
REM Compilacion rapida incremental (solo cambios)
echo [BUILD] Compilando cambios...
mvn package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo [OK] Build exitoso!
    echo [JAR] Listado de artefactos en out\:
    dir /b out\*.jar
) else (
    echo [ERROR] Fallo la compilacion
)
echo.
pause
