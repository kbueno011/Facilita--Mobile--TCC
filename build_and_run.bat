@echo off
REM ============================================
REM Script de Build e Deploy - Facilita App
REM ============================================

echo.
echo ╔════════════════════════════════════════╗
echo ║   FACILITA - BUILD E DEPLOY            ║
echo ╚════════════════════════════════════════╝
echo.

:MENU
echo Escolha uma opcao:
echo.
echo [1] Limpar projeto (Clean)
echo [2] Compilar Debug
echo [3] Instalar no dispositivo
echo [4] Compilar e Instalar
echo [5] Ver logs do app
echo [6] Ver dispositivos conectados
echo [7] Sair
echo.
set /p opcao="Digite o numero da opcao: "

if "%opcao%"=="1" goto CLEAN
if "%opcao%"=="2" goto BUILD
if "%opcao%"=="3" goto INSTALL
if "%opcao%"=="4" goto BUILD_INSTALL
if "%opcao%"=="5" goto LOGS
if "%opcao%"=="6" goto DEVICES
if "%opcao%"=="7" goto END

echo Opcao invalida!
goto MENU

:CLEAN
echo.
echo ===== LIMPANDO PROJETO =====
call .\gradlew.bat clean
echo.
echo ✓ Projeto limpo!
pause
goto MENU

:BUILD
echo.
echo ===== COMPILANDO DEBUG =====
call .\gradlew.bat assembleDebug
echo.
echo ✓ Compilacao concluida!
pause
goto MENU

:INSTALL
echo.
echo ===== INSTALANDO NO DISPOSITIVO =====
call .\gradlew.bat installDebug
echo.
echo ✓ App instalado!
pause
goto MENU

:BUILD_INSTALL
echo.
echo ===== COMPILANDO E INSTALANDO =====
call .\gradlew.bat clean assembleDebug installDebug
echo.
echo ✓ Build e instalacao concluidos!
pause
goto MENU

:LOGS
echo.
echo ===== EXIBINDO LOGS (Ctrl+C para sair) =====
adb logcat | findstr "Facilita TelaChat WebSocket"
pause
goto MENU

:DEVICES
echo.
echo ===== DISPOSITIVOS CONECTADOS =====
adb devices -l
echo.
pause
goto MENU

:END
echo.
echo Ate logo!
exit

