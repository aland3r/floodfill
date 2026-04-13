$base = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $base

Remove-Item -Force -ErrorAction SilentlyContinue -Path @(
    'saida_pilha.png',
    'saida_fila.png'
)
if (Test-Path 'saida_animacao') {
    Remove-Item -Recurse -Force 'saida_animacao'
}

Write-Host 'OK: saida_pilha.png, saida_fila.png e pasta saida_animacao removidos (se existiam).'
