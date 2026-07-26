param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[0-9][0-9A-Za-z.-]*$')]
    [string]$Profile,

    [ValidateSet('build', 'assemble', 'compileJava', 'runServer', 'runGameTestServer')]
    [string]$Task = 'build',

    [switch]$NoClean
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = Split-Path -Parent $PSScriptRoot
$profileFile = Join-Path $repositoryRoot "versions/$Profile.properties"
$projectDirectory = if ($Profile -eq '1.21.1') {
    Join-Path $repositoryRoot 'platforms/neoforge-1.21.1'
} else {
    $repositoryRoot
}

if (-not (Test-Path -LiteralPath $profileFile -PathType Leaf)) {
    throw "Unknown Minecraft version profile '$Profile' (expected $profileFile)"
}

$profileProperties = @{}
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
    $profileProperties[$key] = $value
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

Write-Host "Building GOML for Minecraft $Profile ($Task) from $projectDirectory"
& $gradle '-p' $projectDirectory @tasks @gradleProperties '--no-daemon' '--console=plain'
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

if ($Task -in @('build', 'assemble')) {
    $libraryDirectory = Join-Path $projectDirectory 'build/libs'
    $artifactDirectory = Join-Path $repositoryRoot "build/multiversion/$Profile"
    New-Item -ItemType Directory -Force -Path $artifactDirectory | Out-Null

    $expectedSuffix = "+$($profileProperties['minecraft_version'])+neoforge.jar"
    $artifacts = @(
        Get-ChildItem -LiteralPath $libraryDirectory -Filter '*.jar' |
                Where-Object { $_.Name.EndsWith($expectedSuffix, [StringComparison]::OrdinalIgnoreCase) }
    )
    if ($artifacts.Count -eq 0) {
        throw "No release artifact found in $libraryDirectory"
    }

    $artifacts | Copy-Item -Destination $artifactDirectory -Force
    Write-Host "Artifacts: $artifactDirectory"
}
