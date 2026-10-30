# Get a test APK in ~5 minutes (no Android Studio needed)

1. Create a free account at github.com and click **New repository** (name: nrs-app, Private is fine).
2. On the new repo page click **uploading an existing file**, then drag in the CONTENTS of this folder
   (all files and folders, including the hidden `.github` folder — if your computer hides it, enable "show hidden files").
   Click **Commit changes**.
3. Open the **Actions** tab → **Build test APK** → **Run workflow**. (It also runs automatically after the upload.)
4. Wait ~5 minutes until the run shows a green tick. Open the run → scroll to **Artifacts** →
   download **NRS-Konandur-debug-apk** (a zip containing `app-debug.apk`).
5. Send `app-debug.apk` to your Android phone (WhatsApp/Drive/USB), tap it, and allow "Install unknown apps" when asked.

If the run shows a red cross, open it, copy the error text and send it to me — I will fix the code.

NOTE: this debug APK is only for testing. For Google Play you need the signed release bundle (.aab) described in PLAY-STORE-GUIDE.md.
