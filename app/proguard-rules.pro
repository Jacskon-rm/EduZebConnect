
# Add project specific ProGuard rules here.
-keep class com.eduzeb.connect.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
