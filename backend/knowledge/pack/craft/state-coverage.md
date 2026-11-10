# State coverage

Adapted from OpenDesign's `craft/state-coverage.md` (Apache-2.0). The most reliable
AI-design failure is shipping only the populated state.

## Every surface that fetches, transforms or accepts data renders five states
| State | Must contain |
|---|---|
| **Loading** | skeleton matching the final layout, plus a 15s "taking longer than expected" fallback |
| **Empty** | headline, plain explanation, one primary action |
| **Error** | plain-language cause, recovery action, **user input preserved** |
| **Populated** | the case the design was drawn for |
| **Edge** | extreme volume, long strings, missing optional fields, RTL, partial network |

## Edge matrix — the cases that actually break layouts
| Surface | Stress it with |
|---|---|
| List | 0 items, 1 item, 500 items, 120-character titles |
| Card | missing image, missing meta, 3-line title |
| Form | 200-character input, invalid on blur, server rejection after submit |
| Table | 12 columns, null cells, long numbers, RTL |
| Chat | 3-word message, 2000-word message, failed send, retry |
| Nav | 3 destinations, 5 destinations, one with a very long label |
| Media | portrait, landscape, 0-byte, slow network |

## Rules
- A skeleton that does not match the final layout is worse than no skeleton.
- Errors appear next to the field, never only in a toast; input is never cleared.
- Empty states are an invitation to act, with real copy — not an apology.
- Loading under 300 ms should show nothing at all (avoid the flash).
- Every async button has a loading state that keeps its width.
