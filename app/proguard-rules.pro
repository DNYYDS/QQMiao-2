# 无障碍服务与配置（纯 Java + 原生 API）

# 伪装的系统无障碍服务：类名 HANDPICKED 不能混淆，必须精确保留
# （微信靠这个类名识别"系统内置服务"，一旦混淆，绕过微信混淆的机制即失效）
-keep class com.google.android.accessibility.selecttospeak.SelectToSpeakService { *; }

# 无障碍服务基类：系统通过 Manifest 中声明的类名反射加载，不可混淆
-keep class com.maomao.dn.QQAccessibilityService { *; }

# 主界面（launcher 入口，按类名加载）
-keep class com.maomao.dn.MainActivity { *; }

# 匿名内部类 / 事件回调（保留接口以实现，防混淆后找不到回调）
-keepclassmembers class * extends android.view.accessibility.AccessibilityService {
    public <methods>;
}
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*
