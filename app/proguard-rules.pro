# EventBus
-keepclassmembers class ** {
    public void onEvent*(**);
}

# Jackson
-keep class com.fasterxml.jackson.databind.ObjectMapper {
 public <methods>;
 protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
 public ** writeValueAsString(**);
}

-keepnames class com.fasterxml.jackson.** { *; }

-dontwarn com.fasterxml.jackson.databind.**

# Android Async Http

-keep class com.loopj.android.** { *; }
-keep interface com.loopj.android.** { *; }

# Auth0 Android Code
-keep class com.auth0.core.** { *; }

# Sharelock App

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep the BuildConfig
-keep class com.auth0.app.BuildConfig { *; }

# Keep the support library
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }