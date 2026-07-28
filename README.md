# Get Off My Lawn ReServed

*Get Off My Lawn ReServed* is a player-claim mod for Survival/Freebuild NeoForge servers.
It works fully server side, so players do not need to install it on their clients.

This project is a fork of [Get Off My Lawn Reserved by Patbox](https://github.com/Patbox/get-off-my-lawn-reserved), with a focus on porting the mod to neoforge platform.

## Supported versions

| Minecraft | NeoForge | Server runtime supplied in the release directory |
| --- | --- | --- |
| 1.21.1 | **21.1.233 tested baseline; later 21.1.x accepted** | Connector `2.0.0-beta.14`, Forgified Fabric API `0.116.7+2.2.4` and GOML Polymer Runtime Bundle `0.9.19` |
| 26.1 | NeoForge 26.1.0.19-beta or newer 26.1 build | Patched Polymer Neo `0.16.6-beta.8` |
| 26.1.1 | NeoForge 26.1.1.15-beta or newer 26.1.1 build | Patched Polymer Neo `0.16.6-beta.8` |
| 26.1.2 | NeoForge 26.1.2.87 or newer 26.1.2 build | Patched Polymer Neo `0.16.6-beta.8` |

## Installation and required server mods

Use only the directory matching the server's Minecraft version. Every required
jar listed below is already collected into that build's release directory.

### Minecraft 1.21.1

Install these jars on the server:

- `goml-<version>+1.21.1+neoforge.jar`
- `connector-2.0.0-beta.14+1.21.1-full.jar`
- `forgified-fabric-api-0.116.7+2.2.4+1.21.1.jar`
- `polymer-goml-bundled-0.9.19+1.21.1.jar` (unless a compatible external
  Polymer bundle is already installed)

Forgified Fabric Loader is contained in the full Forgified Fabric API jar, so
do not add a second standalone copy unless another modpack component explicitly
requires one. The GOML Polymer Runtime Bundle contains only Polymer Common and
Core; those modules already contain Packet Tweaker, Networking and Registry
Sync. Servers that already need the full Polymer Bundled mod may omit the GOML
runtime bundle. The full bundle's additional modules are not required by GOML,
however, and can create unrelated compatibility problems: Polymer Blocks can
conflict with Connector beta.14/NeoForge transformations, while Polymer Virtual
Entity conflicts with ByePregen 1.0.7 because both transform
`ServerChunkCache.tickChunks`. When ByePregen is installed, remove the full
Polymer bundle and use the supplied GOML runtime bundle. GOML logs an explicit
warning when that incompatible combination is detected.

### Minecraft 26.1, 26.1.1 and 26.1.2

Install these jars on the server:

- `goml-<version>+<minecraft-version>+neoforge.jar`
- `polymer-bundled-neo-0.16.6-beta.8+26.1.2.jar`

Dynmap and BlueMap are optional integrations and are not required for GOML.

Install every jar from the matching release directory into the server's
`mods` directory. Do not install GOML or these compatibility jars on ordinary
clients. Polymer translates GOML's custom registry entries, skin-textured
blocks and items to vanilla representations, preserving vanilla placement and
breaking animation/sounds without requiring a client mod.

The 1.21.1 build uses [Sinytra Connector](https://github.com/Sinytra/Connector)
and [Forgified Fabric API](https://github.com/Sinytra/ForgifiedFabricAPI) to
run the Fabric Polymer line on NeoForge. NeoForge 21.1.233, Connector
2.0.0-beta.14 and Forgified Fabric API 0.116.7+2.2.4 are the tested baseline.
The metadata accepts later NeoForge 21.1.x builds; newer Connector/FFAPI builds
for Minecraft 1.21.1 may also work, but neither combination is guaranteed.
Polymer 0.9.19 prints an upstream warning that Connector/Forge is unsupported;
the GOML bootstrap path has been validated, but client visuals and large-modpack
interoperability should be tested before deployment. The 1.21.1 build includes
an early-bootstrap compatibility workaround for KubeJS 2101.7.2. The 26.1.x
builds use the audited
[Polymer Neo port](https://codeberg.org/tomalbrc/polymer-neo).

Release files use the format
`<mod-version>+<minecraft-version>+neoforge`, for example
`goml-1.21.0+1.21.1+neoforge.jar`.

## Video showcase

* English: https://youtu.be/R9-PuMRbNEc

* Polish: https://youtu.be/1V8kh0h3NoU

## Getting started

To get started, you'll have to craft a *Claim Anchor*. Each anchor has a different (configurable by admin) claim radius; after placing one, a box around it will be formed. This box is yours!

* **Makeshift**, default radius of 10
* **Reinforced**, default radius of 25
* **Glistening**, default radius of 50
* **Crystal**, default radius of 75
* **Emeradic**, default radius of 125
* **Withered**, default radius of 200

To see claim areas, you'll have to craft a *Goggles of (Claim) Revealing*

When this item equipped in the helmet, mainhand or offhand slot, claim outlines become visible.

## [Recipes](recipes.md)

### Claim configuration

To configure your claim, you can interact with the anchor block. A UI will appear that offers several configuration options:

- The player list can be used to add and remove access of players to your claim
- The Augment list, that can be used for checking and configuring active augments

### Claim upgrades

To upgrade your claim, place an Anchor Augment next to the core Claim Anchor. Anchor Augments available include:

- Ender Binding: Prevents Endermen from teleporting
- Villager Core: Prevents Zombies from damaging Villagers
- Greeter: MOTD to visitors
- Angelic Aura: Regen to all players inside region
- Withering Seal: Prevents wither status effect
- Force Field: non-whitelisted players get launched out of the claim
- Heaven's Wings: flight
- Lake Spirit's Grace: water breathing, water sight, and better breathing
- Chaos Zone: Strength to all players inside region
- PvP Arena: Allows changing pvp state in claim
- Explosion Controller: Allows toggling explosion protection

## Configuration

You can find config file in `./config/getoffmylawn.json`. To reload it, just type `/goml admin reload` in chat/console.

```json5
{
  "makeshiftRadius": 10,                // Radius of makeshift claim
  "reinforcedRadius": 25,               // Radius of reinforced claim
  "glisteningRadius": 50,               // Radius of glistening claim
  "crystalRadius": 75,                  // Radius of crystal claim
  "emeradicRadius": 125,                // Radius of emeradic claim
  "witheredRadius": 200,                // Radius of withered claim
  "maxClaimsPerPlayer": -1,             // -1 means unlimited; permission options can override this
  "enablePvPinClaims": false,
  "allowDamagingUnnamedHostileMobs": true,
  "allowDamagingNamedHostileMobs": false,
  "claimProtectsFullWorldHeight": false,// Makes claim protect area from bottom of the world to top
  "claimAreaHeightMultiplier": 1.0,
  "makeClaimAreaChunkBound": false,
  "allowClaimOverlappingIfSameOwner": false,
  "allowFakePlayersToModify": false,
  "protectAgainstHostileExplosionsActivatedByTrustedPlayers": false,
  "relaxedEntitySourceProtectionCheck": false,
  "dimensionBlacklist": [               // Allows to blacklist specific dimensions
    "example:dim"
  ],             
  "regionBlacklist": {                  // Allows to blacklist specific regions
    "example:dim": [
      {
        x1: -200,
        y1: -64,
        z1: -200,
        x2: 200,
        y2: 512,
        z2: 200,
      }
    ]
  },
  "enabledAugments": {                  // Allows to enable/disable augments per their id
    "goml:lake_spirit_grace": true,
    "goml:angelic_aura": true,
    "goml:greeter": true,
    "goml:force_field": true,
    "goml:village_core": true,
    "goml:withering_seal": true,
    "goml:ender_binding": true,
    "goml:heaven_wings": true,
    "goml:chaos_zone": true
  },
  "allowedBlockInteraction": [          // Allows to interact with specific blocks in claim
    "somemod:store"
  ],
  "allowedEntityInteraction": [         // Allows to interact with specific entities in claim
    "minecraft:villager"
  ],
  "messagePrefix": "<dark_gray>[<#a1ff59>GOML</color>]", // Default prefix used in messages
  "placeholderNoClaimInfo": "<gray><italic>Wilderness",
  "placeholderNoClaimOwners": "<gray><italic>Nobody",
  "placeholderNoClaimTrusted": "<gray><italic>Nobody",
  "placeholderClaimCanBuildInfo": "${owners} <gray>(<green>${anchor}</green>)",
  "placeholderClaimCantBuildInfo": "${owners} <gray>(<red>${anchor}</red>)",
  "claimColorSource": "location"        // either "location" or "player" - "location" will chose the color based on the location of the claim (hash of coordinates), "player" will chose the color based on the owner of the claim (hash of UUID).
}
```

## Building

Use PowerShell 7 and the included Gradle wrapper. Java 21 is used for the
1.21.1 target and Java 25 for the 26.1.x target.

```powershell
./scripts/build-version.ps1 -Profile 1.21.1
./scripts/build-version.ps1 -Profile 26.1
./scripts/build-version.ps1 -Profile 26.1.1
./scripts/build-version.ps1 -Profile 26.1.2
./scripts/build-all.ps1
```

Collected release jars are written to
`build/multiversion/<minecraft-version>/`. Each directory contains GOML, its
required server-side runtime jars, the third-party notices, all applicable
license texts, and the modified Polymer Neo source patch. The build and release
workflows use the same version profiles.

GitHub Actions caches both Gradle's shared dependency state and the
version-specific Minecraft/NeoForge workspace for all four targets. A missing
cache is populated after a successful build and reused by later matching
builds.

## Development note

The NeoForge port, multi-version build setup, compatibility backports, and
documentation were created with assistance from OpenAI Codex, followed by
local compile and dedicated-server validation.

## License

*Get Off My Lawn ReServed* is available under the MIT license. The project, code, and assets found in this repository are available for free public use (as long as credited).

Embedded, distributed, and optional compile-only third-party components are
listed individually in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
Complete license texts and a reverse component mapping are in
[`third_party/licenses/`](third_party/licenses/). The locally patched Polymer
Neo bundle includes its exact upstream revision, checksum, rebuild instructions
and corresponding source patch in
[`third_party/polymer-neo/`](third_party/polymer-neo/).
