# License file index

This directory is copied into every release directory and every GOML jar.
`THIRD_PARTY_NOTICES.md` contains component versions, source links, and the
same mapping in component-first form.

## `Apache-2.0.txt`

- rtree-3i-lite-fabric 0.3.0 — embedded in all GOML jars
- Forgified Fabric API 0.116.7+2.2.4+1.21.1 — distributed with Minecraft
  1.21.1
- Forgified Fabric Loader 2.5.68+0.18.4+1.21.1 — contained in the Forgified
  Fabric API distribution
- DynmapCoreAPI 3.6 — optional compile-only API, not redistributed

## `LGPL-3.0-only.txt`

- Server GUI 1.6.1+1.21.1 and 2.0.0+26.1 — embedded
- Placeholder API 2.4.2+1.21 and 3.0.0+26.1 — embedded
- GOML Polymer Runtime Bundle 0.9.19+1.21.1, containing Polymer Common and
  Core plus their nested Networking and Packet Tweaker runtimes — distributed
  with Minecraft 1.21.1
- Packet Tweaker 0.5.6+1.21 — transitive runtime distributed with Minecraft
  1.21.1
- Modified Polymer Neo bundle 0.16.6-beta.8+26.1.2 — distributed with
  Minecraft 26.1, 26.1.1 and 26.1.2

Some upstream PB4 projects declare `LGPL-3.0-or-later`; the supplied text is
the version 3 license text and does not remove their “or later” option.

## `GPL-3.0-only.txt`

The GNU GPL v3 text is supplied because LGPL v3 incorporates its terms and
requires both texts to accompany combined distributions. It therefore
corresponds to every component listed under `LGPL-3.0-only.txt` above.

## `MIT.txt`

- Common Protection API 1.0.0 and 2.0.0 — embedded
- Sinytra Connector 2.0.0-beta.14+1.21.1 — distributed with Minecraft 1.21.1
- Polymer Registry Sync Manipulator 0.9.19+1.21.1 — distributed with
  Minecraft 1.21.1
- Polymer Neo Registry Sync Manipulator 0.16.6-beta.8+26.1.2 — included in the
  modified Polymer Neo bundle
- BlueMap API 2.7.4 — optional compile-only API, not redistributed

GOML itself remains covered by the repository-root `LICENSE`, which is copied
to release directories as `LICENSE-GOML.txt`.
