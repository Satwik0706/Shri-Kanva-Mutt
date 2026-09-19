# Play Store Readiness Plan for "Visitor" Flavor

This plan outlines the necessary steps to prepare the "Visitor" flavor (Shri Kanva Mutt) for a successful and professional Google Play Store release.

## User Review Required

> [!IMPORTANT]
> **Application ID Change**: The current Application ID is `com.satwik.example.mutt_app`. I recommend changing this to something more professional like `com.shrikanvamatha.official` for the Play Store. This will require updating your Firebase project's registered Android apps and downloading a new `google-services.json`.

> [!WARNING]
> **Privacy Policy**: Google Play requires a Privacy Policy link both in the app and on the store listing. I will add a link in the app's footer that points to a URL you can configure in the Admin Panel.

## Proposed Changes

### Data & Repository
#### [MODIFY] [MuttData.kt](file:///D:/app/app/src/main/java/com/satwik/example/mutt_app/data/MuttData.kt)
- Add `privacyPolicyUrl` to `ContactConfig` data class.

### UI Screens & Components
#### [MODIFY] [Screens.kt](file:///D:/app/app/src/main/java/com/satwik/example/mutt_app/ui/screens/Screens.kt)
- Add "Privacy Policy" link to `AppFooter`.
- Implement a "Share App" utility function and add a share button in the `ContactScreen` or `AboutScreen`.

#### [MODIFY] [AdminPanelScreen.kt](file:///D:/app/app/src/main/java/com/satwik/example/mutt_app/ui/screens/AdminPanelScreen.kt)
- Update `ContactEditor` to allow editing the `privacyPolicyUrl`.

### Build & Configuration
#### [MODIFY] [build.gradle.kts](file:///D:/app/app/build.gradle.kts)
- Update `targetSdk` to 35 (latest stable).
- (Optional but recommended) Update `applicationId` for `visitor` flavor.

#### [MODIFY] [AndroidManifest.xml](file:///D:/app/app/src/main/AndroidManifest.xml)
- Ensure all necessary permissions are declared and no debugging permissions are left over.

## Verification Plan

### Automated Tests
- Build the `visitorRelease` variant to ensure Proguard/R8 optimization passes without errors.
- `gradlew assembleVisitorRelease`

### Manual Verification
- Deploy the `visitor` flavor to a device.
- Verify the "Privacy Policy" link appears in the footer.
- Verify the "Share App" functionality works.
- Check that the App Name is correctly displayed as "Shri Kanva Mutt".
