# Get Off My Lawn ReServed

*Get Off My Lawn ReServed* is a player-claim mod for Survival/Freebuild
NeoForge servers and modpacks. The NeoForge port uses native registries and
must be installed on both the server and every connecting client.

This project is a fork of [Get Off My Lawn Reserved by Patbox](https://github.com/Patbox/get-off-my-lawn-reserved), with a focus on porting the mod to neoforge platform.

## Supported versions

| Minecraft | Tested NeoForge baseline | Required external mods |
| --- | --- | --- |
| 1.21.1 | 21.1.233 | None |
| 26.1 | 26.1.0.19-beta | None |
| 26.1.1 | 26.1.1.15-beta | None |
| 26.1.2 | 26.1.2.87 | None |

The metadata accepts later NeoForge builds within the same Minecraft release
line. They may work, but only the listed baseline is tested and compatibility
is not guaranteed.

## Installation and dependencies

Install only the matching
`goml-<mod-version>+<minecraft-version>+neoforge.jar` on both sides:

- the dedicated or integrated server; and
- every client that connects to it.

The client and server must use the same Minecraft-targeted GOML build. GOML has
no required external mod dependencies on any supported version: do not install
Connector, Forgified Fabric API, Polymer, or Packet Tweaker for GOML. If another
mod independently requires one of them, follow that mod's instructions.

Dynmap and BlueMap remain optional server-side integrations. Neither is
required for GOML.

The original `goml:*` registry IDs are retained for world compatibility, while
the NeoForge build now registers blocks, items and block entities natively.
Claim blocks use generated native models based on the original skin textures
and standard block sounds, placement feedback and breaking animations. Back up
existing worlds before changing builds and test the result in the target
modpack.

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
`build/multiversion/<minecraft-version>/`. Each directory contains the single
matching GOML jar, third-party notices and all applicable license texts. The
build and release workflows use the same version profiles.

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
[`third_party/licenses/`](third_party/licenses/).
