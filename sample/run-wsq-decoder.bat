@echo off
REM Decode WSQ (.wsq) samples. Package sample first: mvn clean package -DskipTests -Dgpg.skip=true
setlocal
set "DIR=%~dp0"
if exist "%DIR%target\sample-imagedecoder-*.jar" (
  pushd "%DIR%target"
) else (
  pushd "%DIR%"
)
set "JAR="
for %%F in (sample-imagedecoder-*.jar) do (
  echo %%~nxF | findstr /I /C:"sources" /C:"javadoc" /C:"with-dependencies" >nul
  if errorlevel 1 set "JAR=%%~fF"
)
if not defined JAR (
  echo error: sample jar not found. Package sample first ^(mvn clean package -DskipTests -Dgpg.skip=true^).
  popd
  exit /b 1
)
if not exist "lib" (
  echo error: lib\ missing. Package sample first ^(mvn clean package -DskipTests -Dgpg.skip=true^).
  popd
  exit /b 1
)
java -cp "%JAR%;lib\*" io.mosip.imagedecoder.sample.SampleImageDecoderApplication "io.mosip.imagedecoder.image.type=1" "io.mosip.imagedecoder.image.folder.path=/BiometricInfo"
set "RC=%ERRORLEVEL%"
popd
exit /b %RC%
