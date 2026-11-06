# PaperReader theme extension sample

Status: educational API 2 sample, separate from official provider packages.
Declarative light/dark colors, typography, shape tokens, decorations, and a
complete semantic icon set. The host validates and renders every value; the
extension supplies no executable UI code.

## Build

Requires JDK 21, Android Platform 37.0, Build-Tools 36.1.0, and a PaperReader
checkout containing `:extension-api`. The SDK dependency is
`dev.paperreader:extension-api:0.2.0`; API 1 hosts are incompatible.

Set `PAPERREADER_SDK_PATH` to the host checkout, then run:

```powershell
.\gradlew.bat `
  "-PpaperReaderSdkPath=$env:PAPERREADER_SDK_PATH" `
  testDebugUnitTest lintDebug assembleDebug
```

CI pins the SDK to `f5ab91e089798c0c81b1c53a6e1579d45008241f`.
The APK is `app/build/outputs/apk/debug/app-debug.apk`.

## Test with a local host

Set `PAPERREADER_HOST_SIGNER_SHA256` to the host APK certificate's SHA-256
fingerprint, then rebuild with:

```powershell
.\gradlew.bat `
  "-PpaperReaderSdkPath=$env:PAPERREADER_SDK_PATH" `
  "-PpaperReaderHostSignerSha256=$env:PAPERREADER_HOST_SIGNER_SHA256" `
  assembleDebug
```

An unset fingerprint deliberately rejects host calls. The host must also trust
the sample's package, service, API version, version code, and certificate.
Install beside a trusted PaperReader debug build and select the sample theme.
Check both light and dark modes and the complete semantic icon set.

## Boundary

The extension runs in its own Android package and UID. It never receives the
host database, private-storage paths, or credentials. Production installation
requires a verified signed-store entry and Android's user confirmation.
See the [host extension guide][extension-guide] for the full contract.

Icon data uses a 2400 × 2400 viewport and
[Tabler Icons 3.46.0](https://tabler.io/icons). Its MIT notice is in
[LICENSES/Tabler-Icons-MIT.txt](LICENSES/Tabler-Icons-MIT.txt).

Code is licensed under [Apache License 2.0](LICENSE).

[extension-guide]: https://github.com/ImAno177/PaperReader/blob/main/docs/EXTENSIONS.md
