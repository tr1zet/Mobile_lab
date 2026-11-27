param([switch]$DeviceTests)

$ErrorActionPreference = 'Stop'
$projectPath = $PSScriptRoot
$previousJava = $env:JAVA_HOME
$previousGradle = $env:GRADLE_USER_HOME
$drive = $null
$pushed = $false
$mapped = $false
try {
    # An ASCII path also keeps Gradle's test-worker argument files portable on Windows.
    foreach ($letter in @('Z', 'Y', 'X', 'W', 'V', 'U', 'T', 'S', 'Q', 'P')) {
        if (-not (Test-Path ($letter + ':\'))) { $drive = $letter + ':'; break }
    }
    if ($null -eq $drive) { throw 'Нет свободной буквы диска для временного пути к проекту.' }
    & subst $drive $projectPath
    if ($LASTEXITCODE -ne 0) { throw 'Не удалось создать временный путь к проекту.' }
    $mapped = $true
    $jdk = Join-Path $env:ProgramFiles 'Java\jdk-21'
    if (Test-Path (Join-Path $jdk 'bin\java.exe')) { $env:JAVA_HOME = $jdk }
    if (-not $env:JAVA_HOME -or -not (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
        throw 'Установите JDK 17 или 21 и укажите его каталог в JAVA_HOME.'
    }
    Push-Location ($drive + '\')
    $pushed = $true
    $env:GRADLE_USER_HOME = $drive + '\.gradle-user-home'
    $tasks = @(':app:clean', ':app:assembleDebug', ':app:testDebugUnitTest', ':app:lintDebug')
    if ($DeviceTests) { $tasks += ':app:connectedDebugAndroidTest' }
    & .\gradlew.bat @tasks --no-daemon --max-workers=2 --console=plain
    $result = $LASTEXITCODE
} finally {
    if ($pushed) { Pop-Location }
    $env:JAVA_HOME = $previousJava
    $env:GRADLE_USER_HOME = $previousGradle
    if ($mapped) { & subst $drive /d }
}
exit $result
