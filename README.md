# PaperReader theme extension sample

This repository is a minimal out-of-process PaperReader theme extension. It
supplies validated light and dark semantic palettes, shape/typography choices,
and a complete semantic icon set through the versioned PaperReader AIDL
contract.

Icons are bounded ASCII path data. PaperReader owns parsing and rendering; the
extension cannot inject layouts, code, arbitrary resources, or host file paths.

## Build

Clone `PaperReader` into this repository as `PaperReader`, or provide its path
explicitly:

```bash
./gradlew :app:assembleDebug -PpaperReaderSdkPath=/path/to/PaperReader
```

For a local runtime test, also pass the SHA-256 certificate fingerprint of the
PaperReader build that is allowed to bind the service:

```bash
./gradlew :app:assembleDebug \
  -PpaperReaderSdkPath=/path/to/PaperReader \
  -PpaperReaderHostSignerSha256=<64-hex-sha256>
```

The default fingerprint is deliberately invalid, so an unconfigured sample
fails closed. Icon path data uses the SDK's `2400 × 2400` viewport and must
include every semantic icon key.

The sample is intentionally not trusted by PaperReader releases. A production
extension must be signed, published through a reviewed index entry, and matched
by package, version, API level, service component, and certificate SHA-256
before the host will bind it.

See the [PaperReader extension guide][extension-guide] for the contract limits
and host-side development properties.

The sample icon paths are generated from Tabler Icons 3.46.0 under the MIT
License. See
[`LICENSES/Tabler-Icons-MIT.txt`](LICENSES/Tabler-Icons-MIT.txt).

The sample code is licensed under the [Apache License 2.0](LICENSE).

[extension-guide]: https://github.com/ImAno177/PaperReader/blob/main/docs/EXTENSIONS.md
