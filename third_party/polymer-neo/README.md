# Polymer Neo bundle used by GOML

GOML's Minecraft 26.1.x releases distribute a locally built Polymer Neo
bundle because the required 26.1 implementation is not published to a public
Maven repository.

- Upstream: https://codeberg.org/tomalbrc/polymer-neo
- Upstream revision: `f557c37`
- Declared upstream version: `0.16.6-beta.8+26.1.2`
- GOML bundle file:
  `polymer-bundled-neo-0.16.6-beta.8+26.1.2.jar`
- SHA-256:
  `A58A03639B1EC8915415434F060F2959AC7D6202F8828F6D55458E918D09A68E`
- Primary license: LGPL-3.0-only
- Registry Sync Manipulator module license: MIT

The binary is used for Minecraft 26.1, 26.1.1 and 26.1.2. It contains Polymer
Common, Core, Networking, Registry Sync Manipulator, Resource Pack, Resource
Pack Extras and Sound Patcher. Modules unrelated to GOML's server-only block
and item translation are omitted.

## Local changes

The complete local source changes are recorded in
`polymer-neo-goml.patch`. In summary:

1. Limit the bundled module set to what GOML needs.
2. Remove duplicate or omitted-module compile dependencies.
3. Adapt Polymer Core's advanced-tooltip mixin to the 26.1.2 method split,
   while keeping each injection optional across 26.1 patch releases.

## Rebuilding

1. Clone the upstream repository and check out `f557c37`.
2. Apply `polymer-neo-goml.patch` from this directory with
   `git apply --unidiff-zero polymer-neo-goml.patch`.
3. Use Java 25 and run `./gradlew build`.
4. Copy the root bundled jar from `build/libs/` to this directory.

The upstream source at the revision above plus the supplied patch is the
corresponding source for this modified binary. Full license texts are in
`../licenses/`.
