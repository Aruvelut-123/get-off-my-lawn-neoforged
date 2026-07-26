param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[0-9][0-9A-Za-z.-]*$')]
    [string]$Profile,

    [ValidateSet('build', 'assemble', 'compileJava', 'runGameTestServer')]
    [string]$Task = 'build',

    [switch]$NoClean
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = Split-Path -Parent $PSScriptRoot
$profileFile = Join-Path $repositoryRoot "versions/$Profile.properties"

if (-not (Test-Path -LiteralPath $profileFile -PathType Leaf)) {
    throw "Unknown Minecraft version profile '$Profile' (expected $profileFile)"
}

$gradleProperties = @()
foreach ($line in Get-Content -LiteralPath $profileFile) {
    $trimmed = $line.Trim()
    if ($trimmed.Length -eq 0 -or $trimmed.StartsWith('#')) {
        continue
    }

    $separator = $trimmed.IndexOf('=')
    if ($separator -lt 1) {
        throw "Invalid profile line: $line"
    }

    $key = $trimmed.Substring(0, $separator).Trim()
    $value = $trimmed.Substring($separator + 1).Trim()
    $gradleProperties += "-P$key=$value"
}

$gradle = if ($IsWindows -or $env:OS -eq 'Windows_NT') {
    Join-Path $repositoryRoot 'gradlew.bat'
} else {
    Join-Path $repositoryRoot 'gradlew'
}

$tasks = @()
if (-not $NoClean) {
    $tasks += 'clean'
}
$tasks += $Task

Write-Host "Building GOML for Minecraft $Profile ($Task)"
& $gradle @tasks @gradleProperties '--no-daemon' '--console=plain'
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}
