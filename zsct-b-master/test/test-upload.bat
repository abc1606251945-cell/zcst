@echo off
chcp 65001 >nul
echo ========================================
echo 测试文件上传功能
echo ========================================
echo.

REM 设置图片路径（请修改为你自己的图片路径）
set IMAGE_PATH=D:\test-image.jpg

echo 提示：请修改下面的 IMAGE_PATH 为你的图片路径
echo 当前配置的图片路径：%IMAGE_PATH%
echo.

REM 检查图片是否存在
if not exist "%IMAGE_PATH%" (
    echo 错误：图片文件不存在：%IMAGE_PATH%
    echo.
    echo 请按照以下步骤操作：
    echo 1. 编辑这个文件（test-upload.bat）
    echo 2. 找到 IMAGE_PATH 这一行
    echo 3. 修改为你实际的图片路径
    echo 4. 重新运行这个脚本
    echo.
    pause
    exit /b 1
)

echo 准备上传图片...
echo 图片路径：%IMAGE_PATH%
echo 提示：学号会自动从登录 token 中获取
echo 上传地址：http://localhost:8081/upload/schedule
echo.

REM 需要设置 token（从登录接口获取）
set TOKEN=your_jwt_token_here

echo 提示：请先修改上面的 TOKEN 为你的登录 token
echo.
pause

echo 正在上传，请稍候...
echo.

curl.exe -X POST http://localhost:8081/upload/schedule ^
  -F "file=@%IMAGE_PATH%" ^
  -F "studentId=S001" ^
  -v

echo.
echo ========================================
echo 测试完成
echo ========================================
echo.
echo 提示：
echo - 如果看到 "code": 200 表示上传成功
echo - 可以在阿里云 OSS 控制台查看上传的文件
echo - Bucket 名称：zcsst-student-timetable
echo.
pause
