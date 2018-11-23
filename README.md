# FChat
This is a simple real time one-to-one chat app for android using firebase database. Users can login using their google accounts and have conversations with other registered members.

![ScreenShot](https://raw.github.com/g0g0l/FChat/master/screenshots/screenshot_1.png)
![ScreenShot](https://raw.github.com/g0g0l/FChat/master/screenshots/screenshot_2.png)
![ScreenShot](https://raw.github.com/g0g0l/FChat/master/screenshots/screenshot_3.png)
![ScreenShot](https://raw.github.com/g0g0l/FChat/master/screenshots/screenshot_4.png)

Before you use:
1. Create a firebase project in your firebase console
2. Enable google login for firebase in your firebase project (more http://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/)
3. Create SHA1 fingerprint in your system 

Windows: keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android (From Java->jdk-bin)

Mac/Linux: keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

(For production, the json file is to be created from play store console)

4. Download and add google-services.json file in the app directory (more https://firebase.google.com/docs/android/setup)


For reference, see https://codelabs.developers.google.com/codelabs/firebase-android/#0

I have provided my google-services.json as a sample (DO NOT USE IT IN YOUR OWN PROJECT, IT WILL NOT WORK)
