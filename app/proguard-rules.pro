# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\tools\adt-bundle-windows-x86_64-20131030\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn **
-dontnote **
-keep class org.xmlpull.**
-keepclassmember class brut.androlib.meta.**{
	public *;
}
-keepclassmembers class org.xmlpull.**{
	public *;
}
-keepclassmembers class ** extends sun1.security.x509.Extension { <init>(...); }
# 将.class信息中的类名重新定义为"Proguard"字符串
-renamesourcefileattribute SourceFile
# 并保留源文件名为"Proguard"字符串，而非原始的类名 并保留行号 // blog from sodino.com
-keepattributes SourceFile,LineNumberTable

