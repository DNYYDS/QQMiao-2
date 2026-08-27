package com.maomao.dn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessor {
    private static final Random RANDOM = new Random();
    private static final Pattern SENTENCE_SPLIT_PATTERN = Pattern.compile("([，,。！!？?\\s]+)");

    public static String process(String original, CatConfig config) {
        return process(original, config, null);
    }

    // fixedEmoticon 非空时复用该表情(实时模式下保持稳定), 否则每次随机
    public static String process(String original, CatConfig config, String fixedEmoticon) {
        return processWithCursor(original, config, fixedEmoticon).text;
    }

    public static class ProcessResult {
        public final String text;
        public final int userEndIndex;
        public ProcessResult(String text, int userEndIndex) {
            this.text = text;
            this.userEndIndex = userEndIndex;
        }
    }

    // 返回处理结果 + 用户原文内容结束位置(userEndIndex, 不含自动加的尾巴/表情), 供实时模式精确定位光标
    public static ProcessResult processWithCursor(String original, CatConfig config, String fixedEmoticon) {
        if (original == null || original.trim().isEmpty()) {
            return new ProcessResult(original, 0);
        }
        String text = original.trim();

        if (config.rules != null) {
            for (CatConfig.Rule rule : config.rules) {
                if (rule == null || rule.from.isEmpty()) {
                    continue;
                }
                text = text.replace(rule.from, rule.to);
            }
        }

        int userEnd = text.length(); // 默认光标在内容末尾
        // 先根据"不含尾巴"的原文末尾标点, 决定智能表情(避免断句追加的尾巴挡住标点判断)
        String smartEmoticon = null;
        if (config.enableRandomEmoticon) {
            smartEmoticon = pickSmartEmoticon(text, config);
        }

        if (config.enableAppend) {
            // 尾巴: 选中非默认人格且用户未自定义尾巴(仍是默认"喵~")时, 用人格专属尾巴
            String tail = config.appendText;
            String personaTail = CatConfig.getPersonaTail(config.persona);
            boolean userCustomized = tail != null && !"喵~".equals(tail); // 用户改过尾巴则尊重
            if (personaTail != null && !userCustomized) {
                tail = personaTail;
            }
            // 逐句追加尾巴, 返回用户内容结束位置(最后一个非空分句的结尾)
            int[] holder = new int[]{ text.length() };
            text = appendPerSentence(text, tail, holder);
            userEnd = holder[0];
        }

        if (config.enableRandomEmoticon) {
            // 智能表情优先(已按原文标点挑好), 否则用固定表情/默认随机
            String emoticon = smartEmoticon != null ? smartEmoticon : chooseEmoticon(text, config, fixedEmoticon);
            if (emoticon != null && !emoticon.isEmpty()) {
                text = text + " " + emoticon;
            }
            // 表情后附加一个 (随机词): 开启随机话题/词时, 从词池抽一个
            // 方案B: 先按正文情绪关键词取专属词池(标点已定主表情), 未命中情绪则用默认池(自定义优先, 否则内置)
            if (config.enableRandomText) {
                String[] texts = CatConfig.findEmotionTexts(text);
                if (texts == null) {
                    texts = config.getActiveTexts(); // 自定义优先, 否则人格词池, 再否则内置
                }
                if (texts != null && texts.length > 0) {
                    text = text + " (" + texts[RANDOM.nextInt(texts.length)] + ")";
                }
            }
        }
        return new ProcessResult(text, userEnd);
    }

    // 仅当启用智能表情且自定义为空时, 按原文末尾标点挑选智能表情; 否则返回 null 走常规流程
    private static String pickSmartEmoticon(String text, CatConfig config) {
        boolean customEmpty = config.customEmoticons == null || config.customEmoticons.length == 0;
        if (config.enableSmartEmoticon && customEmpty) {
            String[] pool = CatConfig.getSmartPoolForEnding(text);
            if (pool != null && pool.length > 0) {
                return pool[RANDOM.nextInt(pool.length)];
            }
        }
        return null;
    }

    // 表情挑选: 实时模式稳定表情 > 智能表情(按标点, 自定义为空时) > 随机
    private static String chooseEmoticon(String text, CatConfig config, String fixedEmoticon) {
        if (fixedEmoticon != null && !fixedEmoticon.isEmpty()) {
            return fixedEmoticon;
        }
        return getRandomEmoticon(config);
    }

    private static String appendPerSentence(String text, String suffix, int[] holder) {
        String s = (suffix == null) ? "" : suffix;
        List<String> parts = new ArrayList<>();
        List<String> separators = new ArrayList<>();
        Matcher matcher = SENTENCE_SPLIT_PATTERN.matcher(text);
        int lastEnd = 0;
        while (matcher.find()) {
            parts.add(text.substring(lastEnd, matcher.start()));
            separators.add(matcher.group(1));
            lastEnd = matcher.end();
        }
        if (lastEnd < text.length()) {
            parts.add(text.substring(lastEnd));
        } else if (!parts.isEmpty() && lastEnd == text.length()) {
            parts.add("");
        }
        if (parts.isEmpty()) {
            parts.add(text);
        }
        StringBuilder result = new StringBuilder();
        int endPos = text.length(); // 默认整个内容结束
        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i).trim();
            if (!part.isEmpty()) {
                result.append(part);
                result.append(s);
                // 分句内容结束位置(不含尾巴): 即 append 尾巴前
                endPos = result.length() - s.length();
            }
            if (i < separators.size()) {
                result.append(separators.get(i));
                // 若本句后面还有用户内容(分隔符后还有后续输入), 光标应紧随该分隔符
                endPos = result.length();
            }
        }
        String resultStr = result.toString().trim();
        if (resultStr.isEmpty()) {
            return text + s;
        }
        if (holder != null) {
            holder[0] = endPos;
        }
        return resultStr;
    }

    private static String getRandomEmoticon(CatConfig config) {
        String[] emoticons = config.getActiveEmoticons();
        if (emoticons == null || emoticons.length == 0) {
            emoticons = CatConfig.BUILTIN_EMOTICONS;
        }
        return emoticons.length == 0 ? "" : emoticons[RANDOM.nextInt(emoticons.length)];
    }

    // 供服务端在实时模式下取一个稳定表情
    public static String getRandomEmoticonStatic(CatConfig config) {
        return getRandomEmoticon(config);
    }

    public static String process(String original) {
        CatConfig defaults = new CatConfig();
        defaults.enableAppend = true;
        defaults.appendText = "喵~";
        defaults.enableRandomEmoticon = true;
        defaults.customEmoticons = new String[0];
        defaults.rules = CatConfig.parseRulesText(CatConfig.DEFAULT_RULES_TEXT);
        return process(original, defaults);
    }
}