# Builds one jar per supported Minecraft version group into dist.
# Needs JDK 25 to run Gradle (set JAVA_HOME). The 1.21.x jars still target Java 21.
#
# Each jar is compiled against the OLDEST version of its range so it only uses calls that exist in the whole range.
$ErrorActionPreference = 'Stop'

$targets = @(
    @{ lane = 'yarn';     mc = '1.21';    yarn = '1.21+build.9';    fabric = '0.102.0+1.21';    compat = 'y1'; range = '>=1.21 <1.21.2';       extra = '';                  java = 21 },
    @{ lane = 'yarn';     mc = '1.21.2';  yarn = '1.21.2+build.1';  fabric = '0.106.1+1.21.2';  compat = 'y1'; range = '>=1.21.2 <1.21.9';     extra = '';                  java = 21 },
    @{ lane = 'yarn';     mc = '1.21.9';  yarn = '1.21.9+build.1';  fabric = '0.134.1+1.21.9';  compat = 'y4'; range = '>=1.21.9 <1.21.10';    extra = 'WorldRendererMixin'; java = 21 },
    @{ lane = 'yarn';     mc = '1.21.10'; yarn = '1.21.10+build.3'; fabric = '0.138.4+1.21.10'; compat = 'y3'; range = '>=1.21.10 <=1.21.11';  extra = '';                  java = 21 },
    @{ lane = 'official'; mc = '26.1';    yarn = '';                fabric = '0.145.1+26.1';    compat = 'o1'; range = '>=26.1 <26.2';         extra = '';                  java = 25 },
    @{ lane = 'official'; mc = '26.2';    yarn = '';                fabric = '0.161.0+26.2';    compat = 'o2'; range = '>=26.2 <26.4';         extra = '';                  java = 25 }
)

$out = Join-Path $PSScriptRoot 'dist'
New-Item -ItemType Directory -Force $out | Out-Null
Get-ChildItem $out -Filter *.jar | Remove-Item

foreach ($t in $targets) {
    Write-Host "=== Minecraft $($t.mc) ($($t.lane), compat $($t.compat))"
    $gradleArgs = @(
        'clean', 'build',
        "-Plane=$($t.lane)", "-Pminecraft_version=$($t.mc)", "-Pfabric_version=$($t.fabric)",
        "-Pcompat=$($t.compat)", "-Pmc_range=$($t.range)", "-Pjava_version=$($t.java)",
        "-Pextra_mixin=$($t.extra)"
    )
    if ($t.yarn) { $gradleArgs += "-Pyarn_mappings=$($t.yarn)" }
    & "$PSScriptRoot/gradlew.bat" @gradleArgs
    if ($LASTEXITCODE -ne 0) { throw "Build failed for Minecraft $($t.mc)" }
    Get-ChildItem (Join-Path $PSScriptRoot 'build/libs') -Filter *.jar |
        Where-Object { $_.Name -notlike '*-sources.jar' -and $_.Name -notlike '*-dev.jar' } |
        Copy-Item -Destination $out
}

Write-Host "Done. Jars:"
Get-ChildItem $out -Filter *.jar | ForEach-Object { Write-Host "  $($_.Name)" }
