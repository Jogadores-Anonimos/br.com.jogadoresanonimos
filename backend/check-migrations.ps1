# Verifica se ha migration nova em backend/src/main/resources/db/migration
# que ainda nao foi aplicada no container Postgres do docker compose.
#
# Motivo: o container backend (Dockerfile.dev) roda "mvnw spring-boot:run"
# uma unica vez no "docker compose up". Como backend/src e bind mount, editar
# arquivos no host (inclusive uma migration nova) NAO reinicia a JVM sozinho,
# entao o Flyway nao roda de novo ate um restart/rebuild do container. Isso
# gera schema desatualizado e erros confusos (500 generico, dado ausente).
#
# Uso: .\check-migrations.ps1   (rodar a partir de backend/, com docker compose up)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

$migrationDir = Join-Path $scriptDir "src\main\resources\db\migration"
$composeFile = Join-Path $scriptDir "..\docker-compose.yml"
$envFile = Join-Path $scriptDir "..\.env"

if (-not (Test-Path $migrationDir)) {
    Write-Error "Diretorio de migrations nao encontrado: $migrationDir"
    exit 1
}

$dbName = "jogadoresanonimos"
$dbUsername = "postgres"
if (Test-Path $envFile) {
    $envContent = Get-Content $envFile
    $dbNameLine = $envContent | Where-Object { $_ -match '^DB_NAME=' } | Select-Object -First 1
    $dbUserLine = $envContent | Where-Object { $_ -match '^DB_USERNAME=' } | Select-Object -First 1
    if ($dbNameLine) { $dbName = ($dbNameLine -split '=', 2)[1] }
    if ($dbUserLine) { $dbUsername = ($dbUserLine -split '=', 2)[1] }
}

$query = "select version from flyway_schema_history where success = true order by installed_rank;"
$applied = docker compose -f $composeFile exec -T postgres psql -U $dbUsername -d $dbName -tAc $query 2>$null

if (-not $applied) {
    Write-Error "Nao foi possivel consultar flyway_schema_history. O container postgres esta rodando (docker compose up)?"
    exit 2
}

$appliedVersions = $applied -split "`n" | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne "" }

$pending = @()
Get-ChildItem -Path $migrationDir -Filter "V*.sql" | Sort-Object Name | ForEach-Object {
    if ($_.Name -match '^V([0-9]+(_[0-9]+)*)__') {
        $version = $Matches[1]
        if ($appliedVersions -notcontains $version) {
            $pending += $_.Name
        }
    }
}

if ($pending.Count -eq 0) {
    Write-Host "OK: schema do container esta em dia com as migrations em $migrationDir."
    exit 0
} else {
    Write-Host "ATENCAO: existem migrations no host ainda NAO aplicadas no container:"
    foreach ($f in $pending) {
        Write-Host "  - $f"
    }
    Write-Host ""
    Write-Host "Rode: docker compose up -d --build backend"
    exit 3
}
