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

# 解析版本配置文件并转换为 Gradle 参数
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

# 根据操作系统选择 Gradle Wrapper
$gradle = if ($IsWindows -or $env:OS -eq 'Windows_NT') {
    Join-Path $repositoryRoot 'gradlew.bat'
} else {
    Join-Path $repositoryRoot 'gradlew'
}

# 1. 【核心修复】预热 Loom/NeoForge 资产
# 在 clean 和 build 之前，先下载并合并 Minecraft 依赖，防止 remapMinecraftIntermediary 找不到文件
Write-Host "Pre-warming Loom/NeoForge assets for $Profile..."
& $gradle '-p' $projectDirectory 'downloadAssets' '--no-daemon' '--console=plain'
if ($LASTEXITCODE -ne 0) {
    Write-Warning "Asset pre-warming failed with exit code $LASTEXITCODE. The main build might fail."
}

# 2. 组装并执行主构建任务
$tasks = @()
if (-not $NoClean) {
    $tasks += 'clean'
}
$tasks += $Task

Write-Host "Building GOML for Minecraft $Profile ($Task) from $projectDirectory"
& $gradle '-p' $projectDirectory @tasks @gradleProperties '--no-daemon' '--console=plain' '--stacktrace'
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

# 3. 收集并复制构建产物
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
    Write-Host "Artifacts successfully copied to: $artifactDirectory"
}