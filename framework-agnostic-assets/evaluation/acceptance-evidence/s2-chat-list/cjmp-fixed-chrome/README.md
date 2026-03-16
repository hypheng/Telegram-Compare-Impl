# CJMP S2 Fixed Chrome Evidence (2026-03-16)

Device: Android emulator `Pixel_3a_API_34` (1080x2220).

Captured states:
- `s2-cjmp-default-top.png`: fixed chrome + search field + refresh row + list default state.
- `s2-cjmp-default-scroll.png`: list scrolled while top/bottom chrome remain fixed.
- `s2-cjmp-search-infra.png`: search keyword `infra` filters list with result count banner.
- `s2-cjmp-refreshing.png`: refresh indicator visible while list remains scrollable.
- `s2-cjmp-entry-placeholder.png`: tap on list row triggers S3 entry placeholder banner.
- `s2-cjmp-empty.png`: empty fixture with recovery CTA (debug panel still visible).
- `s2-cjmp-error.png`: error fixture verified manually; debug panel remained expanded during capture.

Known gaps in this capture set:
- `empty` / `error` / `refreshing` now captured; error state was verified manually and captured with debug panel visible.
- Debug panel is still visible in empty/error captures; a clean (collapsed) evidence pass is pending.
