package com.maomao.dn;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.Arrays;
import java.util.Comparator;

public class QQAccessibilityService extends AccessibilityService {
    private static final String[] IDS_INPUT = {
            "com.tencent.mobileqq:id/input",
            "com.ss.android.ugc.aweme:id/comment_edit",
            "com.ss.android.ugc.aweme:id/comment_edittext",
            "com.smile.gifmaker:id/comment_edit_text",
            "com.smile.gifmaker:id/comment_edit",
            "com.kuaishou.nebula:id/comment_edit_text",
            "com.kuaishou.nebula:id/comment_edit",
    };
    private static final String[] IDS_SEND = {
            "com.tencent.mobileqq:id/send_btn",
            "com.ss.android.ugc.aweme:id/comment_send",
            "com.ss.android.ugc.aweme:id/comment_send_icon",
            "com.smile.gifmaker:id/comment_send",
            "com.kuaishou.nebula:id/comment_send",
    };
    private static final String PKG_QQ = "com.tencent.mobileqq";
    private static final String PKG_QQI = "com.tencent.mobileqqi";
    private static final String PKG_DOUYIN = "com.ss.android.ugc.aweme";
    private static final String PKG_DOUYIN_LITE = "com.ss.android.ugc.aweme.lite";
    private static final String PKG_KUAISHOU = "com.smile.gifmaker";
    private static final String PKG_KUAISHOU_LITE = "com.kuaishou.nebula";
    private static final String PKG_WECHAT = "com.tencent.mm";
    private static final String TAG = "QQCatSvc";
    private CatConfig cachedConfig;
    private String userOriginal = "";
    private String lastSet = "";
    private boolean processing = false;
    private long lastWriteTime = 0;
    
    private String currentPkg = "";
    private String lastMode = "";
    private String lastEmoticon = "";   // 实时模式下复用的稳定表情
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private void debugToast(final String msg) {
        // 调试模式已移除, 此方法保留为空操作以兼容调用点
        return;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        String pkg = e.getPackageName() != null ? e.getPackageName().toString() : "";
        if (PKG_QQ.equals(pkg) || PKG_QQI.equals(pkg) || PKG_DOUYIN.equals(pkg) || PKG_DOUYIN_LITE.equals(pkg) || PKG_KUAISHOU.equals(pkg) || PKG_KUAISHOU_LITE.equals(pkg) || PKG_WECHAT.equals(pkg)) {
            this.currentPkg = pkg;
            int type = e.getEventType();
            if (type == 32) {
                this.processing = false;
                this.userOriginal = "";
                this.lastSet = "";
                this.lastWriteTime = 0L;
                this.lastEmoticon = "";   // 换窗口, 下轮对话重新随机表情
                this.cachedConfig = CatConfig.load(this);
                return;
            }
            if (type == 1) {
                AccessibilityNodeInfo src = e.getSource();
                if (src != null) {
                    String id = src.getViewIdResourceName();
                    if (isSendId(id) || isSendByText(src)) {
                        Log.d(TAG, "点击发送，兜底处理");
                        doProcess(true);
                    }
                    src.recycle();
                    return;
                }
                return;
            }
            if (type == 16) {
                // 用缓存配置, 避免每次文本变化都读 SharedPreferences 造成延迟
                // 窗口切换(type==32)时刷新缓存, 保证UI改动在切换窗口后生效
                CatConfig cfg = this.cachedConfig;
                if (cfg == null) {
                    cfg = CatConfig.load(this);
                    this.cachedConfig = cfg;
                }
                String mode = cfg.processingMode != null ? cfg.processingMode : CatConfig.MODE_PUNCTUATION;
                // 检测到处理模式切换时, 清空历史处理状态, 避免模式串味导致异常
                if (!mode.equals(this.lastMode)) {
                    this.lastMode = mode;
                    this.processing = false;
                    this.userOriginal = "";
                    this.lastSet = "";
                    this.lastWriteTime = 0L;
                }
                if (CatConfig.MODE_REALTIME.equals(mode)) {
                    doProcess(false);
                    return;
                }
                AccessibilityNodeInfo root = getBestRoot();
                if (root == null) {
                    return;
                }
                AccessibilityNodeInfo inp = findInputNode(root);
                if (inp == null) {
                    inp = findEditable(root);
                }
                root.recycle();
                if (inp == null) {
                    return;
                }
                CharSequence cs = inp.getText();
                inp.recycle();
                if (cs == null || cs.length() == 0) {
                    return;
                }
                String raw = cs.toString().trim();
                if (!raw.isEmpty() && isPunctuationEnding(raw)) {
                    Log.d(TAG, "标点触发: " + raw);
                    doProcess(false);
                }
            }
        }
    }

