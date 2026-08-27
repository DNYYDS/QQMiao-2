package com.maomao.dn;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    // ===== 方案A · 现代简约·浅色大留白 =====
    private static final int C_BG = 0xFFF7F3EE;           // 米白背景
    private static final int C_CARD = 0xFFFFFFFF;          // 白色卡片
    private static final int C_TEXT = 0xFF3E2A20;          // 深棕主文本
    private static final int C_TEXT_SUB = 0xFF9A857A;      // 次级文本
    private static final int C_DIVIDER = 0xFFF0E8E2;       // 分隔线
    private static final int C_ACCENT = 0xFFFF7043;        // 强调橙
    private static final int C_GREEN = 0xFF43D17A;         // 服务开启绿
    private static final int C_RED = 0xFFE57368;           // 服务关闭红
    private static final int C_INPUT_BG = 0xFFF6F1EC;      // 输入框底
    private static final int C_ICON_C1 = 0xFFFFF0E5;       // 图标块-浅橙
    private static final int C_ICON_C2 = 0xFFE8F6EC;       // 图标块-浅绿
    private static final int C_ICON_C3 = 0xFFECEDFF;       // 图标块-浅紫
    private static final int C_ICON_C4 = 0xFFFFF6E0;       // 图标块-浅黄

    private CatConfig config;
    private CheckBox cbAppend, cbEmoticon, cbSmart, cbRandomText, rbPunctuation, rbRealtime;
    private EditText etAppendText, etRules, etCustomEmoticons, etCustomTexts;
    private int selectedPersona = 0;
    private TextView statusText;
    private Button toggleButton;
    private final Handler statusPollHandler = new Handler(Looper.getMainLooper());
    private boolean statusPollRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.config = CatConfig.load(this);
        } catch (Exception e) {
            this.config = new CatConfig();
        }

        // ===== 浅色背景(方案A) =====
        getWindow().setStatusBarColor(C_BG);
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        scrollView.setBackgroundColor(C_BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, 0, 0, 36);
        root.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));

        // ===== 顶部 =====
        root.addView(header());

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(18, 0, 18, 0);
        body.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        // ===== 状态卡片 =====
        body.addView(statusCard());

        // ===== 处理模式 =====
        this.rbPunctuation = new CheckBox(this);
        this.rbPunctuation.setChecked(CatConfig.MODE_PUNCTUATION.equals(this.config.processingMode));
        this.rbRealtime = new CheckBox(this);
        this.rbRealtime.setChecked(CatConfig.MODE_REALTIME.equals(this.config.processingMode));
        LinearLayout modeBox = new LinearLayout(this);
        modeBox.setOrientation(LinearLayout.VERTICAL);
        modeBox.addView(modeRow(this.rbPunctuation, this.rbRealtime, "标点触发", "打字时在标点处立即处理", C_ACCENT));
        modeBox.addView(rowDivider());
        modeBox.addView(modeRow(this.rbRealtime, this.rbPunctuation, "实时处理", "每输入一个字立即处理", C_ACCENT));
        body.addView(sectionCard("处理模式", modeBox));

        // ===== 人格 =====
        this.selectedPersona = Math.max(0, CatConfig.personaIndex(this.config.persona));
        body.addView(sectionCard("人格", personaContent()));

        // ===== 功能开关 =====
        this.cbAppend = new CheckBox(this);
        this.cbAppend.setChecked(this.config.enableAppend);
        this.cbEmoticon = new CheckBox(this);
        this.cbEmoticon.setChecked(this.config.enableRandomEmoticon);
        this.cbSmart = new CheckBox(this);
        this.cbSmart.setChecked(this.config.enableSmartEmoticon);
        this.cbRandomText = new CheckBox(this);
        this.cbRandomText.setChecked(this.config.enableRandomText);

        LinearLayout feature = new LinearLayout(this);
        feature.setOrientation(LinearLayout.VERTICAL);
        feature.addView(switchRow("断句追加", "分句后在句末追加文本", this.cbAppend, "🫧", C_ICON_C1));
        feature.addView(rowDivider());
        feature.addView(appendInputRow());
        feature.addView(rowDivider());
        feature.addView(switchRow("句末颜文字", "消息末尾附加随机颜文字", this.cbEmoticon, "😺", C_ICON_C3));
        feature.addView(rowDivider());
        feature.addView(switchRow("智能表情", "按结尾标点配颜文字，自定义的话则不会生效（仅标点触发模式）", this.cbSmart, "🧠", C_ICON_C4));
        feature.addView(rowDivider());
        feature.addView(switchRow("表情附加词", "颜文字后再加一个小括号词，如 (蹭蹭)", this.cbRandomText, "💬", C_ICON_C1));
        body.addView(sectionCard("功能开关", feature));

        // ===== 替换规则 =====
        body.addView(sectionCard("文本替换规则", rulesContent()));

        // ===== 颜文字 =====
        body.addView(sectionCard("自定义颜文字", emojiContent()));

        // ===== 附加词 =====
        body.addView(sectionCard("自定义附加词", textContent()));

        // ===== 测试按钮(无保存按钮, 全实时保存) =====
        body.addView(testButton());
        TextView hint = new TextView(this);
        hint.setText("所有改动实时自动保存 · 服务下次触发即生效");
        hint.setTextSize(12.0f);
        hint.setTextColor(C_TEXT_SUB);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, 22, 0, 0);
        body.addView(hint);

        root.addView(body);
        scrollView.addView(root);
        setContentView(scrollView);
    }

    private View header() {
        // 方案A: 浅色大留白, 顶部居中猫图标 + 大标题 + 副标题, 无大色块
        LinearLayout h = new LinearLayout(this);
        h.setOrientation(LinearLayout.VERTICAL);
        h.setGravity(Gravity.CENTER_HORIZONTAL);
        h.setPadding(24, 26, 24, 26);
        h.setBackgroundColor(C_BG);

        TextView mascot = new TextView(this);
        mascot.setText("🐱");
        mascot.setTextSize(52.0f);
        h.addView(mascot);

        TextView title = new TextView(this);
        title.setText("喵喵助手");
        title.setTextSize(27.0f);
        title.setTextColor(C_TEXT);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 6, 0, 2);
        h.addView(title);

        TextView sub = new TextView(this);
        sub.setText("文本改写 · 断句加成 · 颜文字");
        sub.setTextSize(13.0f);
        sub.setTextColor(C_TEXT_SUB);
        sub.setGravity(Gravity.CENTER);
        h.addView(sub);
        return h;
    }

    private View spacer() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1.0f));
        return v;
    }

    private View statusCard() {
        LinearLayout card = whiteCard();
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        // 状态行: 圆点 + 状态文字 + flex + 设置按钮
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView dot = new TextView(this);
        this.statusText = new TextView(this);
        this.statusText.setTextSize(15.0f);
        this.statusText.setTypeface(Typeface.DEFAULT_BOLD);
        dot.setText("●");
        dot.setTextSize(18.0f);
        top.addView(dot);
        this.statusText.setPadding(dp(6), 0, 0, 0);
        top.addView(this.statusText, new LinearLayout.LayoutParams(0, -2, 1.0f));
        card.addView(top);

        TextView sub = new TextView(this);
        sub.setText("无障碍服务 · 控制面板");
        sub.setTextSize(12.0f);
        sub.setTextColor(C_TEXT_SUB);
        sub.setPadding(dp(2), 2, 0, 14);
        card.addView(sub);

        this.toggleButton = new Button(this);
        this.toggleButton.setTextSize(15.0f);
        this.toggleButton.setTextColor(Color.WHITE);
        this.toggleButton.setAllCaps(false);
        this.toggleButton.setTypeface(Typeface.DEFAULT_BOLD);
        this.toggleButton.setPadding(0, dp(13), 0, dp(13));
        this.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { MainActivity.this.openAccessibilitySettings(); }
        });
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(100));
        bg.setColor(C_ACCENT);
        this.toggleButton.setBackground(bg);
        card.addView(this.toggleButton);
        updateServiceStatus();
        return card;
    }

    private View sectionCard(String title, View content) {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams wlp = new LinearLayout.LayoutParams(-1, -2);
        wlp.setMargins(0, dp(18), 0, 0);
        wrap.setLayoutParams(wlp);

        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(14.0f);
        t.setTextColor(C_TEXT);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setPadding(dp(4), 0, 0, dp(9));
        wrap.addView(t);

        LinearLayout card = whiteCard();
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(8), dp(14), dp(8));
        if (content != null) card.addView(content);
        wrap.addView(card);
        return wrap;
    }

    private View personaContent() {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setPadding(dp(14), dp(12), dp(14), dp(12));

        // 标题 + 说明: 参考图片 /thinking 样式(左标题, 下行小字说明)
        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.VERTICAL);
        head.setPadding(0, 0, 0, dp(10));
        TextView t = new TextView(this);
        t.setText("人格");
        t.setTextSize(15.0f);
        t.setTextColor(C_TEXT);
        head.addView(t);
        TextView d = new TextView(this);
        d.setText("点击切换 — 默认只影响尾巴与附加词, 你自定义的仍优先");
        d.setTextSize(12.0f);
        d.setTextColor(C_TEXT_SUB);
        head.addView(d);
        col.addView(head);

        // 分段按钮组: 参考图片"低|中|高|超高"的圆角分段样式
        col.addView(buildPersonaSegments());
        return col;
    }

    private View buildPersonaSegments() {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable outer = new GradientDrawable();
        outer.setCornerRadius(dp(12));
        outer.setColor(0xFFF1ECE6); // 浅灰底分段容器
        wrap.setBackground(outer);
        wrap.setPadding(dp(4), dp(4), dp(4), dp(4));

        String[] labels = CatConfig.PERSONA_LABELS;
        String[] keys = CatConfig.PERSONA_KEYS;
        int half = (labels.length + 1) / 2;
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        final List<TextView> segViews = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            TextView seg = new TextView(this);
            seg.setText(labels[i]);
            seg.setTextSize(13.0f);
            seg.setGravity(Gravity.CENTER);
            seg.setPadding(dp(2), dp(8), dp(2), dp(8));
            seg.setTextColor(C_TEXT_SUB);
            seg.setClickable(true);
            seg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.this.selectedPersona = idx;
                    MainActivity.this.config.persona = CatConfig.PERSONA_KEYS[idx];
                    MainActivity.this.saveConfig();
                    MainActivity.this.refreshPersonaSegments(segViews);
                }
            });
            segViews.add(seg);
            (i < half ? row1 : row2).addView(seg, new LinearLayout.LayoutParams(0, -2, 1.0f));
        }
        wrap.addView(row1);
        wrap.addView(row2);
        // 刷新初始选中态
        refreshPersonaSegments(segViews);
        return wrap;
    }

    private void refreshPersonaSegments(List<TextView> segs) {
        GradientDrawable on = new GradientDrawable();
        on.setCornerRadius(dp(9));
        on.setColor(C_ACCENT);
        GradientDrawable off = new GradientDrawable();
        off.setCornerRadius(dp(9));
        off.setColor(0xFFF1ECE6);
        for (int i = 0; i < segs.size(); i++) {
            TextView seg = segs.get(i);
            boolean sel = (i == this.selectedPersona);
            seg.setBackground(sel ? on : off);
            seg.setTextColor(sel ? Color.WHITE : C_TEXT_SUB);
            seg.setTypeface(sel ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        }
    }

    private View modeRow(final CheckBox cb, final CheckBox opponent, String title, String desc, int color) {
        final LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(12), dp(14), dp(12));
        if (cb.isChecked()) applyModeSelected(row, color);
        row.addView(cb, new LinearLayout.LayoutParams(1, 1));
        cb.setAlpha(0f);
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(15.0f);
        t.setTextColor(C_TEXT);
        col.addView(t);
        TextView d = new TextView(this);
        d.setText(desc);
        d.setTextSize(12.0f);
        d.setTextColor(C_TEXT_SUB);
        col.addView(d);
        row.addView(col, new LinearLayout.LayoutParams(0, -2, 1.0f));

        // 选中指示点
        final TextView dot = new TextView(this);
        dot.setText("●");
        dot.setTextSize(16.0f);
        dot.setTextColor(cb.isChecked() ? color : 0xFFD8D0C9);
        dot.setPadding(0, 0, dp(4), 0);
        row.addView(dot);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton b, boolean c) {
                dot.setTextColor(c ? color : 0xFFD8D0C9);
                applyModeSelected(row, c ? color : 0);
                if (c) MainActivity.this.saveConfig(); // 选中即保存并提示
            }
        });
        // 点击整行选中该项（严格单选: 先取消对手再选中自己, 避免保存时读到旧值）
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb.isChecked()) {
                    opponent.setChecked(false);
                    cb.setChecked(true);
                }
            }
        });
        return row;
    }

    private void applyModeSelected(LinearLayout row, int color) {
        if (row == null) return;
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(13));
        if (color != 0) {
            bg.setColor(0x12FF7043); // 选中浅橙底
            bg.setStroke(dp(1), 0xFFFF7043);
        } else {
            bg.setColor(0x00000000);
            bg.setStroke(0, 0x00000000);
        }
        row.setBackground(bg);
    }

    private LinearLayout switchRow(final String title, String desc, final CheckBox cb, String icon, int color) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(12), 0, dp(12));

        // 浅色图标块(方案A: 彩色浅底+深色图标)
        TextView iv = new TextView(this);
        iv.setText(icon);
        iv.setTextSize(20.0f);
        iv.setGravity(Gravity.CENTER);
        GradientDrawable ico = new GradientDrawable();
        ico.setCornerRadius(dp(13));
        ico.setColor(color); // 传入浅色背景
        iv.setBackground(ico);
        iv.setLayoutParams(new LinearLayout.LayoutParams(dp(40), dp(40)));
        row.addView(iv);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        col.setPadding(dp(13), 0, 0, 0);
        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(15.0f);
        t.setTextColor(C_TEXT);
        col.addView(t);
        TextView d = new TextView(this);
        d.setText(desc);
        d.setTextSize(12.0f);
        d.setTextColor(C_TEXT_SUB);
        col.addView(d);
        row.addView(col);

        // 开关(方案A: 橙色轨道)
        Switch sw = new Switch(this);
        sw.setChecked(cb.isChecked());
        sw.setThumbTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));
        sw.setTrackTintList(android.content.res.ColorStateList.valueOf(cb.isChecked() ? C_ACCENT : 0xFFD8D0C9));
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton b, boolean c) {
                cb.setChecked(c);
                if (b instanceof Switch) {
                    ((Switch) b).setTrackTintList(android.content.res.ColorStateList.valueOf(c ? C_ACCENT : 0xFFD8D0C9));
                }
                MainActivity.this.saveConfig(); // 拨动即保存并提示
            }
        });
        final Switch swRef = sw;
        row.addView(sw, new LinearLayout.LayoutParams(-2, -2));
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { swRef.setChecked(!swRef.isChecked()); }
        });
        return row;
    }

    private View appendInputRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, dp(12), 0, dp(12));
        TextView t = new TextView(this);
        t.setText("追加内容");
        t.setTextSize(15.0f);
        t.setTextColor(C_TEXT);
        row.addView(t);
        this.etAppendText = new EditText(this);
        etAppendText.setText(this.config.appendText != null ? this.config.appendText : "喵");
        styleInput(etAppendText, "默认：喵~");
        row.addView(etAppendText);
        return row;
    }

    private View rulesContent() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        TextView hint = new TextView(this);
        hint.setText("每行一条，按顺序应用。格式：原词=替换词（支持 ＝ / →）\n例：我=本喵 · 你＝主人");
        hint.setTextSize(12.0f);
        hint.setTextColor(C_TEXT_SUB);
        hint.setLineSpacing(0, 1.2f);
        hint.setPadding(0, dp(8), 0, dp(10));
        box.addView(hint);
        this.etRules = new EditText(this);
        etRules.setInputType(0x20001);
        etRules.setText(CatConfig.rulesToString(this.config.rules));
        styleInput(etRules, "输入替换规则…");
        etRules.setMaxLines(500);
        box.addView(etRules);
        return box;
    }

    private View emojiContent() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        TextView hint = new TextView(this);
        hint.setText("每行一个颜文字，留空则使用内置库");
        hint.setTextSize(12.0f);
        hint.setTextColor(C_TEXT_SUB);
        hint.setPadding(0, dp(8), 0, dp(10));
        box.addView(hint);
        this.etCustomEmoticons = new EditText(this);
        etCustomEmoticons.setInputType(0x20001);
        etCustomEmoticons.setText(joinLines(this.config.customEmoticons));
        styleInput(etCustomEmoticons, "例如 (=^w^=)");
        etCustomEmoticons.setMaxLines(500);
        box.addView(etCustomEmoticons);
        return box;
    }

    private View textContent() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        TextView hint = new TextView(this);
        hint.setText("每行一个词，会填进表情后的小括号 (词)。留空则使用内置猫娘词池");
        hint.setTextSize(12.0f);
        hint.setTextColor(C_TEXT_SUB);
        hint.setLineSpacing(0, 1.2f);
        hint.setPadding(0, dp(8), 0, dp(10));
        box.addView(hint);
        this.etCustomTexts = new EditText(this);
        etCustomTexts.setInputType(0x20001);
        etCustomTexts.setText(joinLines(this.config.customTexts));
        styleInput(etCustomTexts, "例如 蹭蹭");
        etCustomTexts.setMaxLines(500);
        box.addView(etCustomTexts);
        return box;
    }

    private void styleInput(EditText et, String hint) {
        et.setTextSize(14.0f);
        et.setTextColor(C_TEXT);
        et.setHintTextColor(C_TEXT_SUB);
        et.setPadding(dp(14), dp(12), dp(14), dp(12));
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(13));
        bg.setColor(C_INPUT_BG);
        bg.setStroke(dp(1), C_DIVIDER);
        et.setBackground(bg);
        et.setMinHeight(dp(48));
        // 实时保存: 输入即静默保存, 无需点下面的保存按钮
        et.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                MainActivity.this.saveConfigSilent();
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private View testButton() {
        Button btn = new Button(this);
        btn.setText("测试当前配置");
        btn.setTextSize(14.5f);
        btn.setTextColor(C_ACCENT);
        btn.setAllCaps(false);
        btn.setPadding(0, dp(15), 0, dp(15));
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(100));
        bg.setColor(C_CARD);
        bg.setStroke(dp(1), C_ACCENT);
        btn.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(12), 0, 0);
        btn.setLayoutParams(lp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { MainActivity.this.showTestDialog(); }
        });
        return btn;
    }

    private LinearLayout whiteCard() {
        LinearLayout c = new LinearLayout(this);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(18));
        bg.setColor(C_CARD);
        bg.setStroke(dp(1), C_DIVIDER);
        c.setBackground(bg);
        c.setElevation(dp(1));
        return c;
    }

    private View rowDivider() {
        View v = new View(this);
        v.setBackgroundColor(C_DIVIDER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 1);
        lp.setMargins(0, 2, 0, 2);
        v.setLayoutParams(lp);
        return v;
    }

    private int dp(float v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    // ===== 服务状态 =====
    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
        // 兜底轮询: 防止从系统设置返回时 onResume 未及时触发, 导致状态卡不同步
        if (!this.statusPollRunning) {
            this.statusPollRunning = true;
            this.statusPollHandler.postDelayed(this.statusPollRunnable, 2000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.statusPollRunning = false;
        this.statusPollHandler.removeCallbacks(this.statusPollRunnable);
    }

    private final Runnable statusPollRunnable = new Runnable() {
        @Override
        public void run() {
            updateServiceStatus();
            if (MainActivity.this.statusPollRunning) {
                MainActivity.this.statusPollHandler.postDelayed(this, 2000);
            }
        }
    };

    private void updateServiceStatus() {
        if (this.statusText == null || this.toggleButton == null) return;
        boolean enabled = isAccessibilityServiceEnabled();
        if (enabled) {
            this.statusText.setText("服务已开启");
            this.statusText.setTextColor(C_GREEN);
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dp(100));
            bg.setColor(C_GREEN);
            this.toggleButton.setBackground(bg);
            this.toggleButton.setText("服务运行中");
            this.toggleButton.setTextColor(Color.WHITE);
            this.toggleButton.setEnabled(false);
            this.toggleButton.setAlpha(0.92f);
        } else {
            this.statusText.setText("服务未开启");
            this.statusText.setTextColor(C_RED);
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dp(100));
            bg.setColor(C_ACCENT);
            this.toggleButton.setBackground(bg);
            this.toggleButton.setText("前往开启无障碍服务");
            this.toggleButton.setTextColor(Color.WHITE);
            this.toggleButton.setEnabled(true);
            this.toggleButton.setAlpha(1f);
        }
    }

    private boolean isAccessibilityServiceEnabled() {
        try {
            AccessibilityManager am = (AccessibilityManager) getSystemService("accessibility");
            if (am == null) return false;
            List<AccessibilityServiceInfo> services = am.getEnabledAccessibilityServiceList(-1);
            String pkg = getPackageName();
            for (AccessibilityServiceInfo info : services) {
                if (info.getResolveInfo() != null && info.getResolveInfo().serviceInfo != null
                        && pkg.equals(info.getResolveInfo().serviceInfo.packageName)) {
                    // 校验服务类名: 本应用注册的是伪装成系统内置的 SelectToSpeakService
                    String cname = info.getResolveInfo().serviceInfo.name;
                    if (cname != null && (cname.contains("SelectToSpeakService") || cname.contains("QQAccessibilityService"))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) { }
        return false;
    }

    public void openAccessibilitySettings() {
        try {
            Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
            intent.setFlags(268435456);
            startActivity(intent);
        } catch (Exception e) {
            shortToast("无法打开设置");
        }
    }

    private String joinLines(String[] arr) {
        if (arr == null) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            if (s == null) continue;
            String t = s.trim();
            if (t.isEmpty()) continue;
            if (sb.length() > 0) sb.append("\n");
            sb.append(t);
        }
        return sb.toString();
    }

    public void saveConfig() {
        boolean ok = persistConfig();
        if (!ok) { shortToast("保存失败"); return; }
        shortToast("设置已保存");
    }

    // 自定义短提示: 悬浮小条, 精确控制时长(默认0.4秒), 替代系统Toast
    private android.view.View toastOverlay = null;
    private void shortToast(String msg) {
        try {
            android.view.View decorV = getWindow().getDecorView();
            if (!(decorV instanceof android.widget.FrameLayout)) { Toast.makeText(this, msg, 0).show(); return; }
            final android.widget.FrameLayout decor = (android.widget.FrameLayout) decorV;
            // 移除旧的
            if (toastOverlay != null) {
                try { decor.removeView(toastOverlay); } catch (Exception e) {}
                toastOverlay = null;
            }
            android.widget.FrameLayout overlay = new android.widget.FrameLayout(this);
            TextView tv = new TextView(this);
            tv.setText(msg);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(13.5f);
            tv.setPadding(dp(18), dp(9), dp(18), dp(9));
            tv.setBackground(new GradientDrawable() { { setCornerRadius(dp(100)); setColor(0xE6221B1C); } });
            tv.setElevation(dp(8));
            tv.setAlpha(0.0f);
            android.widget.FrameLayout.LayoutParams tlp = new android.widget.FrameLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.Gravity.TOP | android.view.Gravity.CENTER_HORIZONTAL);
            tlp.topMargin = dp(90);
            overlay.addView(tv, tlp);
            overlay.setClickable(false);
            overlay.setFocusable(false);
            overlay.setLayoutParams(new android.view.ViewGroup.LayoutParams(-1, -1));
            decor.addView(overlay);
            tv.animate().alpha(1f).setDuration(100).start();
            this.toastOverlay = overlay;
            final android.view.View ov = overlay;
            final android.os.Handler h = new android.os.Handler(android.os.Looper.getMainLooper());
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try { decor.removeView(ov); } catch (Exception e) {}
                    if (toastOverlay == ov) toastOverlay = null;
                }
            }, 400); // 0.4秒
        } catch (Exception e) {
            Toast.makeText(this, msg, 0).show();
        }
    }

    // 静默保存：只落盘+写配置，不弹Toast，用于文本框输入等频繁触发
    public void saveConfigSilent() {
        persistConfig();
    }


    private boolean persistConfig() {
        try {
            this.config.enableAppend = this.cbAppend != null && this.cbAppend.isChecked();
            String append = this.etAppendText.getText().toString().trim();
            this.config.appendText = append.isEmpty() ? "喵~" : append;
            this.config.enableRandomEmoticon = this.cbEmoticon != null && this.cbEmoticon.isChecked();
            this.config.enableSmartEmoticon = this.cbSmart != null && this.cbSmart.isChecked();
            this.config.enableRandomText = this.cbRandomText != null && this.cbRandomText.isChecked();
            // 处理模式: 以"标点"开关为权威(click回调整已保证互斥), 避免读到未取消的旧值
            boolean punct = this.rbPunctuation != null && this.rbPunctuation.isChecked();
            this.config.processingMode = punct ? CatConfig.MODE_PUNCTUATION : CatConfig.MODE_REALTIME;

            ArrayList<CatConfig.Rule> rules = new ArrayList<>();
            String rulesText = this.etRules.getText() == null ? "" : this.etRules.getText().toString();
            for (String line : rulesText.split("\n")) {
                CatConfig.Rule r = CatConfig.parseRule(line);
                if (r != null) rules.add(r);
            }
            this.config.rules = rules;

            ArrayList<String> list = new ArrayList<>();
            String customText = this.etCustomEmoticons.getText() == null ? "" : this.etCustomEmoticons.getText().toString().trim();
            if (!customText.isEmpty()) {
                for (String raw : customText.split("\n")) {
                    String t = raw.trim();
                    if (!t.isEmpty()) list.add(t);
                }
            }
            this.config.customEmoticons = list.toArray(new String[0]);

            ArrayList<String> tlist = new ArrayList<>();
            String customTxt = this.etCustomTexts.getText() == null ? "" : this.etCustomTexts.getText().toString().trim();
            if (!customTxt.isEmpty()) {
                for (String raw : customTxt.split("\n")) {
                    String t = raw.trim();
                    if (!t.isEmpty()) tlist.add(t);
                }
            }
            this.config.customTexts = tlist.toArray(new String[0]);
            this.config.save(this);
            return true;
        } catch (Exception e) {
            // 静默失败, 交由调用方决定是否提示
            return false;
        }
    }

    public void showTestDialog() {
        try {
            saveConfig();
            CatConfig testCfg = CatConfig.load(this);
            String sample = "今天我很好，你准备好了吗？我们去公园玩吧";
            String processed = TextProcessor.process(sample, testCfg);
            String msg = "断句追加：" + yn(testCfg.enableAppend) + "（" + (testCfg.appendText == null ? "" : testCfg.appendText) + "）"
                    + "\n句末颜文字：" + yn(testCfg.enableRandomEmoticon)
                    + "\n表情附加词：" + yn(testCfg.enableRandomText) + "（" + (testCfg.customTexts.length > 0 ? testCfg.customTexts.length + "个" : personaLbl(testCfg)) + "）"
                    + "\n人格：" + personaLbl(testCfg)
                    + "\n替换规则：" + testCfg.rules.size() + " 条"
                    + "\n自定义颜文字：" + (testCfg.customEmoticons.length > 0 ? testCfg.customEmoticons.length + "个" : "使用内置")
                    + "\n\n原始：\n" + sample
                    + "\n\n处理后：\n" + processed;
            new AlertDialog.Builder(this).setTitle("预览").setMessage(msg).setPositiveButton("好的", (DialogInterface.OnClickListener) null).show();
        } catch (Exception e) {
            shortToast("测试失败: " + e.getMessage());
        }
    }

    private String yn(boolean b) {
        return b ? "开" : "关";
    }

    private String personaLbl(CatConfig cfg) {
        int idx = CatConfig.personaIndex(cfg.persona);
        if (idx >= 0 && idx < CatConfig.PERSONA_LABELS.length) {
            return CatConfig.PERSONA_LABELS[idx];
        }
        return "默认";
    }
}
