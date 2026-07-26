param(
    [ValidateSet('build', 'assemble', 'compileJava', 'runGameTestServer')]
    [string]$Task = 'build'
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = Split-Path -Parent $PSScriptRoot
$outputRoot = Join-Path $repositoryRoot 'build/multiversion'
$profiles = Get-ChildItem -LiteralPath (Join-Path $repositoryRoot 'versions') -Filter '*.properties' |
        Sort-Object BaseName
$gradle = if ($IsWindows -or $env:OS -eq 'Windows_NT') {
    Join-Path $repositoryRoot 'gradlew.bat'
} else {
    Join-Path $repositoryRoot 'gradlew'
}

& $gradle clean '--no-daemon' '--console=plain'
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

foreach ($profile in $profiles) {
    & (Join-Path $PSScriptRoot 'build-version.ps1') -Profile $profile.BaseName -Task $Task -NoClean
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }

    if ($Task -in @('build', 'assemble')) {
        $profileOutput = Join-Path $outputRoot $profile.BaseName
        New-Item -ItemType Directory -Force -Path $profileOutput | Out-Null
        Get-ChildItem -LiteralPath (Join-Path $repositoryRoot 'build/libs') -Filter '*.jar' |
                Copy-Item -Destination $profileOutput -Force
    }
}

if ($Task -in @('build', 'assemble')) {
    Write-Host "Multi-version artifacts: $outputRoot"
}