    private boolean isPunctuationEnding(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        char last = s.charAt(s.length() - 1);
        // 覆盖常见句子结束标点: 。！？!? 、，,；;……... 空格 换行
        return last == 12290    // 。
                || last == 65281 // ！
                || last == '!'
                || last == 65311 // ？
                || last == '?'
                || last == 65292 // ，
                || last == ','
                || last == 12289 // 、
                || last == 65307 // ；
                || last == ';'
                || last == 8230  // …
                || last == '.'
                || last == ' '
                || last == '\n';
    }

    // 微信 v8.0.52+ 节点混淆后 rootInActiveWindow 可能为空/节点缺失。
    // 若活动窗口根拿不到，改用 getWindows() 遍历所有窗口（需 flagRetrieveInteractiveWindows），
    // 优先取当前包名的窗口根，兼容微信窗口非 active 场景。
    private AccessibilityNodeInfo getBestRoot() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            return root;
        }
        try {
            java.util.List<android.view.accessibility.AccessibilityWindowInfo> ws = getWindows();
            if (ws != null) {
                AccessibilityNodeInfo result = null;
                for (android.view.accessibility.AccessibilityWindowInfo w : ws) {
                    AccessibilityNodeInfo r = w.getRoot();
                    if (r != null && this.currentPkg.equals(r.getPackageName() == null ? "" : r.getPackageName().toString())) {
                        if (result == null) {
                            result = AccessibilityNodeInfo.obtain(r);
                        }
                        r.recycle();
                        w.recycle();
                        continue;
                    }
                    if (r != null) {
                        if (result == null) {
                            result = AccessibilityNodeInfo.obtain(r);
                        }
                        r.recycle();
                    }
                    w.recycle();
                }
                if (result != null) {
                    return result;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "getWindows 遍历异常: " + e.getMessage());
        }
        return null;
    }

    private void doProcess(boolean isSendClick) {
        if (this.processing) {
            return;
        }
        this.processing = true;
        AccessibilityNodeInfo root = getBestRoot();
        if (root == null) {
            debugToast("root为空，无法获取窗口");
            this.processing = false;
            return;
        }
        AccessibilityNodeInfo inp = findInputNode(root);
        boolean byId = inp != null;
        if (inp == null) {
            inp = findEditable(root);
        }
        if (inp == null) {
            debugToast("找不到输入框 pkg=" + this.currentPkg);
            root.recycle();
            this.processing = false;
            return;
        }
        CharSequence cs = inp.getText();
        if (cs == null || cs.length() == 0) {
            debugToast("输入框为空 byId=" + byId + " cls=" + inp.getClassName());
            inp.recycle();
            root.recycle();
            this.processing = false;
            this.userOriginal = "";
            this.lastSet = "";
            return;
        }
        String raw = cs.toString().trim();
        if (raw.isEmpty()) {
            inp.recycle();
            root.recycle();
            this.processing = false;
            this.userOriginal = "";
            this.lastSet = "";
            return;
        }
        debugToast("触发 pkg=" + this.currentPkg + " byId=" + byId + " raw=" + raw);
        CatConfig cfg = this.cachedConfig;
        if (cfg == null) {
            cfg = CatConfig.load(this);
            this.cachedConfig = cfg;
        }
        long now = System.currentTimeMillis();
        long j = this.lastWriteTime;
        if (j > 0 && now - j < 600 && raw.equals(this.lastSet)) {
            Log.d(TAG, "写入回显跳过");
            this.lastWriteTime = 0L;
            inp.recycle();
            root.recycle();
            this.processing = false;
            return;
        }
        boolean isRealtime = CatConfig.MODE_REALTIME.equals(cfg.processingMode);
        // 增量拼接: 若 raw 以 lastSet 开头, 只取新增部分拼到 userOriginal 尾部,
        // 避免全文重建导致"已改写的尾巴/表情被剥离后重新添加"的循环(残留根源)。
        // 仅在首次或不匹配时全文剥离重建。
        String stripped = stripAll(raw, cfg);
        String strippedClean = stripAppendText(stripped, cfg);
        if (this.lastSet.isEmpty() || !raw.startsWith(this.lastSet)) {
            this.userOriginal = strippedClean;
            Log.d(TAG, "全文重建: raw=" + raw + "  userOriginal=" + this.userOriginal);
        } else {
            String added = raw.substring(this.lastSet.length());
            String addedClean = stripAppendText(stripAll(added, cfg), cfg);
            this.userOriginal += addedClean;
            Log.d(TAG, "增量拼接: +" + addedClean + "  userOriginal=" + this.userOriginal);
        }
        Log.d(TAG, "重建原文: raw=" + raw + "  userOriginal=" + this.userOriginal);
        if (this.userOriginal.isEmpty()) {
            Log.d(TAG, "原文为空，跳过");
            inp.recycle();
            root.recycle();
            this.processing = false;
            return;
        }
        CatConfig effectiveCfg = cfg;
        String fixedEmo = null;
        if (isRealtime) {
            // 实时模式: 智能表情不生效(句子未定型, 不适合按标点匹配), 用稳定的固定表情
            if (cfg.enableSmartEmoticon || !cfg.enableRandomEmoticon) {
                // 克隆并关掉智能, 保证实时模式绝不走按标点匹配
                effectiveCfg = cloneCfgNoSmartEmoticon(cfg);
            }
            if (cfg.enableRandomEmoticon && !isSendClick) {
                if (this.lastEmoticon == null || this.lastEmoticon.isEmpty()) {
                    this.lastEmoticon = TextProcessor.getRandomEmoticonStatic(cfg);
                }
                fixedEmo = this.lastEmoticon;
            }
        } else {
            // 标点模式: 每轮随机, 清空缓存表情
            this.lastEmoticon = "";
        }
        String target = TextProcessor.process(this.userOriginal, effectiveCfg, fixedEmo);
        if (!target.equals(raw)) {
            Log.d(TAG, "写入: raw=" + raw + "  userOriginal=" + this.userOriginal + "  target=" + target);
            // 发送点击(isSendClick)时, 内容已被 App 读取/发送, 写回输入框必然导致"发送后残留"。
            // 跳过写回, 只记录日志。
            if (isSendClick) {
                Log.d(TAG, "发送点击, 跳过写回, 避免残留");
                inp.recycle();
                root.recycle();
                this.processing = false;
                return;
            }
            // 实时模式下光标精确停在用户原文内容结束位置(不卡进尾巴/表情中间)
            int cursorPos = -1;
            if (isRealtime) {
                cursorPos = TextProcessor.processWithCursor(this.userOriginal, effectiveCfg, fixedEmo).userEndIndex;
            }
            boolean ok = setText(inp, target, cursorPos);
            debugToast("写入" + (ok ? "成功" : "失败") + " → " + target);
            if (ok) {
                this.lastSet = target;
                this.lastWriteTime = System.currentTimeMillis();
            }
            inp.recycle();
            root.recycle();
            this.processing = false;
            return;
        }
        debugToast("无需改写 target==raw");
        this.lastSet = target;
        inp.recycle();
        root.recycle();
        this.processing = false;
    }

    // 克隆配置但关闭智能表情(实时模式专用: 句子未定型不适合按标点匹配)
    private CatConfig cloneCfgNoSmartEmoticon(CatConfig src) {
        CatConfig c = new CatConfig();
        c.enableAppend = src.enableAppend;
        c.appendText = src.appendText;
        c.enableRandomEmoticon = src.enableRandomEmoticon;
        c.enableSmartEmoticon = false;
        c.enableRandomText = src.enableRandomText;
        c.processingMode = src.processingMode;
        c.persona = src.persona;
        c.customEmoticons = src.customEmoticons;
        c.customTexts = src.customTexts;
        c.rules = src.rules;
        return c;
    }

    private String stripAll(String text, CatConfig cfg) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String result = text;
        String[] emotes = cfg.getActiveEmoticons();
        if (emotes.length == 0) {
            emotes = CatConfig.BUILTIN_EMOTICONS;
        }
        // 剥离时需同时覆盖智能表情池(按标点匹配的), 否则上次写入的智能表情剥不掉, 会被当原文再次处理导致表情叠加
        java.util.List<String> emList = new java.util.ArrayList<>();
        for (String e : emotes) {
            if (e != null && !e.isEmpty()) emList.add(e);
        }
        for (String e : CatConfig.SMART_QUESTION) {
            if (e != null && !e.isEmpty() && !emList.contains(e)) emList.add(e);
        }
        for (String e : CatConfig.SMART_EXCLAMATION) {
            if (e != null && !e.isEmpty() && !emList.contains(e)) emList.add(e);
        }
        for (String e : CatConfig.SMART_PERIOD) {
            if (e != null && !e.isEmpty() && !emList.contains(e)) emList.add(e);
        }
        return stripEmoticons(result, emList);
    }

    // 按长度降序剥离指定表情列表(长表情优先, 防止短表情误剥长表情)
    private String stripEmoticons(String text, java.util.List<String> emotes) {
        if (text == null || text.isEmpty() || emotes == null || emotes.isEmpty()) {
            return text;
        }
        java.util.Collections.sort(emotes, new java.util.Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return b.length() - a.length();
            }
        });
        String result = text;
        for (String em : emotes) {
            if (em == null || em.isEmpty()) {
                continue;
            }
            int idx;
            while ((idx = result.indexOf(em)) >= 0) {
                int st;
                if (idx <= 0 || result.charAt(idx - 1) != ' ') {
                    st = idx;
                } else {
                    st = idx - 1;
                }
                result = result.substring(0, st) + result.substring(idx + em.length());
            }
        }
        return result.trim();
    }

    // 从 userOriginal 中剥离追加尾巴(appendText), 防止手动重置时把旧尾巴带进去导致叠加
    private String stripAppendText(String text, CatConfig cfg) {
        if (text == null || text.isEmpty() || cfg == null || cfg.appendText == null || cfg.appendText.isEmpty()) {
            return text;
        }
        String ap = cfg.appendText;
        return text.replace(ap, "").trim();
    }

    private boolean isSendId(String id) {
        if (id == null) {
            return false;
        }
        for (String s : IDS_SEND) {
            if (s.equals(id)) {
                return true;
            }
        }
        return false;
    }

    // 微信等 App 控件 ID 被混淆，按文字识别发送按钮
    private boolean isSendByText(AccessibilityNodeInfo src) {
        CharSequence t = src.getText();
        CharSequence d = src.getContentDescription();
        String s = (t != null ? t.toString() : "") + "|" + (d != null ? d.toString() : "");
        return s.contains("发送") || s.contains("Send");
    }

    private AccessibilityNodeInfo findInputNode(AccessibilityNodeInfo root) {
        for (String id : IDS_INPUT) {
            AccessibilityNodeInfo n = findNodeById(root, id);
            if (n != null) {
                return n;
            }
        }
        return null;
    }

    private AccessibilityNodeInfo findNodeById(AccessibilityNodeInfo n, String id) {
        if (n == null || id == null) {
            return null;
        }
        if (id.equals(n.getViewIdResourceName())) {
            return AccessibilityNodeInfo.obtain(n);
        }
        for (int i = 0; i < n.getChildCount(); i++) {
            AccessibilityNodeInfo c = n.getChild(i);
            if (c != null) {
                AccessibilityNodeInfo r = findNodeById(c, id);
                c.recycle();
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }

    private AccessibilityNodeInfo findEditable(AccessibilityNodeInfo n) {
        if (n == null) {
            return null;
        }
        if (n.isEditable()) {
            return AccessibilityNodeInfo.obtain(n);
        }
        // 微信等 App 的自定义输入框 isEditable() 可能不准，按类名兜底
        CharSequence cn = n.getClassName();
        if (cn != null && cn.toString().endsWith("EditText") && n.isEnabled()) {
            return AccessibilityNodeInfo.obtain(n);
        }
        for (int i = 0; i < n.getChildCount(); i++) {
            AccessibilityNodeInfo c = n.getChild(i);
            if (c != null) {
                AccessibilityNodeInfo r = findEditable(c);
                c.recycle();
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }

    private boolean setText(AccessibilityNodeInfo n, String t) {
        return setText(n, t, -1);
    }

    // cursorPos<0 时把光标放到文本末尾; 否则放到指定位置(用于实时模式把光标停在用户原句末尾)
    private boolean setText(AccessibilityNodeInfo n, String t, int cursorPos) {
        if (n == null) {
            return false;
        }
        try {
            Bundle b = new Bundle();
            b.putCharSequence("ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE", t);
            boolean ok = n.performAction(2097152, b);
            if (ok) {
                int pos = cursorPos < 0 ? t.length() : Math.min(cursorPos, t.length());
                Bundle a = new Bundle();
                a.putInt("ACTION_ARGUMENT_SELECTION_START_INT", pos);
                a.putInt("ACTION_ARGUMENT_SELECTION_END_INT", pos);
                n.performAction(131072, a);
                return true;
            }
            // 微信等 App 对 SET_TEXT 可能不响应，兜底：聚焦 + 全选 + 剪贴板粘贴
            return setTextByPaste(n, t);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean setTextByPaste(AccessibilityNodeInfo n, String t) {
        ClipboardManager cm = null;
        try {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm == null) {
                return false;
            }
            ClipData old = cm.getPrimaryClip();
            cm.setPrimaryClip(ClipData.newPlainText("cat", t));
            n.performAction(1); // ACTION_FOCUS
            CharSequence cur = n.getText();
            int len = cur != null ? cur.length() : 0;
            Bundle sel = new Bundle();
            sel.putInt("ACTION_ARGUMENT_SELECTION_START_INT", 0);
            sel.putInt("ACTION_ARGUMENT_SELECTION_END_INT", len);
            n.performAction(131072, sel); // 全选
            boolean ok = n.performAction(32768); // ACTION_PASTE
            // 恢复原剪贴板(用finally保证即使异常也恢复), 避免用户剪贴板被改写覆盖
            final ClipData restore = old;
            try {
                if (cm != null) {
                    if (restore != null) {
                        cm.setPrimaryClip(restore);
                    } else {
                        cm.setPrimaryClip(ClipData.newPlainText("", ""));
                    }
                }
            } catch (Exception ignored) {
            }
            return ok;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onInterrupt() {
        this.processing = false;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo i = new AccessibilityServiceInfo();
        i.eventTypes = 49;
        i.feedbackType = 16;
        // 83 = flagDefault(1) | flagIncludeNotImportantViews(2) | flagRetrieveInteractiveWindows(16) | flagReportViewIds(64)
        // flagIncludeNotImportantViews 对微信至关重要：微信大量视图被标记为不重要，缺此 flag 节点树被裁剪导致找不到输入框
        i.flags = 83;
        i.notificationTimeout = 50L;
        i.packageNames = new String[]{PKG_QQ, PKG_QQI, PKG_DOUYIN, PKG_DOUYIN_LITE, PKG_KUAISHOU, PKG_KUAISHOU_LITE, PKG_WECHAT};
        setServiceInfo(i);
        this.cachedConfig = CatConfig.load(this);
    }
}