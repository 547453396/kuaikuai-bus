# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Leon/Documents/android_develop/sdk/tools/proguard/proguard-android.txt
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

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontoptimize

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * extends android.appwidget.AppWidgetProvider {
    public void clickOnTimeView(...);
    public void startDeskClock(...);
}

-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class cn.kuaikuai.trip.R$*{
    public static final int *;
}
#=========软件后续加入的混淆代码均在下面====================================
#Webview js和本地代码交互所需
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keepclasseswithmembers class cn.kuaikuai.trip.customview.ETWebViewHelper {
    <fields>;
    <methods>;
}

# 解决warning: Ignoring InnerClasses attribute for an anonymous inner class
 -keepattributes EnclosingMetho

-keep class com.mobclick.android.**{ *; }
-keep class org.apache.** { *; }
-keep class com.tencent.**{ *; }
-keep class cn.etouch.ecalendar.nongliManager.**{ *; }
-keep class cn.psea.sdk.** { *; }

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep class cn.kuaikuai.common.libs.**{ *; }
-dontwarn android.app.**
-keep class android.app.**{*;}

##---------------Begin: proguard configuration for Gson  ----------
-keep class com.google.gson.**{ *; }
-keepattributes Signature
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
##---------------End: proguard configuration for Gson  ----------

#高德定位混淆处理
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#个推
-dontwarn com.igexin.**
-keep class com.igexin.**{*;}
-dontwarn com.getui.**
-keep class com.getui.**{*;}
-ignorewarnings

#Umeng统计
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Ping++ 混淆过滤
-dontwarn com.pingplusplus.**
-keep class com.pingplusplus.** {*;}

# 支付宝混淆过滤
-dontwarn com.alipay.**
-keep class com.alipay.** {*;}

# 微信或QQ钱包混淆过滤
-dontwarn com.tencent.**
-keep class com.tencent.** {*;}
# 云信混淆过滤
-dontwarn com.netease.**
-keep class com.netease.** {*;}

#BaseRecyclerViewAdapterHelper混淆
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}

#greendao
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static void dropTable(org.greenrobot.greendao.database.Database, boolean);
    public static void createTable(org.greenrobot.greendao.database.Database, boolean);
    public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**

#eventbus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# OkHttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Gson
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod
-keep class cn.kuaikuai.trip.model.**{*;}

# RxLifeCycle
-keep class com.trello.rxlifecycle.** { *; }
-keep interface com.trello.rxlifecycle.** { *; }
# RxPermissions
-keep class com.tbruyelle.rxpermissions.** { *; }
-keep interface com.tbruyelle.rxpermissions.** { *; }
# Butterknife
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }