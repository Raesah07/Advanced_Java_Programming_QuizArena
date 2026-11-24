param([Parameter(ValueFromRemainingArguments = $true)][string[]]$MavenArgs)
$ErrorActionPreference = 'Stop'
$projectRoot = (Get-Location).Path
cmd.exe /c "subst X: /d" 2>$null
cmd.exe /c "subst X: $projectRoot"
try {
  Set-Location X:\
  if (Test-Path .env) { Get-Content .env | Where-Object { $_ -match '^[^#].+=' } | ForEach-Object { $name,$value=$_.Split('=',2); Set-Item -Path "Env:$name" -Value $value } }
  $env:JAVA_HOME = 'C:\Program Files\Java\jdk-24'
  $env:MAVEN_HOME = 'X:\.tools\apache-maven-3.9.16'
  & 'X:\.tools\apache-maven-3.9.16\bin\mvn.cmd' @MavenArgs
  exit $LASTEXITCODE
} finally { cmd.exe /c "subst X: /d" 2>$null }
