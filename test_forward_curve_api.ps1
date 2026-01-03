# Forward Curve API Test Script for PowerShell
# Run this from PowerShell to test the Forward Curve APIs

$baseUrl = "http://localhost:8080"

Write-Host "`n=== Forward Curve API Testing ===" -ForegroundColor Cyan

# Test 1: Get all instruments with curves
Write-Host "`n1. Getting all instruments with forward curves..." -ForegroundColor Yellow
try {
    $instruments = Invoke-RestMethod -Uri "$baseUrl/api/forward-curves/instruments" -Method Get
    Write-Host "Instruments with curves: $($instruments -join ', ')" -ForegroundColor Green
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

# Test 2: Get all curves for PWR-Q1-25
Write-Host "`n2. Getting all forward curves for PWR-Q1-25..." -ForegroundColor Yellow
try {
    $curves = Invoke-RestMethod -Uri "$baseUrl/api/forward-curves/instrument/PWR-Q1-25" -Method Get
    Write-Host "Found $($curves.Count) curve points" -ForegroundColor Green
    $curves | Select-Object -First 3 | Format-Table
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

# Test 3: Get specific curve point
Write-Host "`n3. Getting specific curve point (PWR-Q1-25, 2026-01-02)..." -ForegroundColor Yellow
try {
    $curve = Invoke-RestMethod -Uri "$baseUrl/api/forward-curves?instrumentCode=PWR-Q1-25&deliveryDate=2026-01-02" -Method Get
    Write-Host "Curve point:" -ForegroundColor Green
    $curve | Format-List
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

# Test 4: Create new curve point
Write-Host "`n4. Creating new curve point..." -ForegroundColor Yellow
$newCurve = @{
    instrumentCode = "PWR-Q1-25"
    deliveryDate = "2026-01-20"
    price = 64.50
} | ConvertTo-Json

try {
    $result = Invoke-RestMethod -Uri "$baseUrl/api/forward-curves" -Method Post -Body $newCurve -ContentType "application/json"
    Write-Host "Successfully created curve point:" -ForegroundColor Green
    $result | Format-List
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

# Test 5: Bulk upload
Write-Host "`n5. Bulk uploading curves..." -ForegroundColor Yellow
$bulkCurves = @(
    @{
        instrumentCode = "PWR-Q1-25"
        deliveryDate = "2026-01-21"
        price = 65.00
    },
    @{
        instrumentCode = "PWR-Q1-25"
        deliveryDate = "2026-01-22"
        price = 65.25
    }
) | ConvertTo-Json

try {
    $bulkResult = Invoke-RestMethod -Uri "$baseUrl/api/forward-curves/bulk" -Method Post -Body $bulkCurves -ContentType "application/json"
    Write-Host "Bulk upload result:" -ForegroundColor Green
    $bulkResult | Format-List
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

# Test 6: Calculate P&L for trade
Write-Host "`n6. Testing P&L calculation for trade TRD-20260103-0001..." -ForegroundColor Yellow
try {
    $pnl = Invoke-RestMethod -Uri "$baseUrl/api/trades/TRD-20260103-0001/pnl" -Method Get
    Write-Host "P&L: $pnl" -ForegroundColor Green
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

# Test 7: Delete curve point
Write-Host "`n7. Deleting test curve point (2026-01-20)..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/api/forward-curves?instrumentCode=PWR-Q1-25&deliveryDate=2026-01-20" -Method Delete
    Write-Host "Successfully deleted curve point" -ForegroundColor Green
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

Write-Host "`n=== Testing Complete ===" -ForegroundColor Cyan
Write-Host "`nNote: Make sure the application is running on port 8080" -ForegroundColor Yellow
