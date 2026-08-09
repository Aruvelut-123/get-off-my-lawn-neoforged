# License file index

This directory is copied into every release directory and every GOML jar.
`THIRD_PARTY_NOTICES.md` contains component versions, source links, and the
same mapping in component-first form.

## `Apache-2.0.txt`

- rtree-3i-lite-fabric 0.3.0 — embedded in all GOML jars
- DynmapCoreAPI 3.6 — optional compile-only API, not redistributed

## `LGPL-3.0-only.txt`

- Placeholder API 2.4.2+1.21 and 3.0.0+26.1 — embedded

Some upstream PB4 projects declare `LGPL-3.0-or-later`; the supplied text is
the version 3 license text and does not remove their “or later” option.

## `GPL-3.0-only.txt`

The GNU GPL v3 text is supplied because LGPL v3 incorporates its terms and
requires both texts to accompany combined distributions. It therefore
corresponds to every component listed under `LGPL-3.0-only.txt` above.

## `MIT.txt`

- Common Protection API 1.0.0 and 2.0.0 — embedded
- BlueMap API 2.7.4 — optional compile-only API, not redistributed

GOML itself remains covered by the repository-root `LICENSE`, which is copied
to release directories as `LICENSE-GOML.txt`.
