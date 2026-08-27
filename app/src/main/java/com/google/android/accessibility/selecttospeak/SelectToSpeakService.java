package com.google.android.accessibility.selecttospeak;

import com.maomao.dn.QQAccessibilityService;

/**
 * 伪装成系统内置的无障碍服务（SelectToSpeakService）。
 *
 * 背景：微信 v8.0.52 及以上版本对第三方无障碍服务（AccessibilityService）返回的
 * 节点信息做了混淆/打乱，导致 getRootInActiveWindow / findAccessibilityNodeInfos* 失效。
 * 社区验证的解决方案是：将服务注册为与系统内置服务相同的包名+类名，
 * 微信检测到"系统内置服务"后不再混淆节点。
 *
 * 此类的包名+类名即为伪装身份，实际逻辑全部继承自 QQAccessibilityService。
 */
public class SelectToSpeakService extends QQAccessibilityService {
}
