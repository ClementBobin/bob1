# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Koin - prevent R8 from crashing on Kotlin metadata rewriting (AGP 8.13 / R8 8.13.6)
-keep class org.koin.** { *; }
-keepclassmembers class org.koin.** { *; }
-dontwarn org.koin.**

# Prevent R8 from rewriting Kotlin metadata entirely for Koin
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-keep class dev.kindling.** { *; }
-keepclassmembers class dev.kindling.** { *; }