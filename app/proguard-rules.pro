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
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keepattributes *Annotation*, InnerClasses, SourceFile, LineNumberTable, Signature, Exceptions
-keep, allowobfuscation @pl.selvin.android.autocontentprovider.annotation.Table public interface * {
          @pl.selvin.android.autocontentprovider.annotation.Column static <fields>;
          @pl.selvin.android.autocontentprovider.annotation.TableName static <fields>;
}
-keep class pl.selvin.android.popularmovies.models.** { *; }
-dontwarn pl.selvin.android.syncframework.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontnote okhttp3.**
-dontwarn okio.**
-dontnote com.google.gson.**
-dontnote pl.selvin.android.autocontentprovider.annotation.**
-dontnote org.apache.http.**
-dontnote android.net.http.**
-dontnote retrofit2.Platform