@echo off
echo Retro Encryption Tool
echo =====================

if "%~1"=="" (
    echo Starting GUI mode...
    java EncryptionTool
) else (
    echo Starting command-line mode...
    java EncryptionTool %*
)