param([string]$Python = "python", [string]$Bind = "127.0.0.1:8000")
$ErrorActionPreference = "Stop"
Push-Location (Join-Path $PSScriptRoot "backend")
try {
    & $Python -m venv .venv
    if ($LASTEXITCODE -ne 0) { throw "No se pudo crear el entorno Python." }
    $venvPython = Join-Path $PWD ".venv/Scripts/python.exe"
    & $venvPython -m pip install -r requirements.txt
    if ($LASTEXITCODE -ne 0) { throw "No se pudieron instalar las dependencias." }
    & $venvPython manage.py migrate
    if ($LASTEXITCODE -ne 0) { throw "Fallaron las migraciones." }
    & $venvPython manage.py seed_demo
    & $venvPython manage.py runserver $Bind
} finally { Pop-Location }
