param(
    [ValidateSet('build', 'assemble', 'compileJava', 'runServer', 'runGameTestServer')]
    [string]$Task = 'build'
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = Split-Path -Parent $PSScriptRoot
$profiles = Get-ChildItem -LiteralPath (Join-Path $repositoryRoot 'versions') -Filter '*.properties' |
        Sort-Object BaseName
$gradle = if ($IsWindows -or $env:OS -eq 'Windows_NT') {
    Join-Path $repositoryRoot 'gradlew.bat'
} else {
    Join-Path $repositoryRoot 'gradlew'
}

$projectDirectories = @(
    $repositoryRoot
    Join-Path $repositoryRoot 'platforms/neoforge-1.21.1'
)

foreach ($projectDirectory in $projectDirectories) {
    & $gradle '-p' $projectDirectory clean '--no-daemon' '--console=plain'
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}

foreach ($profile in $profiles) {
    & (Join-Path $PSScriptRoot 'build-version.ps1') -Profile $profile.BaseName -Task $Task -NoClean
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}

if ($Task -in @('build', 'assemble')) {
    $outputRoot = Join-Path $repositoryRoot 'build/multiversion'
    Write-Host "Multi-version artifacts: $outputRoot"
}
