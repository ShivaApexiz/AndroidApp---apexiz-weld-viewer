$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$sourceIndex = Join-Path $projectRoot "index.html"
$sourceBuilderCss = Join-Path $projectRoot "mobile-builder.css"
$sourceBuilderJs = Join-Path $projectRoot "mobile-builder.js"
$sourceLogo = Join-Path $projectRoot "Logo.png"
$webDir = Join-Path $projectRoot "web"
$targetIndex = Join-Path $webDir "index.html"
$targetBuilderCss = Join-Path $webDir "mobile-builder.css"
$targetBuilderJs = Join-Path $webDir "mobile-builder.js"
$targetLogo = Join-Path $webDir "Logo.png"

if (-not (Test-Path -LiteralPath $sourceIndex)) {
  throw "Source file not found: $sourceIndex"
}
if (-not (Test-Path -LiteralPath $sourceBuilderCss)) {
  throw "Source file not found: $sourceBuilderCss"
}
if (-not (Test-Path -LiteralPath $sourceBuilderJs)) {
  throw "Source file not found: $sourceBuilderJs"
}
if (-not (Test-Path -LiteralPath $sourceLogo)) {
  throw "Source file not found: $sourceLogo"
}

New-Item -ItemType Directory -Force -Path $webDir | Out-Null
Copy-Item -LiteralPath $sourceIndex -Destination $targetIndex -Force
Copy-Item -LiteralPath $sourceBuilderCss -Destination $targetBuilderCss -Force
Copy-Item -LiteralPath $sourceBuilderJs -Destination $targetBuilderJs -Force
Copy-Item -LiteralPath $sourceLogo -Destination $targetLogo -Force

Write-Host "Synced web assets to $webDir"
