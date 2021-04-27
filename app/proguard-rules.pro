# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/suzuki.ren/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

########## Annotation, Exceptions, Signature ##########
-keepattributes *Annotation*,Exceptions,Signature,SourceFile,LineNumberTable,InnerClass
########## Activity, Application, Service, BroadcastReceiver ##########
-keep public class * extends android.app.*
-keep public class * extends android.content.*
-keep public class * extends android.os.Binder
-keep public class * extends android.widget.*
########## AndroidX ##########
-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends androidx.fragment.app.Fragment
########## Android Support Library ##########
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v7.app.AppCompatActivity
-keep interface android.support.v7.** { *; }
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
##################################################
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-dontwarn android.security.**
-dontwarn android.databinding.**

########## Logger(Log block need to comment while release if you want to stop logging) ##########
-assumenosideeffects class android.util.Log {
 public static *** d(...);
 public static *** i(...);
 public static *** v(...);
}
#-keep class com.meembusoft.iot.util.Logger {*;}

########## Android Test ##########
# Proguard rules that are applied to your test apk/code.
# https://github.com/googlesamples/android-testing-templates/blob/master/AndroidTestingBlueprint/app/proguard-test-rules.pro
-ignorewarnings

-keepattributes *Annotation*

-dontnote junit.framework.**
-dontnote junit.runner.**

-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
-dontwarn com.squareup.javawriter.JavaWriter

-dontwarn org.mockito.**

########## Google Play Service ##########
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

########## OkHttp3 ##########
-keep class okhttp3.** { *; }
-keep interface okhttp3.* { *; }
-keep class okio.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

########## Picasso ##########
-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.picasso.**

########## Retrofit ##########
# https://square.github.io/retrofit/
## Retrofit
#-dontwarn retrofit.**
#-keep class retrofit.** { *; }
#-keepattributes Signature
#-keepattributes Exceptions
## Retrofit2
#-keep class retrofit2.** { *; }
#-dontwarn retrofit2.**
#-keepattributes Signature
#-keepattributes Exceptions
#-dontwarn okio.**
#-dontwarn javax.annotation.**

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
# Retrofit does reflection on generic parameters and InnerClass is required to use Signature.
-keepattributes Signature, InnerClasses, Exceptions
# Retain service method parameters when optimizing.
-keepclassmembers, allowshrinking, allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
#-keepclasseswithmembers class * {
#    @retrofit2.http.* <methods>;
#}
#-keepclassmembernames interface * {
#        @retrofit.http.* <methods>;
#}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**
# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit
# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.-KotlinExtensions

########## Gson ##########
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keep class sum.misc.Unsafe { *; }
-keepattributes Expose
-keepattributes SerializedName
-keepattributes Since
-keepattributes Until
-keepclasseswithmembers class * { @com.google.gson.annotations.Expose <fields>; }
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# keep enum so gson can deserialize it
-keepclassmembers enum * { *; }

### TODO declare the classes which use GSON
#-keep class com.meembusoft.iot.model.** {*;}
#-keep class com.meembusoft.iot.retrofit.** {*;}
-keep class com.reversecoder.library.event.** {*;}
-keep class com.reversecoder.library.event.** {*;}
-keep class me.jerryhanks.countrypicker.Country {*;}
-keep class me.jerryhanks.countrypicker.Util {*;}

########## Apache commons ##########
-keep class org.apache.commons.** {*;}

########## EventBus ##########
# http://greenrobot.org/eventbus/documentation/proguard
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

########## Realm ##########
# Proguard Configuration for Realm (http://realm.io)
# For detailed discussion see: https://groups.google.com/forum/#!topic/realm-java/umqKCc50JGU
# Additionally you need to keep your Realm Model classes as well
# For example:
# -keep class com.yourcompany.realm.** { *; }

-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-dontwarn javax.**
-dontwarn io.realm.**