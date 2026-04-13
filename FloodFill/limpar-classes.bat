@echo off
cd /d "%~dp0"
if exist "src\*.class" (
  del /q "src\*.class"
  echo Removidos os .class em src\
) else (
  echo Nenhum .class em src\
)
