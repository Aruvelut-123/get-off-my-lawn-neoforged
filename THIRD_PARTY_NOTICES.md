# Third-party notices

This file covers the third-party libraries that GOML embeds or compiles against
for an optional integration. The notices do not replace the original projects'
license files or terms.

Full license texts are stored in `third_party/licenses/` and are also packaged
inside every GOML jar under `META-INF/licenses/`.

## Embedded in every GOML jar

| Component | Versions used | License | Source |
| --- | --- | --- | --- |
| rtree-3i-lite-fabric | 0.3.0 | Apache-2.0 | https://github.com/JamiesWhiteShirt/rtree-3i-lite |
| Placeholder API | 2.4.2+1.21; 3.0.0+26.1 | LGPL-3.0-or-later | https://github.com/Patbox/TextPlaceholderAPI |
| Common Protection API | 1.0.0; 2.0.0 | MIT | https://github.com/Patbox/common-protection-api |

## Optional compile-only integrations

These APIs are not embedded in or distributed with GOML.

| Component | Version used to compile | License | Source |
| --- | --- | --- | --- |
| DynmapCoreAPI | 3.6 | Apache-2.0 | https://github.com/webbukkit/dynmap |
| BlueMap API | 2.7.4 | MIT | https://github.com/BlueMap-Minecraft/BlueMapAPI |

## License text mapping

- Apache-2.0: `third_party/licenses/Apache-2.0.txt`
- GNU GPL v3, incorporated by and required alongside LGPL v3:
  `third_party/licenses/GPL-3.0-only.txt`
- GNU LGPL v3: `third_party/licenses/LGPL-3.0-only.txt`
- MIT: `third_party/licenses/MIT.txt`

Copyright remains with the respective projects and contributors. The
copyright notices stated by the MIT-licensed upstream projects are:

- Common Protection API: Copyright (c) 2022 Patbox
- BlueMap API: Copyright (c) Blue <https://www.bluecolored.de> and contributors
