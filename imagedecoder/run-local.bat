@echo off
setlocal EnableExtensions EnableDelayedExpansion
REM Standalone Windows cmd runner for imagedecoder library (no .ps1).
REM Linux / macOS / Git Bash: use run-local.sh
REM
REM   run-local.bat init | test | all

set "MODULE_DIR=%~dp0"
if "%MODULE_DIR:~-1%"=="\" set "MODULE_DIR=%MODULE_DIR:~0,-1%"
set "REPO_ROOT=%MODULE_DIR%\.."

set "LOCAL_DIR=%MODULE_DIR%\.local"
set "LOG_DIR=%LOCAL_DIR%\logs"
set "MODULE=imagedecoder"

set "CMD=%~1"
if "%CMD%"=="" goto :usage
if /I "%CMD%"=="-h" goto :usage
if /I "%CMD%"=="--help" goto :usage
if /I "%CMD%"=="help" goto :usage
if /I "%CMD%"=="init" goto :init
if /I "%CMD%"=="test" goto :test
if /I "%CMD%"=="all" goto :all

echo error: unknown command '%CMD%'
goto :usage

:usage
echo Local imagedecoder library
echo.
echo   run-local.bat init     install parent + package this module ^(skip tests^)
echo   run-local.bat test     Maven unit tests
echo   run-local.bat all      init + test
echo.
echo Run from imagedecoder\
exit /b 1

:ensure_dirs
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
exit /b 0

:check_prereqs
where java >nul 2>&1
if errorlevel 1 (
  echo error: java is required on PATH
  exit /b 1
)
where mvn >nul 2>&1
if errorlevel 1 (
  echo error: mvn is required on PATH
  exit /b 1
)
exit /b 0

:init
call :check_prereqs
if errorlevel 1 exit /b 1
call :ensure_dirs
echo ==^> packaging %MODULE% ^(skip tests^)
pushd "%REPO_ROOT%"
call mvn clean install -DskipTests "-Dgpg.skip=true" "-Dmaven.javadoc.skip=true" -pl imagedecoder -am
set "RC=%ERRORLEVEL%"
popd
if not "%RC%"=="0" exit /b %RC%
echo init complete
exit /b 0

:test
call :check_prereqs
if errorlevel 1 exit /b 1
echo ==^> maven tests
pushd "%REPO_ROOT%"
call mvn test "-Dgpg.skip=true" "-Dmaven.javadoc.skip=true" -pl imagedecoder
set "RC=%ERRORLEVEL%"
popd
exit /b %RC%

:all
echo ==^> all: init + test
call :init
if errorlevel 1 exit /b 1
call :test
exit /b %ERRORLEVEL%
