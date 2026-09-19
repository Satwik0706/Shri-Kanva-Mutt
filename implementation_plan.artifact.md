# Implementation Plan: Tithi Date Correction and UI/UX Overhaul

This plan addresses the date/time mismatch in Tithi display and performs a comprehensive UI/UX upgrade to a professional ("entrepreneur level") standard.

## User Review Required

> [!IMPORTANT]
> The UI/UX overhaul will significantly change the app's appearance to a more modern, professional look using gradients, refined shadows, and improved typography.

## Proposed Changes

### Data & Engine Layer
Modify the Panchanga engine to include Tithi start times and update the data model to persist this information.

#### [MODIFY] [PanchangaEngine.kt](file:///D:/app/app/src/main/java/com/satwik/example/mutt_app/data/PanchangaEngine.kt)
- Add `tithiStart` and `nakshatraStart` to `DetailedResult` data class.
- Compute `tithiStart` by finding the transition time for the previous Tithi angle.
- Compute `nakshatraStart` similarly.

#### [MODIFY] [MuttData.kt](file:///D:/app/app/src/main/java/com/satwik/example/mutt_app/data/MuttData.kt)
- Update `Panchang` data class to include `tithiStart` and `nakshatraStart`.
- Update `calculateLocalPanchanga` in `MuttRepository` to map these new fields.

### UI/UX Layer
Comprehensive redesign of screens and components for a "premium" feel.

#### [MODIFY] [Screens.kt](file:///D:/app/app/src/main/java/com/satwik/example/mutt_app/ui/screens/Screens.kt)
- **Backgrounds:** Replace flat `Color(0xFFFDF5E6)` with sophisticated vertical gradients (e.g., Parchment to subtle Gold/White).
- **Cards:** Add subtle borders (`1.dp` width, low alpha Gold/Terracotta) and refined elevation.
- **Buttons:** Implement "Entrepreneur" level buttons with subtle gradients, rounded corners (12dp-16dp), and clear visual hierarchies.
- **Panchanga Card:** Display full Tithi range: "Tithi: [Name] ([Start Time] - [End Time])".
- **Hero Banners:** Refine with better overlays and typography.
- **Typography:** Increase use of `FontWeight.Medium` and `FontWeight.SemiBold` for a more structured look.

#### [MODIFY] [Color.kt](file:///D:/app/app/src/main/java/com/satwik/example/mutt_app/ui/theme/Color.kt)
- Define new "Premium" palette colors if needed (e.g., refined variants of Terracotta and Gold).

## Verification Plan

### Automated Tests
- N/A (UI-centric changes)

### Manual Verification
- Deploy the app and verify the Panchanga screen shows both start and end times for Tithis.
- Navigate through all screens to ensure the new "Entrepreneur" level UI/UX is consistent and visually appealing.
- Verify button interactions and hover/press states.
