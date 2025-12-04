@echo off
title 图书馆管理系统启动器
echo ==========================================
echo           图书馆管理系统
echo ==========================================
echo.
echo 正在启动应用...
echo 端口: 8083
echo.
cd /d "%~dp0"
java -jar target\library-management-system-1.0.0.jar --server.port=8083

echo.
echo 应用已停止，按任意键退出...
pause >nul