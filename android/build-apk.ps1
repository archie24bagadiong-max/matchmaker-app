# Builds MatchMaker.apk from this folder + ..\index.html without Gradle.
# Needs: JDK (javac, keytool), Android SDK platform 34 + build-tools 34.0.0 in %LOCALAPPDATA%\Android\Sdk.
# The signing key lives OUTSIDE the repo (D:\Codes\matchmaker-keys) — the repo is public. Back that folder up:
# every future APK must be signed with the same key to install over an earlier one.
param(
  [int]$VersionCode = 12,
  [string]$VersionName = '1.11',
  [string]$KeyDir = 'D:\Codes\matchmaker-keys',
  [string]$Out = 'D:\Codes\matchmaker-build'
)
$ErrorActionPreference = 'Stop'
$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$repo = Split-Path -Parent $here
$sdk  = "$env:LOCALAPPDATA\Android\Sdk"
$bt   = "$sdk\build-tools\34.0.0"
$jar  = "$sdk\platforms\android-34\android.jar"
$w    = "$Out\work"

if (Test-Path $w) { Remove-Item -Recurse -Force $w }
New-Item -ItemType Directory -Force "$w\res\values", "$w\res\mipmap-xxxhdpi", "$w\assets", "$w\src\com\badminton\matchmaker", "$w\gen", "$w\classes", "$w\dex" | Out-Null

# resources: app name + launcher icon (192 px = xxxhdpi launcher size)
Set-Content -Encoding utf8 "$w\res\values\strings.xml" '<?xml version="1.0" encoding="utf-8"?><resources><string name="app_name">Match Maker</string></resources>'
Copy-Item "$repo\icons\icon-192.png" "$w\res\mipmap-xxxhdpi\ic_launcher.png"

# assets: the app page (latest) and the Excel library used offline
Copy-Item "$repo\index.html" "$w\assets\index.html"
if (-not (Test-Path "$Out\assets\xlsx.full.min.js")) {
  New-Item -ItemType Directory -Force "$Out\assets" | Out-Null
  Invoke-WebRequest -UseBasicParsing 'https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js' -OutFile "$Out\assets\xlsx.full.min.js"
}
Copy-Item "$Out\assets\xlsx.full.min.js" "$w\assets\"
# manifest with this build's version
$man = Get-Content -Raw "$here\AndroidManifest.xml"
$man = $man -replace 'android:versionCode="\d+"', "android:versionCode=`"$VersionCode`"" -replace 'android:versionName="[^"]*"', "android:versionName=`"$VersionName`""
[IO.File]::WriteAllText("$w\AndroidManifest.xml", $man, (New-Object Text.UTF8Encoding $false))

# 1. resources -> base apk + R.java
& "$bt\aapt2.exe" compile --dir "$w\res" -o "$w\res.zip"; if ($LASTEXITCODE) { throw 'aapt2 compile failed' }
& "$bt\aapt2.exe" link -o "$w\base.apk" -I $jar --manifest "$w\AndroidManifest.xml" -A "$w\assets" --java "$w\gen" --min-sdk-version 24 --target-sdk-version 34 "$w\res.zip"; if ($LASTEXITCODE) { throw 'aapt2 link failed' }

# 2. java -> classes -> dex
Copy-Item "$here\*.java" "$w\src\com\badminton\matchmaker\"
$srcs = @(Get-ChildItem -Recurse "$w\src", "$w\gen" -Filter *.java | ForEach-Object FullName)
& javac -nowarn -source 8 -target 8 -bootclasspath $jar -encoding UTF-8 -d "$w\classes" @srcs; if ($LASTEXITCODE) { throw 'javac failed' }
$cls = @(Get-ChildItem -Recurse "$w\classes" -Filter *.class | ForEach-Object FullName)
& "$bt\d8.bat" --release --lib $jar --min-api 24 --output "$w\dex" @cls; if ($LASTEXITCODE) { throw 'd8 failed' }

# 3. add classes.dex, align, sign
Copy-Item "$w\base.apk" "$w\unsigned.apk"
Add-Type -AssemblyName System.IO.Compression, System.IO.Compression.FileSystem
$z = [IO.Compression.ZipFile]::Open("$w\unsigned.apk", 'Update')
[IO.Compression.ZipFileExtensions]::CreateEntryFromFile($z, "$w\dex\classes.dex", 'classes.dex') | Out-Null
$z.Dispose()
& "$bt\zipalign.exe" -f -p 4 "$w\unsigned.apk" "$w\aligned.apk"; if ($LASTEXITCODE) { throw 'zipalign failed' }

$ks = "$KeyDir\matchmaker-release.jks"; $pwf = "$KeyDir\keystore-password.txt"
if (-not (Test-Path $ks)) {
  New-Item -ItemType Directory -Force $KeyDir | Out-Null
  $pw = -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 24 | ForEach-Object { [char]$_ })
  Set-Content -Encoding ascii $pwf $pw
  & keytool -genkeypair -keystore $ks -storepass $pw -keypass $pw -alias matchmaker -keyalg RSA -keysize 2048 -validity 10000 -dname 'CN=Match Maker, O=JAMREIBIZ Badminton Club' | Out-Null
  if ($LASTEXITCODE) { throw 'keytool failed' }
}
$pw = (Get-Content $pwf -Raw).Trim()
& "$bt\apksigner.bat" sign --ks $ks --ks-key-alias matchmaker --ks-pass "pass:$pw" --key-pass "pass:$pw" --out "$Out\MatchMaker.apk" "$w\aligned.apk"; if ($LASTEXITCODE) { throw 'apksigner failed' }
& "$bt\apksigner.bat" verify "$Out\MatchMaker.apk"; if ($LASTEXITCODE) { throw 'verify failed' }
Write-Output "Built $Out\MatchMaker.apk (version $VersionName / code $VersionCode)"
