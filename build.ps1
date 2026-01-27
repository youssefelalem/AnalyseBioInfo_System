# Build and Deploy Script for AnalyseBioInfo_System
# Usage: .\build.ps1

$ErrorActionPreference = "Stop"

# Configuration
$PROJECT_DIR = $PSScriptRoot
$SRC_DIR = "$PROJECT_DIR\src\main\java"
$WEBAPP_DIR = "$PROJECT_DIR\src\main\webapp"
$BUILD_DIR = "$PROJECT_DIR\target"
$CLASSES_DIR = "$BUILD_DIR\WEB-INF\classes"
$TOMCAT_HOME = "C:\apache-tomcat-10.1.50"
$TOMCAT_WEBAPPS = "$TOMCAT_HOME\webapps"
$APP_NAME = "AnalyseBioInfo_System"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Building AnalyseBioInfo_System" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Step 1: Clean previous build
Write-Host "`n[1/5] Cleaning previous build..." -ForegroundColor Yellow
if (Test-Path $BUILD_DIR) {
    Remove-Item -Recurse -Force $BUILD_DIR
}
New-Item -ItemType Directory -Force -Path $CLASSES_DIR | Out-Null

# Step 2: Copy webapp files
Write-Host "[2/5] Copying webapp files..." -ForegroundColor Yellow
Copy-Item -Recurse "$WEBAPP_DIR\*" "$BUILD_DIR\" -Force

# Step 3: Find Jakarta Servlet API
Write-Host "[3/5] Locating Jakarta Servlet API..." -ForegroundColor Yellow
$SERVLET_API = "$TOMCAT_HOME\lib\servlet-api.jar"
if (-not (Test-Path $SERVLET_API)) {
    $SERVLET_API = "$TOMCAT_HOME\lib\jakarta.servlet-api.jar"
}
if (-not (Test-Path $SERVLET_API)) {
    Write-Host "ERROR: Cannot find servlet-api.jar in Tomcat lib!" -ForegroundColor Red
    Write-Host "Please check: $TOMCAT_HOME\lib\" -ForegroundColor Red
    exit 1
}
Write-Host "   Found: $SERVLET_API" -ForegroundColor Green

# Step 4: Compile Java files
Write-Host "[4/5] Compiling Java files..." -ForegroundColor Yellow
$JAVA_FILES = Get-ChildItem -Path $SRC_DIR -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
$MYSQL_JAR = "$WEBAPP_DIR\WEB-INF\lib\mysql-connector-j-9.6.0.jar"
$CLASSPATH = "$SERVLET_API;$MYSQL_JAR"

$javaFilesList = $JAVA_FILES -join '" "'
$compileCmd = "javac -encoding UTF-8 -d `"$CLASSES_DIR`" -cp `"$CLASSPATH`" `"$javaFilesList`""

Write-Host "   Compiling $($JAVA_FILES.Count) Java files..." -ForegroundColor Gray
Invoke-Expression $compileCmd

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Compilation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "   Compilation successful!" -ForegroundColor Green

# Step 5: Deploy to Tomcat
Write-Host "[5/5] Deploying to Tomcat..." -ForegroundColor Yellow
$DEPLOY_DIR = "$TOMCAT_WEBAPPS\$APP_NAME"

if (Test-Path $DEPLOY_DIR) {
    Remove-Item -Recurse -Force $DEPLOY_DIR
}
Copy-Item -Recurse $BUILD_DIR $DEPLOY_DIR

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "  BUILD SUCCESSFUL!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "`nNext steps:" -ForegroundColor Cyan
Write-Host "1. Start Tomcat: $TOMCAT_HOME\bin\startup.bat" -ForegroundColor White
Write-Host "2. Open browser: http://localhost:8080/$APP_NAME/" -ForegroundColor White
Write-Host ""
