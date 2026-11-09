# Screen parity map

Every section of the reference prototype (`docs/reference/a0-ui-prototype.html`) has a
Compose counterpart. Nothing was dropped.

| Prototype section | Compose |
|---|---|
| `#chat` — hero mascot, greeting bubble, suggestions, feed, composer | `ui/screens/ChatScreen.kt` |
| feed item types (`u`, `a`, `.st` steps, `.qb` plan, ready card) | `ChatScreen.kt` + `vm/AppViewModel.kt` (`FeedKind`) |
| `#prev` — top bar, device/edit segmented, `#pv` | `ui/screens/PreviewScreen.kt` |
| `.dev` device frame + `home / brew / shop` screens | `ui/preview/DevicePreview.kt` |
| `#p-canvas` — frames, zoom, variants | `PreviewScreen.kt` → `CanvasPane` |
| `#p-code` — Compose / React / HTML | `PreviewScreen.kt` → `CodePane` |
| `#p-ds` — accent, typeface, roundness, mode, fidelity | `PreviewScreen.kt` → `StylePane` |
| `#p-hist` — compare / restore | `PreviewScreen.kt` → `HistoryPane` |
| `#ftb` element toolbar + annotation | `PreviewScreen.kt` → `ElementToolbar` |
| `#prj` projects | `ui/screens/LibraryScreens.kt` → `ProjectsScreen` |
| `#tpl` explore | `LibraryScreens.kt` → `ExploreScreen` |
| `#use` usage | `LibraryScreens.kt` → `UsageScreen` |
| `#set` settings | `LibraryScreens.kt` → `SettingsScreen` |
| `#key` API keys | `LibraryScreens.kt` → `ApiKeysScreen` |
| `#dr` drawer | `ui/sheets/Overlays.kt` → `A0Drawer` |
| `#nv` floating nav + settings orb | `Overlays.kt` → `A0BottomNav` |
| `#mini` current-design pill | `Overlays.kt` → `MiniDesignPill` |
| `#sh-m` model sheet, `#sh-p` attach, `#sh-e` export, `#sh-a` actions | `Overlays.kt` → `A0SheetHost` |
| `#toast` | `ui/comp/Common.kt` → `ToastHost` |
| `.mas` mascot + moods + `fx` particles | `ui/mascot/Mascot.kt` |
| `svg.i` icon set (33 paths) | `ui/icon/A0Paths.kt` (verbatim path data) |
| CSS tokens | `ui/theme/Tokens.kt`, `ui/theme/Theme.kt` |
