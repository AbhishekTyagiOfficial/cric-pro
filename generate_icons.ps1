Add-Type -AssemblyName System.Drawing

$srcPath = "C:\Users\abhis\.gemini\antigravity\brain\a2a0cff8-c0b7-484b-bc5a-7c51f3b9cde4\cricpro_app_icon_1789477201970.jpg"
$resDir = "C:\Users\abhis\.gemini\antigravity\scratch\cric-pro\app\src\main\res"

$srcImage = [System.Drawing.Image]::FromFile($srcPath)

$densities = [ordered]@{
    "mipmap-mdpi" = 48
    "mipmap-hdpi" = 72
    "mipmap-xhdpi" = 96
    "mipmap-xxhdpi" = 144
    "mipmap-xxxhdpi" = 192
}

foreach ($key in $densities.Keys) {
    $size = $densities[$key]
    $folderPath = Join-Path $resDir $key
    if (-not (Test-Path $folderPath)) {
        New-Item -ItemType Directory -Path $folderPath | Out-Null
    }
    
    $destBitmap = New-Object System.Drawing.Bitmap($size, $size)
    $graphics = [System.Drawing.Graphics]::FromImage($destBitmap)
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.DrawImage($srcImage, 0, 0, $size, $size)
    
    $destBitmap.Save((Join-Path $folderPath "ic_launcher.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $destBitmap.Save((Join-Path $folderPath "ic_launcher_round.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    
    $graphics.Dispose()
    $destBitmap.Dispose()
}

# Also save 432x432 foreground PNG in drawable
$drawablePath = Join-Path $resDir "drawable"
if (-not (Test-Path $drawablePath)) {
    New-Item -ItemType Directory -Path $drawablePath | Out-Null
}
$fgBitmap = New-Object System.Drawing.Bitmap(432, 432)
$fgGraphics = [System.Drawing.Graphics]::FromImage($fgBitmap)
$fgGraphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$fgGraphics.DrawImage($srcImage, 0, 0, 432, 432)
$fgBitmap.Save((Join-Path $drawablePath "ic_launcher_foreground.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$fgGraphics.Dispose()
$fgBitmap.Dispose()

$srcImage.Dispose()
Write-Output "Generated PNG app icons and ic_launcher_foreground.png successfully!"
