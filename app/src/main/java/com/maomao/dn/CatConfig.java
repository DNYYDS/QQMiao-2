package com.maomao.dn;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class CatConfig {
    public static final String[] BUILTIN_EMOTICONS = {"^⌯𖥦⌯^ ੭ ^", "⌯'ㅅ'⌯", "=^𖥦^=", "⌯•ㅅ•⌯", "ฅ•̀∀•́ฅ", "ฅ ̳͒•ˑ̫• ̳͒ฅ♡", "ฅ(̳•·̫•̳ฅ)♡", "ฅ^••^ฅ", "=^•ω•^=", "₍^ >ヮ<^₎", "/ᐠ - ˕ -マ Ⳋ", "ฅ^•ﻌ•^ฅ", "ฅ՞•ﻌ•՞ฅ", "(ฅ´ω`ฅ)", "ฅ(*`ω´*)ฅ", "ฅ꒰ ⸝˶• •˶⸝꒱ฅ", "₍˄·͈༝·͈˄*₎◞ ̑̑", "!!^⌯𖥦⌯^ ੭!!", "₍^⸝⸝> ·̫ <⸝⸝ ^₎", "ฅ^._.^ฅ", "₍🎀˄•͈༝•͈˄₎ฅ˒˒", "^•͈༝•^ฅ", "꒰ఎ(^ . ֑ .^)໒꒱", "ฅ●ω●ฅ", "₍⸍⸌·͈༝·͈⸍⸌₎◞", "(>^ω^<)", "ฅ^-﹃-^ฅ", "^ ̳ට ̫ ට ̳^", "୧₍˄·͈༝·͈˄₎୨", "^ ̳ᴗ  ̫ ᴗ ̳^", "˓˓ก(⸍⸌̣ʷ̣̫⸍̣⸌₎ค˒˒", "ヽ(ฅ≧へ≦)ฅ", "(`･ω･´)ฅ", "(=^･ᴥ･^=)", "(^ω^ฅ)", "ฅ(≧▽≦)ฅ", "ฅ(=´▽`=)ฅ", "ヾ((๑˘ㅂ˘๑)ฅ", "(ฅ◑ω◑ฅ)", "(๑•̀ω•́ฅ)", "(ฅ>ω<*ฅ)", "(=^.^=)", "(=´ᴥ`)", "(=ↀωↀ=)", "(=^-ω-^=)", "ฅ(*°ω°*ฅ)", "ヽ(=^･ω･^=)丿", "(^•ᴥ•^)", "( Φ ω Φ )", "(=^x^=)", "ฅ( ̳• ◡ • ̳)ฅ", "o( =•ω•= )m", "~o( =∩ω∩= )m", "≡ω≡"};

    // 内置猫娘默认词池: 用于表情后的小括号 (词), 用户未自定义时使用
    public static final String[] BUILTIN_TEXTS = {"蹭蹭", "摇尾巴", "竖耳朵", "打滚", "扑过来", "卷尾巴", "喵呜", "嗷呜", "呜呜", "嘿嘿", "呜哇", "亲亲", "抱住", "咕噜噜", "开心", "好耶", "害羞", "委屈巴巴", "吃饱饱", "困困", "贴贴", "等你哦", "冲呀", "晕乎乎"};

    // 智能表情池(仅标点触发模式): 按句末标点分类匹配. 混入部分可爱猫系让风格不单调.
    public static final String[] SMART_QUESTION = {
        "ฅ՞•ﻌ•՞ฅ",
        "꒰ఎ(^ . ֑ .^)໒꒱",
        "˓˓ก(⸍⸌̣ʷ̣̫⸍̣⸌₎ค˒˒",
        "=^x^=",
        "(｡◔‸◔｡)？",
        "(｡•ㅅ•｡)？",
        "(・ε・)ﾝ??",
        "(´д`;)ﾎﾞｸｯｽｶｧ??",
        "(☆´σ‥｀)ﾉ　ﾝー、、ﾅﾝﾀﾞﾍﾞ??",
        "(。「´-ω・)ﾝ？?",
        "(’ー’*)┌ｻｧ???",
        "(;´_ゝ`)ﾆﾎﾝｺﾞﾜｶﾗﾅｨｮ...",
        "(´∀｀ )ゞ…｡oо○",
        "(★;´Д｀)┌　ｬﾚｬﾚ・・・・",
        "(･´ω`･)ﾝｰ",
        "(ﾟДﾟ≡ﾟДﾟ)ｴｯﾅﾆﾅﾆ?",
        "(･´ω･｀*)─ﾝ…",
        "(*´ﾟω｀【ﾎ】)㍗?",
        "(´L_`a)ﾞ ｳﾞｰﾝ…",
        "(´･∀･｀*)ゞﾀﾊｧ★",
        "(●-д´･)ﾝﾄﾈｪ･･･",
        "(・・∂) ｱﾚ?",
        "(o´З`;a)ﾄｫｵｫ…",
        "(｡ｕ｀ω´ｕ｡)ﾄﾞｫｼﾖｫｶ…",
        "(・ω・｀●)(●´・ω・)㌣ＤёＳц",
        "(*｀ωﾟ∞)y━ﾟﾟﾟ",
        "(^･ω･^).....ﾝﾆｭﾆｭ?",
        "(；´∀｀)ゞﾝｰﾄｫ…",
        "(/д｀*)ﾜｶﾝﾈｪｪ",
        "(σ´∀`;＠)─ﾄ…",
        "(‘、｀)ｩーﾝ…",
        "(◎_◎) ﾝ?",
        "(￢￢)ﾎﾝﾄ???",
        "(‐ｪ‐´◎)",
        "(ｏ･ω･ｏ）ﾎｴ?",
        "( ,,｀･ ω´･)ﾝﾝﾝ？",
        "( ﾟДﾟy)yｴｪｯ!??",
        "(-`ω´-)―(ﾟДﾟ)ﾊｯ!!→",
        "(((oﾉД´;q)))ｳ─ﾝ…",
        "(；´・o・｀)ﾝｰ・・・",
        "(ﾟｰﾟ*?)ｵﾖ?",
        "(q´д`p;)",
        "(｡´-ω･)ﾝ?",
        "(　･?д･?) ｴｪｯ？",
        "（ー_ー？）ﾝ？",
        "(｡；´Å｀)9ｳｰﾝ…",
        "(´_｀)━━･････",
        "(･･?..)?",
        "(´ﾟc_,ﾟ` ) ﾜｰｶﾘﾏｾｰﾝ",
        "(*σ´Å`*@)ｪ-ﾄﾈ…",
        "(Ａﾟ∇ﾟ)ﾊﾃｯ?ﾅﾉﾗ",
        "(*´･д･)ｱﾝ？"
    };
    public static final String[] SMART_EXCLAMATION = {
        "ฅ•̀∀•́ฅ",
        "ฅ(*`ω´*)ฅ",
        "ฅ꒰ ⸝˶• •˶⸝꒱ฅ",
        "₍^⸝⸝> ·̫ <⸝⸝ ^₎",
        "ฅ●ω●ฅ",
        "(>^ω^<)",
        "ヽ(ฅ≧へ≦)ฅ",
        "ฅ(≧▽≦)ฅ",
        "ฅ(=´▽`=)ฅ",
        "(ฅ>ω<*ฅ)",
        "ฅ(*°ω°*ฅ)",
        "ヾ((๑˘ㅂ˘๑)ฅ",
        "^ ̳ට ̫ ට ̳^",
        "o( =•ω•= )m",
        "~o( =∩ω∩= )m",
        "(๑•̀ω•́ฅ)",
        "ヽ(=^･ω･^=)丿",
        "≡ω≡",
        "Σ(･ω･ﾉ)",
        "Σ(ﾟωﾟ)",
        "(ﾟωﾟ)",
        "(゜o゜)",
        "Σ(ﾟ▽ﾟ)",
        "Σ(･ω･)",
        "ﾟ(ﾟﾉ0ﾟ)ﾟ",
        "Σd(ﾟ∀ﾟd)",
        "(ﾟoﾟ)",
        "(°o°)",
        "(((ﾟдﾟ)))",
        "Σ(ﾟДﾟ；)",
        "(ﾟдﾟ)",
        "Σ(ﾟдﾟlll)",
        "(((;ﾟДﾟ)))",
        "Σ(°Д°)",
        "(゜Д゜;)",
        "Σ(ﾉﾟДﾟ)ﾉ",
        "(ﾟДﾟ;)",
        "(ﾉﾟ0ﾟ)ﾉ~",
        "Σ(ﾟ∀ﾟ)",
        "Σ(･`д･´)",
        "(ﾉ゜⊿゜)ﾉ",
        "(´Д｀υ)",
        "(･_･;)",
        "Σ(ﾟ∀ﾟ;)",
        "(；・∀・)",
        "(・∀・;)",
        "(•́ω•̀;)",
        "(°_°)",
        "(ﾟ⊿ﾟ)?",
        "Σ(ーﾟωﾟ)",
        "(｡•́ω•̀｡;)",
        "(⊙o⊙)"
    };
    public static final String[] SMART_PERIOD = {
        "^⌯𖥦⌯^ ੭ ^",
        "⌯'ㅅ'⌯",
        "=^𖥦^=",
        "⌯•ㅅ•⌯",
        "ฅ ̳͒•ˑ̫• ̳͒ฅ♡",
        "ฅ(̳•·̫•̳ฅ)♡",
        "ฅ^••^ฅ",
        "=^•ω•^=",
        "₍^ >ヮ<^₎",
        "/ᐠ - ˕ -マ Ⳋ",
        "ฅ^•ﻌ•^ฅ",
        "(ฅ´ω`ฅ)",
        "₍˄·͈༝·͈˄*₎◞ ̑̑",
        "!!^⌯𖥦⌯^ ੭!!",
        "ฅ^._.^ฅ",
        "₍🎀˄•͈༝•͈˄₎ฅ˒˒",
        "^•͈༝•^ฅ",
        "₍⸍⸌·͈༝·͈⸍⸌₎◞",
        "ฅ^-﹃-^ฅ",
        "^ ̳ᴗ  ̫ ᴗ ̳^",
        "୧₍˄·͈༝·͈˄₎୨",
        "(`･ω･´)ฅ",
        "(=^･ᴥ･^=)",
        "(^ω^ฅ)",
        "(ฅ◑ω◑ฅ)",
        "(=^.^=)",
        "(=´ᴥ`)",
        "(=ↀωↀ=)",
        "(=^-ω-^=)",
        "(^•ᴥ•^)",
        "( Φ ω Φ )",
        "ฅ( ̳• ◡ • ̳)ฅ",
        "(｡･ω･｡)",
        "(◍•ᴗ•◍)",
        "(´▽`)",
        "(*´︶`*)",
        "(｡◕‿◕｡)",
        "(◕‿◕✿)",
        "(≧◡≦)",
        "(＾▽＾)",
        "(˶ˆ▽ˆ˵)",
        "(ᵔ◡ᵔ)",
        "(˶ᵔᵕᵔ˶)",
        "(´｡•ᵕ•｡`)",
        "(⁄⁄•⁄ω⁄•⁄⁄)",
        "(*ﾉωﾉ)",
        "(〃▽〃)",
        "(*/ω＼*)",
        "(⸝⸝•ᴗ•⸝⸝)",
        "(´•ω•`)",
        "(｡♡‿♡｡)",
        "(♡˙︶˙♡)",
        "♡(ᐢᵕᐢ)",
        "(˘³˘)♥",
        "(´ε｀)♡",
        "♡＼(￣▽￣)／♡",
        "(=^･ω･^=)",
        "(=｀ω´=)",
        "ฅ(•ω•)ฅ",
        "(ↀ˙▽˙ↀ)",
        "ʕ•ᴥ•ʔ",
        "(•ㅅ•)"
    };

    public static final String KEY_RULES = "rules";
    public static final String KEY_ENABLE_APPEND = "enable_append";
    public static final String KEY_APPEND_TEXT = "append_text";
    public static final String KEY_ENABLE_EMOTICON = "enable_emoticon";
    public static final String KEY_ENABLE_SMART_EMOTICON = "enable_smart_emoticon";
    public static final String KEY_ENABLE_RANDOM_TEXT = "enable_random_text";
    public static final String KEY_CUSTOM_TEXTS = "custom_texts";
    public static final String KEY_PERSONA = "persona";
    public static final String KEY_CUSTOM_EMOTICONS = "custom_emoticons";
    public static final String KEY_PROCESSING_MODE = "processing_mode";
    public static final String MODE_PUNCTUATION = "punctuation";
    public static final String MODE_REALTIME = "realtime";

    // 人格: 一套预设风格(固定尾巴 + 附加词池). 选中后作为"默认", 用户自定义项优先
    public static final String PERSONA_NONE = "none";
    public static final String PERSONA_SOFT = "soft";      // 软萌
    public static final String PERSONA_TSUNDERE = "tsundere"; // 傲娇
    public static final String PERSONA_COOL = "cool";      // 高冷
    public static final String PERSONA_SPOILED = "spoiled";// 撒娇
    public static final String PERSONA_ENERGY = "energy";  // 元气
    public static final String PERSONA_YANDERE = "yandere";// 病娇
    public static final String PERSONA_DARK = "dark";      // 腹黑

    public static final String[] PERSONA_KEYS = {
        PERSONA_NONE, PERSONA_SOFT, PERSONA_TSUNDERE, PERSONA_COOL,
        PERSONA_SPOILED, PERSONA_ENERGY, PERSONA_YANDERE, PERSONA_DARK
    };
    public static final String[] PERSONA_LABELS = {
        "默认", "软萌", "傲娇", "高冷", "撒娇", "元气", "病娇", "腹黑"
    };

    public static final String[] PERSONA_TAILS = {
        "喵~",    // 默认
        "喵~",    // 软萌
        "哼~",    // 傲娇
        "……",     // 高冷
        "呜呜~",   // 撒娇
        "！！",     // 元气
        "嘻嘻~",   // 病娇
        "呵呵~"    // 腹黑
    };
    public static final String[][] PERSONA_TEXTS = {
        {"喵~"},
        {"蹭蹭","好耶","贴贴","摇尾巴","咕噜噜","软fufu","黏着你","求抱抱"},
        {"才","笨","不理你","谁要你","哼唧","勉强一下","才不是"},
        {"嗯","知道","随意","还行","无所谓","就这","淡定"},
        {"要抱抱","人家要嘛","别不理我","求求你辣","呜哇","委屈巴巴"},
        {"冲呀","好耶","出发","干饭啦","活力满满","冲鸭","干杯"},
        {"永远在一起","不许跑","你是我的","别想逃","嘿嘿","锁住你","一直陪着你"},
        {"你懂的","看你的","没那么简单","有意思","套路人","拿捏"}
    };

    private static final String PREFS_NAME = "cat_config";

    // 默认替换规则（首次安装/从未配置时使用，可在界面修改）
    public static final String DEFAULT_RULES_TEXT =
            "我=本喵\n" +
            "你=大人\n" +
            "哥哥=大人\n" +
            "乐乐=杂鱼🐟♡\n" +
            "乐子=杂鱼🐟♡\n" +
            "傻逼=杂鱼🐟♡\n" +
            "。= ";

    public static List<Rule> parseRulesText(String text) {
        List<Rule> list = new ArrayList<>();
        if (text != null) {
            for (String line : text.split("\n")) {
                Rule r = parseRule(line);
                if (r != null) {
                    list.add(r);
                }
            }
        }
        return list;
    }

    public static class Rule {
        public final String from;
        public final String to;

        public Rule(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return from + "=" + to;
        }
    }

    public boolean enableAppend = true;
    public String appendText = "喵~";
    public boolean enableRandomEmoticon = true;
    public boolean enableSmartEmoticon = false;
    public boolean enableRandomText = true;
    public String[] customTexts = new String[0];
    public String processingMode = MODE_PUNCTUATION;
    public String persona = PERSONA_NONE;
    public String[] customEmoticons = new String[0];
    public List<Rule> rules = new ArrayList<>();

    public static Rule parseRule(String line) {
        if (line == null) {
            return null;
        }
        if (line.trim().isEmpty()) {
            return null;
        }
        String separators = "=＝→";
        int idx = -1;
        for (int i = 0; i < separators.length(); i++) {
            int p = line.indexOf(separators.charAt(i));
            if (p >= 0 && (idx < 0 || p < idx)) {
                idx = p;
            }
        }
        if (idx <= 0) {
            return null;
        }
        String from = line.substring(0, idx).trim();
        // 保留替换值末尾的空格（如 "。= " 表示句号替换为空格），仅去掉 CR/LF 等控制字符
        String to = line.substring(idx + 1).replaceAll("[\\r\\n]+", "");
        if (from.isEmpty()) {
            return null;
        }
        return new Rule(from, to);
    }

    public static String rulesToString(List<Rule> rules) {
        StringBuilder sb = new StringBuilder();
        if (rules != null) {
            for (Rule r : rules) {
                if (r == null || r.from.isEmpty()) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(r.from).append('=').append(r.to);
            }
        }
        return sb.toString();
    }

    public static CatConfig load(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, 0);
        CatConfig cfg = new CatConfig();
        cfg.enableAppend = sp.getBoolean(KEY_ENABLE_APPEND, true);
        cfg.appendText = sp.getString(KEY_APPEND_TEXT, "喵~");
        cfg.enableRandomEmoticon = sp.getBoolean(KEY_ENABLE_EMOTICON, true);
        cfg.enableSmartEmoticon = sp.getBoolean(KEY_ENABLE_SMART_EMOTICON, false);
        cfg.enableRandomText = sp.getBoolean(KEY_ENABLE_RANDOM_TEXT, true);
        cfg.processingMode = sp.getString(KEY_PROCESSING_MODE, MODE_PUNCTUATION);
        cfg.persona = sp.getString(KEY_PERSONA, PERSONA_NONE);

        String rulesStr = sp.getString(KEY_RULES, "");
        if (rulesStr != null && !rulesStr.trim().isEmpty()) {
            List<Rule> list = new ArrayList<>();
            for (String line : rulesStr.split("\n")) {
                Rule r = parseRule(line);
                if (r != null) {
                    list.add(r);
                }
            }
            cfg.rules = list;
        } else if (!sp.contains(KEY_RULES)) {
            // 首次安装 / 从未保存过配置：使用默认替换规则
            cfg.rules = parseRulesText(DEFAULT_RULES_TEXT);
        }

        String custom = sp.getString(KEY_CUSTOM_EMOTICONS, "");
        if (custom != null && !custom.trim().isEmpty()) {
            List<String> list = new ArrayList<>();
            for (String s : custom.split("\n")) {
                String t = s.trim();
                if (!t.isEmpty()) {
                    list.add(t);
                }
            }
            cfg.customEmoticons = list.toArray(new String[0]);
        } else {
            cfg.customEmoticons = new String[0];
        }

        String customTxt = sp.getString(KEY_CUSTOM_TEXTS, "");
        if (customTxt != null && !customTxt.trim().isEmpty()) {
            List<String> tlist = new ArrayList<>();
            for (String s : customTxt.split("\n")) {
                String t = s.trim();
                if (!t.isEmpty()) {
                    tlist.add(t);
                }
            }
            cfg.customTexts = tlist.toArray(new String[0]);
        } else {
            cfg.customTexts = new String[0];
        }
        return cfg;
    }

    public void save(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(KEY_ENABLE_APPEND, this.enableAppend);
        ed.putString(KEY_APPEND_TEXT, this.appendText == null ? "" : this.appendText);
        ed.putBoolean(KEY_ENABLE_EMOTICON, this.enableRandomEmoticon);
        ed.putBoolean(KEY_ENABLE_SMART_EMOTICON, this.enableSmartEmoticon);
        ed.putBoolean(KEY_ENABLE_RANDOM_TEXT, this.enableRandomText);
        ed.putString(KEY_PROCESSING_MODE, this.processingMode == null ? MODE_PUNCTUATION : this.processingMode);
        ed.putString(KEY_PERSONA, this.persona == null ? PERSONA_NONE : this.persona);
        ed.putString(KEY_RULES, rulesToString(this.rules));
        ed.putString(KEY_CUSTOM_EMOTICONS, join(this.customEmoticons, "\n"));
        ed.putString(KEY_CUSTOM_TEXTS, join(this.customTexts, "\n"));
        ed.apply();
    }

    public String[] getActiveEmoticons() {
        if (this.customEmoticons != null && this.customEmoticons.length > 0) {
            return this.customEmoticons;
        }
        return BUILTIN_EMOTICONS;
    }

    // 表情后小括号(词)池: 用户自定义优先, 否则用内置猫娘词池
    public String[] getActiveTexts() {
        if (this.customTexts != null && this.customTexts.length > 0) {
            return this.customTexts;
        }
        if (this.persona != null && !this.persona.isEmpty() && !PERSONA_NONE.equals(this.persona)) {
            int idx = personaIndex(this.persona);
            if (idx >= 0 && idx < PERSONA_TEXTS.length) {
                return PERSONA_TEXTS[idx];
            }
        }
        return BUILTIN_TEXTS;
    }

    // 人格索引: key -> 下标, 未找到返回 -1
    public static int personaIndex(String key) {
        if (key == null) return -1;
        for (int i = 0; i < PERSONA_KEYS.length; i++) {
            if (PERSONA_KEYS[i].equals(key)) return i;
        }
        return -1;
    }

    // 人格的默认尾巴: key -> 尾巴, 未找到/默认返回空(不强制覆盖)
    public static String getPersonaTail(String key) {
        int idx = personaIndex(key);
        if (idx >= 0 && idx < PERSONA_TAILS.length && !PERSONA_NONE.equals(key)) {
            return PERSONA_TAILS[idx];
        }
        return null;
    }

    // 按情绪分组的附加词池. 命中正文中的情绪关键词时, 用对应情绪的"词"替换默认词池(方案B: 标点定主表情, 情绪定附加词)
    private static class EmotionText {
        final String[] keywords;
        final String[] texts;
        EmotionText(String[] k, String[] t) { this.keywords = k; this.texts = t; }
    }

    private static final EmotionText[] EMOTION_TEXTS = {
        new EmotionText(new String[]{"哈哈", "笑死", "笑", "hhhh", "233", "好笑", "太逗", "笑死我了"},
            new String[]{"哈哈哈", "笑死本喵", "戳中笑点", "笑到打滚", "乐死我辣"}),
        new EmotionText(new String[]{"难过", "难受", "哭", "委屈", "伤心", "想哭", "呜呜", "好惨", "好难"},
            new String[]{"呜呜", "抱抱", "摸摸头", "好委屈", "哭唧唧", "要抱抱"}),
        new EmotionText(new String[]{"晚安", "睡觉", "睡了", "累", "困", "休息", "好累"},
            new String[]{"困困", "晚安喵", "睡香香", "趴好咯", "做个好梦"}),
        new EmotionText(new String[]{"谢谢", "谢了", "多谢", "感谢", "辛苦", "爱你"},
            new String[]{"不客气", "谢谢大人", "啾咪", "嘿嘿", "最喜欢你"}),
        new EmotionText(new String[]{"生气", "气死", "可恶", "烦", "滚", "怒", "气人"},
            new String[]{"哼", "气鼓鼓", "本喵怒了", "不理你", "哼唧"}),
        new EmotionText(new String[]{"爱你", "喜欢你", "想你", "亲亲", "宝贝", "抱抱", "么么"},
            new String[]{"贴贴", "唔喵", "亲亲", "好喜欢你", "紧紧抱住"}),
        new EmotionText(new String[]{"早安", "早上好", "起床", "早上"},
            new String[]{"早呀", "早安喵", "新的一天", "太阳晒屁股啦"}),
        new EmotionText(new String[]{"拜拜", "再见", "走了", "告辞", "886", "下车"},
            new String[]{"拜拜", "下次见", "走啦", "挥手手", "我溜啦"}),
    };

    // 匹配正文是否命中某情绪关键词, 命中返回该情绪专属词池, 否则返回 null(用默认词池)
    public static String[] findEmotionTexts(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        for (EmotionText e : EMOTION_TEXTS) {
            for (String kw : e.keywords) {
                if (kw != null && !kw.isEmpty() && text.contains(kw)) {
                    return e.texts;
                }
            }
        }
        return null;
    }

    // 智能表情: 按句末标点返回对应分类池. 用户自定义非空时不启用智能(调用方需先判断)
    public static String[] getSmartPoolForEnding(String endingText) {
        if (endingText == null || endingText.isEmpty()) {
            return null; // 无标点 -> 走默认大池
        }
        char last = endingText.charAt(endingText.length() - 1);
        if (last == '？' || last == '?' || last == 65311) {
            return SMART_QUESTION;
        }
        if (last == '！' || last == '!' || last == 65281) {
            return SMART_EXCLAMATION;
        }
        if (last == '。' || last == '．' || last == 12290 || last == 65292) {
            return SMART_PERIOD;
        }
        return null;
    }

    private static String join(String[] arr, String delim) {
        StringBuilder sb = new StringBuilder();
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                String s = arr[i];
                if (s == null) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append(delim);
                }
                sb.append(s);
            }
        }
        return sb.toString();
    }
}