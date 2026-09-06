[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)

# PaperReader theme extension sample

A small out-of-process theme extension that demonstrates declarative colors, typography, shape
tokens, decorations, and semantic icons for PaperReader.

Status: educational sample. This APK is not one of PaperReader's official theme packages.

## Table of contents

- [About the project](#about-the-project)
- [Built with](#built-with)
- [Getting started](#getting-started)
- [Usage](#usage)
- [Security boundary](#security-boundary)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)
- [Acknowledgments](#acknowledgments)

## About the project

The sample supplies complete light and dark semantic palettes, title/body/label font choices,
corner, border, shadow, and decoration tokens, and every required semantic icon through the
versioned PaperReader AIDL contract.

Icon paths are bounded ASCII data. PaperReader parses and renders them in the host process. The
extension cannot inject layouts, executable code, arbitrary resources, JavaScript, or host file paths.

## Built with

| Area | Technology |
| --- | --- |
| Android | Kotlin, Android application module, min SDK 28, target SDK 36 |
| Contract | `dev.paperreader:extension-api:0.1.0` over versioned AIDL |
| Icon source | Tabler Icons 3.46.0, MIT licensed source paths |
| Build | Gradle wrapper, Java and Kotlin target 17 |

## Getting started

### Prerequisites

- A PaperReader checkout with the `:extension-api` module
- JDK 17 or newer
- Android SDK Platform 36

Place the PaperReader checkout in a directory named `PaperReader` under this repository, or set
`PAPERREADER_SDK_PATH` to its absolute path. The default build uses that directory:

```powershell
.\gradlew.bat :app:assembleDebug
```

For a custom checkout, set `PAPERREADER_SDK_PATH` and pass it to Gradle:

```powershell
.\gradlew.bat :app:assembleDebug `
  -PpaperReaderSdkPath=$env:PAPERREADER_SDK_PATH
```

### Configure a local host

The host accepts a service only when the sample is built with the SHA-256 certificate fingerprint of
that host. Set `PAPERREADER_HOST_SIGNER_SHA256` to the 64-character hexadecimal digest, then build:

```powershell
.\gradlew.bat :app:assembleDebug `
  -PpaperReaderSdkPath=$env:PAPERREADER_SDK_PATH `
  -PpaperReaderHostSignerSha256=$env:PAPERREADER_HOST_SIGNER_SHA256
```

When the fingerprint is not configured, the sample deliberately fails closed. Icon path data uses a
`2400 x 2400` viewport and must include every semantic icon key. The debug APK is written to
`app/build/outputs/apk/debug/app-debug.apk`.

## Usage

Install the debug APK beside a compatible PaperReader debug build that uses the same host signer.
Select the sample theme from the host and inspect both light and dark modes, semantic icons, and
shape or typography tokens.

The host owns validation and rendering. Missing, oversized, or malformed icons reject the theme as a
whole.

## Security boundary

This sample demonstrates the declarative theme boundary, not a production release process. A release
extension must be reviewed, published through a signed store entry, and verified against its package,
version, API range, exported service, APK digest, byte size, and signing certificate.

Do not commit signing keys or host certificate material.

## Roadmap

- Keep the sample aligned with the versioned extension API.
- Keep the semantic icon set complete as the host contract evolves.

## Contributing

Keep changes focused on declarative theme data or the sample service. Verify the APK locally before
opening a pull request and update the [PaperReader extension guide][extension-guide] when the host
boundary changes.

## License

The sample code is licensed under the [Apache License 2.0](LICENSE). The icon attribution is recorded
in [`LICENSES/Tabler-Icons-MIT.txt`](LICENSES/Tabler-Icons-MIT.txt).

## Contact

Use the [sample repository issue tracker](https://github.com/ImAno177/PaperReader-theme-sample/issues)
for questions and focused improvements.

## Acknowledgments

The sample icon paths are generated from [Tabler Icons](https://tabler.io/icons) 3.46.0 under the MIT
License.

[extension-guide]: https://github.com/ImAno177/PaperReader/blob/main/docs/EXTENSIONS.md
