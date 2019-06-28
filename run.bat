@echo off
set NAME="ProtectRegion"
set VERSION="1.0"
md "server\plugins\%NAME%"
copy /Y "target\%NAME%-%VERSION%.jar" "server\plugins\"
cd server
.\start.bat
