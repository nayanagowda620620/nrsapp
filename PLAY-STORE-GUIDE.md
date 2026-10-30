# How to publish the NRS Konandur app on Google Play

> This project could not be compiled where it was written (no Android SDK / internet there). It is a standard Android Studio project — expect to build it in about 10 minutes. If Android Studio shows a "newer Android Gradle Plugin / Kotlin available" notice, accept the upgrade.

## 0. What you need
• A computer with **Android Studio** (free) and internet
• Your website live on HTTPS (the app reads notices and shows the privacy policy from it)
• A Google Play developer account (one-time registration fee)

## 1. Open and configure
1. Unzip, then Android Studio → **Open** → choose the `NRS-Konandur-Android` folder. Let Gradle sync finish.
2. Open `app/src/main/java/com/nrskonandur/school/AppConfig.kt` and set `SITE_URL` to your real website address.
3. Upload the website files, including **app/notices.json** and **privacy.html** (from the website zip).
4. Package name `com.nrskonandur.school` (in `app/build.gradle.kts`, `applicationId` and `namespace`) becomes **permanent once published**. Change it now if you prefer another (e.g. `in.yourdomain.nrs`) — also rename the folder `java/com/nrskonandur/school` and the `package` lines at the top of each .kt file.
5. Press **Run ▶** on an emulator or your phone (USB debugging) and check every tab.

## 2. Create your upload key (once — keep it safe!)
Run in a terminal:
```
keytool -genkeypair -v -keystore nrs-upload-key.jks -alias nrs-upload -keyalg RSA -keysize 2048 -validity 10000
```
Copy `keystore.properties.example` → `keystore.properties`, fill in your passwords. **Back up the .jks file and passwords in two places; never share them.** Enable **Play App Signing** in Play Console (default) so a lost upload key can be reset.

## 3. Build the release bundle
Android Studio → **Build → Generate Signed App Bundle / APK → Android App Bundle**, choose your key, **release**. Output: `app/release/app-release.aab` — this is the file you upload.

## 4. Play Console
1. Create the developer account. **Personal accounts created after 13 Nov 2023** must run a **closed test with at least 12 testers for 14 continuous days** before applying for production access. **Organisation accounts are exempt** but need a D-U-N-S number (can take weeks). Plan ~3 weeks if personal. Ask parents/teachers to be testers.
2. **Create app** → name "NRS Konandur", App, Free.
3. Fill **Store listing** using `STORE-LISTING.md` + files in `play-store-assets/` + your emulator screenshots.
4. Complete App content: privacy policy URL, ads, app access, content rating, target audience, data safety (answers in `STORE-LISTING.md`).
5. **Testing → Closed testing** → create release → upload the .aab → add testers' Gmail addresses (or a Google Group) → share the opt-in link → wait 14 days → **Apply for production** → Production release.
6. New apps must target Android 16 (API 36) — already set in this project.

## 5. Running the app for engagement
**Notices (no coding):** edit `app/notices.json` on your website. Put the newest notice at the TOP and give it a NEW `id`. Parents with alerts on get a notification within ~6 hours (Android decides the exact time).
```
{ "notices": [
  { "id": "2027-002", "title": "Parent–teacher meeting", "date": "2027-02-10", "body": "Details…", "link": "" },
  { "id": "2027-001", "title": "Admissions open for 2027–28", "date": "2026-10-23", "body": "…", "link": "" }
] }
```
**Get installs:** print a QR code of your Play Store link for the school gate, admission desk and brochures; share the link in parents' WhatsApp groups; add a "Get it on Google Play" button on the website once live; ask satisfied parents for a rating.
**Updating the app:** increase `versionCode` (and `versionName`) in `app/build.gradle.kts`, build a new .aab, upload to a new release.

## 6. Good to know
• Do not add fees, dates or results to the app unless the office confirms them.
• The "25 years" badge is a placeholder drawing — replace `res/drawable-nodpi/badge_25.png` with your real logo.
• No Firebase/push server is used; alerts come from the app checking your website's notices file, so no extra accounts or costs.
