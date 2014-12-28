/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.inputmethod.keyboard.internal;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import in.androidtweak.inputmethod.annotations.UsedForTesting;
import in.android.inputmethod.indic.Constants;
import in.android.inputmethod.indic.utils.RunInLocale;
import in.android.inputmethod.indic.utils.SubtypeLocaleUtils;

import java.util.HashMap;
import java.util.Locale;

public final class KeyboardTextsSet {
    public static final String PREFIX_TEXT = "!text/";
    public static final String SWITCH_TO_ALPHA_KEY_LABEL = "keylabel_to_alpha";

    private static final char BACKSLASH = Constants.CODE_BACKSLASH;
    private static final int MAX_STRING_REFERENCE_INDIRECTION = 10;

    private String[] mTextsTable;
    // Resource name to text map.
    private HashMap<String, String> mResourceNameToTextsMap = new HashMap<>();

    public void setLocale(final Locale locale, final Context context) {
        mTextsTable = KeyboardTextsTable.getTextsTable(locale);
        final Resources res = context.getResources();
        final int referenceId = context.getApplicationInfo().labelRes;
        final String resourcePackageName = res.getResourcePackageName(referenceId);
        final RunInLocale<Void> job = new RunInLocale<Void>() {
            @Override
            protected Void job(final Resources resource) {
                loadStringResourcesInternal(res, RESOURCE_NAMES, resourcePackageName);
                return null;
            }
        };
        // Null means the current system locale.
        job.runInLocale(res,
                SubtypeLocaleUtils.NO_LANGUAGE.equals(locale.toString()) ? null : locale);
    }

    @UsedForTesting
    void loadStringResourcesInternal(final Resources res, final String[] resourceNames,
            final String resourcePackageName) {
        for (final String resName : resourceNames) {
            final int resId = res.getIdentifier(resName, "string", resourcePackageName);
            mResourceNameToTextsMap.put(resName, res.getString(resId));
        }
    }

    public String getText(final String name) {
        final String text = mResourceNameToTextsMap.get(name);
        return (text != null) ? text : KeyboardTextsTable.getText(name, mTextsTable);
    }

    private static int searchTextNameEnd(final String text, final int start) {
        final int size = text.length();
        for (int pos = start; pos < size; pos++) {
            final char c = text.charAt(pos);
            // Label name should be consisted of [a-zA-Z_0-9].
            if ((c >= 'a' && c <= 'z') || c == '_' || (c >= '0' && c <= '9')) {
                continue;
            }
            return pos;
        }
        return size;
    }

    // TODO: Resolve text reference when creating {@link KeyboardTextsTable} class.
    public String resolveTextReference(final String rawText) {
        if (TextUtils.isEmpty(rawText)) {
            return null;
        }
        int level = 0;
        String text = rawText;
        StringBuilder sb;
        do {
            level++;
            if (level >= MAX_STRING_REFERENCE_INDIRECTION) {
                throw new RuntimeException("Too many " + PREFIX_TEXT + "name indirection: " + text);
            }

            final int prefixLen = PREFIX_TEXT.length();
            final int size = text.length();
            if (size < prefixLen) {
                break;
            }

            sb = null;
            for (int pos = 0; pos < size; pos++) {
                final char c = text.charAt(pos);
                if (text.startsWith(PREFIX_TEXT, pos)) {
                    if (sb == null) {
                        sb = new StringBuilder(text.substring(0, pos));
                    }
                    final int end = searchTextNameEnd(text, pos + prefixLen);
                    final String name = text.substring(pos + prefixLen, end);
                    sb.append(getText(name));
                    pos = end - 1;
                } else if (c == BACKSLASH) {
                    if (sb != null) {
                        // Append both escape character and escaped character.
                        sb.append(text.substring(pos, Math.min(pos + 2, size)));
                    }
                    pos++;
                } else if (sb != null) {
                    sb.append(c);
                }
            }

            if (sb != null) {
                text = sb.toString();
            }
        } while (sb != null);
        return TextUtils.isEmpty(text) ? null : text;
    }

    // These texts' name should be aligned with the @string/<name> in
    // values*/strings-action-keys.xml.
    static final String[] RESOURCE_NAMES = {
        // Labels for action.
        "label_go_key",
        "label_send_key",
        "label_next_key",
        "label_done_key",
        "label_search_key",
        "label_previous_key",
        // Other labels.
        "label_pause_key",
        "label_wait_key",
    };

    private static final String[] NAMES = {
        /*  0 */ "more_keys_for_a",
        /*  1 */ "more_keys_for_e",
        /*  2 */ "more_keys_for_i",
        /*  3 */ "more_keys_for_o",
        /*  4 */ "more_keys_for_u",
        /*  5 */ "more_keys_for_s",
        /*  6 */ "more_keys_for_n",
        /*  7 */ "more_keys_for_c",
        /*  8 */ "more_keys_for_y",
        /*  9 */ "more_keys_for_d",
        /* 10 */ "more_keys_for_r",
        /* 11 */ "more_keys_for_t",
        /* 12 */ "more_keys_for_z",
        /* 13 */ "more_keys_for_k",
        /* 14 */ "more_keys_for_l",
        /* 15 */ "more_keys_for_g",
        /* 16 */ "more_keys_for_v",
        /* 17 */ "more_keys_for_h",
        /* 18 */ "more_keys_for_j",
        /* 19 */ "more_keys_for_w",
        /* 20 */ "keylabel_for_nordic_row1_11",
        /* 21 */ "keylabel_for_nordic_row2_10",
        /* 22 */ "keylabel_for_nordic_row2_11",
        /* 23 */ "more_keys_for_nordic_row2_10",
        /* 24 */ "more_keys_for_nordic_row2_11",
        /* 25 */ "keylabel_for_east_slavic_row1_9",
        /* 26 */ "keylabel_for_east_slavic_row1_12",
        /* 27 */ "keylabel_for_east_slavic_row2_1",
        /* 28 */ "keylabel_for_east_slavic_row2_11",
        /* 29 */ "keylabel_for_east_slavic_row3_5",
        /* 30 */ "more_keys_for_cyrillic_u",
        /* 31 */ "more_keys_for_cyrillic_ka",
        /* 32 */ "more_keys_for_cyrillic_en",
        /* 33 */ "more_keys_for_cyrillic_ghe",
        /* 34 */ "more_keys_for_east_slavic_row2_1",
        /* 35 */ "more_keys_for_cyrillic_a",
        /* 36 */ "more_keys_for_cyrillic_o",
        /* 37 */ "more_keys_for_cyrillic_soft_sign",
        /* 38 */ "more_keys_for_east_slavic_row2_11",
        /* 39 */ "keylabel_for_south_slavic_row1_6",
        /* 40 */ "keylabel_for_south_slavic_row2_11",
        /* 41 */ "keylabel_for_south_slavic_row3_1",
        /* 42 */ "keylabel_for_south_slavic_row3_8",
        /* 43 */ "more_keys_for_cyrillic_ie",
        /* 44 */ "more_keys_for_cyrillic_i",
        /* 45 */ "label_to_alpha_key",
        /* 46 */ "single_quotes",
        /* 47 */ "double_quotes",
        /* 48 */ "single_angle_quotes",
        /* 49 */ "double_angle_quotes",
        /* 50 */ "more_keys_for_currency_dollar",
        /* 51 */ "keylabel_for_currency",
        /* 52 */ "more_keys_for_currency",
        /* 53 */ "more_keys_for_punctuation",
        /* 54 */ "more_keys_for_star",
        /* 55 */ "more_keys_for_bullet",
        /* 56 */ "more_keys_for_plus",
        /* 57 */ "more_keys_for_left_parenthesis",
        /* 58 */ "more_keys_for_right_parenthesis",
        /* 59 */ "more_keys_for_less_than",
        /* 60 */ "more_keys_for_greater_than",
        /* 61 */ "more_keys_for_arabic_diacritics",
        /* 62 */ "keyhintlabel_for_arabic_diacritics",
        /* 63 */ "keylabel_for_symbols_1",
        /* 64 */ "keylabel_for_symbols_2",
        /* 65 */ "keylabel_for_symbols_3",
        /* 66 */ "keylabel_for_symbols_4",
        /* 67 */ "keylabel_for_symbols_5",
        /* 68 */ "keylabel_for_symbols_6",
        /* 69 */ "keylabel_for_symbols_7",
        /* 70 */ "keylabel_for_symbols_8",
        /* 71 */ "keylabel_for_symbols_9",
        /* 72 */ "keylabel_for_symbols_0",
        /* 73 */ "label_to_symbol_key",
        /* 74 */ "label_to_symbol_with_microphone_key",
        /* 75 */ "additional_more_keys_for_symbols_1",
        /* 76 */ "additional_more_keys_for_symbols_2",
        /* 77 */ "additional_more_keys_for_symbols_3",
        /* 78 */ "additional_more_keys_for_symbols_4",
        /* 79 */ "additional_more_keys_for_symbols_5",
        /* 80 */ "additional_more_keys_for_symbols_6",
        /* 81 */ "additional_more_keys_for_symbols_7",
        /* 82 */ "additional_more_keys_for_symbols_8",
        /* 83 */ "additional_more_keys_for_symbols_9",
        /* 84 */ "additional_more_keys_for_symbols_0",
        /* 85 */ "more_keys_for_symbols_1",
        /* 86 */ "more_keys_for_symbols_2",
        /* 87 */ "more_keys_for_symbols_3",
        /* 88 */ "more_keys_for_symbols_4",
        /* 89 */ "more_keys_for_symbols_5",
        /* 90 */ "more_keys_for_symbols_6",
        /* 91 */ "more_keys_for_symbols_7",
        /* 92 */ "more_keys_for_symbols_8",
        /* 93 */ "more_keys_for_symbols_9",
        /* 94 */ "more_keys_for_symbols_0",
        /* 95 */ "keylabel_for_comma",
        /* 96 */ "more_keys_for_comma",
        /* 97 */ "keylabel_for_symbols_question",
        /* 98 */ "keylabel_for_symbols_semicolon",
        /* 99 */ "keylabel_for_symbols_percent",
        /* 100 */ "more_keys_for_symbols_exclamation",
        /* 101 */ "more_keys_for_symbols_question",
        /* 102 */ "more_keys_for_symbols_semicolon",
        /* 103 */ "more_keys_for_symbols_percent",
        /* 104 */ "keylabel_for_tablet_comma",
        /* 105 */ "keyhintlabel_for_tablet_comma",
        /* 106 */ "more_keys_for_tablet_comma",
        /* 107 */ "keyhintlabel_for_period",
        /* 108 */ "more_keys_for_period",
        /* 109 */ "keylabel_for_apostrophe",
        /* 110 */ "keyhintlabel_for_apostrophe",
        /* 111 */ "more_keys_for_apostrophe",
        /* 112 */ "more_keys_for_q",
        /* 113 */ "more_keys_for_x",
        /* 114 */ "keylabel_for_q",
        /* 115 */ "keylabel_for_w",
        /* 116 */ "keylabel_for_y",
        /* 117 */ "keylabel_for_x",
        /* 118 */ "keylabel_for_spanish_row2_10",
        /* 119 */ "more_keys_for_am_pm",
        /* 120 */ "settings_as_more_key",
        /* 121 */ "shortcut_as_more_key",
        /* 122 */ "action_next_as_more_key",
        /* 123 */ "action_previous_as_more_key",
        /* 124 */ "label_to_more_symbol_key",
        /* 125 */ "label_to_more_symbol_for_tablet_key",
        /* 126 */ "label_tab_key",
        /* 127 */ "label_to_phone_numeric_key",
        /* 128 */ "label_to_phone_symbols_key",
        /* 129 */ "label_time_am",
        /* 130 */ "label_time_pm",
        /* 131 */ "keylabel_for_popular_domain",
        /* 132 */ "more_keys_for_popular_domain",
        /* 133 */ "more_keys_for_smiley",
        /* 134 */ "single_laqm_raqm",
        /* 135 */ "single_laqm_raqm_rtl",
        /* 136 */ "single_raqm_laqm",
        /* 137 */ "double_laqm_raqm",
        /* 138 */ "double_laqm_raqm_rtl",
        /* 139 */ "double_raqm_laqm",
        /* 140 */ "single_lqm_rqm",
        /* 141 */ "single_9qm_lqm",
        /* 142 */ "single_9qm_rqm",
        /* 143 */ "double_lqm_rqm",
        /* 144 */ "double_9qm_lqm",
        /* 145 */ "double_9qm_rqm",
        /* 146 */ "more_keys_for_single_quote",
        /* 147 */ "more_keys_for_double_quote",
        /* 148 */ "more_keys_for_tablet_double_quote",
        /* 149 */ "emoji_key_as_more_key",
    };

    private static final String EMPTY = "";

    /* Default texts */
    private static final String[] LANGUAGE_DEFAULT = {
        /* 0~ */
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        /* ~44 */
        // Label for "switch to alphabetic" key.
        /* 45 */ "ABC",
        /* 46 */ "!text/single_lqm_rqm",
        /* 47 */ "!text/double_lqm_rqm",
        /* 48 */ "!text/single_laqm_raqm",
        /* 49 */ "!text/double_laqm_raqm",
        // $ - I'm making ₹ as default
        // U+00A2: "¢" CENT SIGN
        // U+00A3: "£" POUND SIGN
        // U+20AC: "€" EURO SIGN
        // U+00A5: "¥" YEN SIGN
        // U+20B1: "₱" PESO SIGN
        /* 50 */ "$,\u00A2,\u00A3,\u20AC,\u00A5,\u20B1",
        /* 51 */ "$",
        /* 52 */ "$,\u00A2,\u20AC,\u00A3,\u00A5,\u20B1",
        /* 53 */ "!fixedColumnOrder!8,;,/,(,),#,!,\\,,?,&,\\%,+,\",-,:,',@",
        // U+2020: "†" DAGGER
        // U+2021: "‡" DOUBLE DAGGER
        // U+2605: "★" BLACK STAR
        /* 54 */ "\u2020,\u2021,\u2605",
        // U+266A: "♪" EIGHTH NOTE
        // U+2665: "♥" BLACK HEART SUIT
        // U+2660: "♠" BLACK SPADE SUIT
        // U+2666: "♦" BLACK DIAMOND SUIT
        // U+2663: "♣" BLACK CLUB SUIT
        /* 55 */ "\u266A,\u2665,\u2660,\u2666,\u2663",
        // U+00B1: "±" PLUS-MINUS SIGN
        /* 56 */ "\u00B1",
        // The all letters need to be mirrored are found at
        // http://www.unicode.org/Public/6.1.0/ucd/BidiMirroring.txt
        /* 57 */ "!fixedColumnOrder!3,<,{,[",
        /* 58 */ "!fixedColumnOrder!3,>,},]",
        // U+2039: "‹" SINGLE LEFT-POINTING ANGLE QUOTATION MARK
        // U+203A: "›" SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
        // U+2264: "≤" LESS-THAN OR EQUAL TO
        // U+2265: "≥" GREATER-THAN EQUAL TO
        // U+00AB: "«" LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
        // U+00BB: "»" RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
        /* 59 */ "!fixedColumnOrder!3,\u2039,\u2264,\u00AB",
        /* 60 */ "!fixedColumnOrder!3,\u203A,\u2265,\u00BB",
        /* 61 */ EMPTY,
        /* 62 */ EMPTY,
        /* 63 */ "1",
        /* 64 */ "2",
        /* 65 */ "3",
        /* 66 */ "4",
        /* 67 */ "5",
        /* 68 */ "6",
        /* 69 */ "7",
        /* 70 */ "8",
        /* 71 */ "9",
        /* 72 */ "0",
        // Label for "switch to symbols" key.
        /* 73 */ "?123",
        // Label for "switch to symbols with microphone" key. This string shouldn't include the "mic"
        // part because it'll be appended by the code.
        /* 74 */ "123",
        /* 75~ */
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        /* ~84 */
        // U+00B9: "¹" SUPERSCRIPT ONE
        // U+00BD: "½" VULGAR FRACTION ONE HALF
        // U+2153: "⅓" VULGAR FRACTION ONE THIRD
        // U+00BC: "¼" VULGAR FRACTION ONE QUARTER
        // U+215B: "⅛" VULGAR FRACTION ONE EIGHTH
        /* 85 */ "\u00B9,\u00BD,\u2153,\u00BC,\u215B",
        // U+00B2: "²" SUPERSCRIPT TWO
        // U+2154: "⅔" VULGAR FRACTION TWO THIRDS
        /* 86 */ "\u00B2,\u2154",
        // U+00B3: "³" SUPERSCRIPT THREE
        // U+00BE: "¾" VULGAR FRACTION THREE QUARTERS
        // U+215C: "⅜" VULGAR FRACTION THREE EIGHTHS
        /* 87 */ "\u00B3,\u00BE,\u215C",
        // U+2074: "⁴" SUPERSCRIPT FOUR
        /* 88 */ "\u2074",
        // U+215D: "⅝" VULGAR FRACTION FIVE EIGHTHS
        /* 89 */ "\u215D",
        /* 90 */ EMPTY,
        // U+215E: "⅞" VULGAR FRACTION SEVEN EIGHTHS
        /* 91 */ "\u215E",
        /* 92 */ EMPTY,
        /* 93 */ EMPTY,
        // U+207F: "ⁿ" SUPERSCRIPT LATIN SMALL LETTER N
        // U+2205: "∅" EMPTY SET
        /* 94 */ "\u207F,\u2205",
        /* 95 */ ",",
        /* 96 */ EMPTY,
        /* 97 */ "?",
        /* 98 */ ";",
        /* 99 */ "%",
        // U+00A1: "¡" INVERTED EXCLAMATION MARK
        /* 100 */ "\u00A1",
        // U+00BF: "¿" INVERTED QUESTION MARK
        /* 101 */ "\u00BF",
        /* 102 */ EMPTY,
        // U+2030: "‰" PER MILLE SIGN
        /* 103 */ "\u2030",
        /* 104 */ ",",
        /* 105~ */
        EMPTY, EMPTY, EMPTY,
        /* ~107 */
        // U+2026: "…" HORIZONTAL ELLIPSIS
        /* 108 */ "\u2026",
        /* 109 */ "\'",
        /* 110 */ "\"",
        /* 111 */ "\"",
        /* 112 */ EMPTY,
        /* 113 */ EMPTY,
        /* 114 */ "q",
        /* 115 */ "w",
        /* 116 */ "y",
        /* 117 */ "x",
        /* 118 */ EMPTY,
        /* 119 */ "!fixedColumnOrder!2,!hasLabels!,!text/label_time_am,!text/label_time_pm",
        /* 120 */ "!icon/settings_key|!code/key_settings",
        /* 121 */ "!icon/shortcut_key|!code/key_shortcut",
        /* 122 */ "!hasLabels!,!text/label_next_key|!code/key_action_next",
        /* 123 */ "!hasLabels!,!text/label_previous_key|!code/key_action_previous",
        // Label for "switch to more symbol" modifier key.  Must be short to fit on key!
        /* 124 */ "= \\ <",
        // Label for "switch to more symbol" modifier key on tablets.  Must be short to fit on key!
        /* 125 */ "~ [ <",
        // Label for "Tab" key.  Must be short to fit on key!
        /* 126 */ "Tab",
        // Label for "switch to phone numeric" key.  Must be short to fit on key!
        /* 127 */ "123",
        // Label for "switch to phone symbols" key.  Must be short to fit on key!
        // U+FF0A: "＊" FULLWIDTH ASTERISK
        // U+FF03: "＃" FULLWIDTH NUMBER SIGN
        /* 128 */ "\uFF0A\uFF03",
        // Key label for "ante meridiem"
        /* 129 */ "AM",
        // Key label for "post meridiem"
        /* 130 */ "PM",
        /* 131 */ ".com",
        // popular web domains for the locale - most popular, displayed on the keyboard
        /* 132 */ "!hasLabels!,.net,.org,.gov,.edu",
        /* 133 */ "!fixedColumnOrder!5,!hasLabels!,:)|:) ,;)|;) ,:(|:( ,:D|:D ,:P|:P ,^^|^^ ,-_-|-_- ,=-O|=-O ,:-P|:-P ,;-)|;-) ,:-(|:-( ,:-)|:-) ,:-!|:-! ,:-$|:-$ ,B-)|B-) ,:O|:O ,:-*|:-* ,:-D|:-D ,:\'(|:\'( ,:-\\\\|:-\\\\ ,O:-)|O:-) ,:-[|:-[ ",
        // U+2039: "‹" SINGLE LEFT-POINTING ANGLE QUOTATION MARK
        // U+203A: "›" SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
        // U+00AB: "«" LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
        // U+00BB: "»" RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
        // The following characters don't need BIDI mirroring.
        // U+2018: "‘" LEFT SINGLE QUOTATION MARK
        // U+2019: "’" RIGHT SINGLE QUOTATION MARK
        // U+201A: "‚" SINGLE LOW-9 QUOTATION MARK
        // U+201C: "“" LEFT DOUBLE QUOTATION MARK
        // U+201D: "”" RIGHT DOUBLE QUOTATION MARK
        // U+201E: "„" DOUBLE LOW-9 QUOTATION MARK
        // Abbreviations are:
        // laqm: LEFT-POINTING ANGLE QUOTATION MARK
        // raqm: RIGHT-POINTING ANGLE QUOTATION MARK
        // rtl: Right-To-Left script order
        // lqm: LEFT QUOTATION MARK
        // rqm: RIGHT QUOTATION MARK
        // 9qm: LOW-9 QUOTATION MARK
        // The following each quotation mark pair consist of
        // <opening quotation mark>, <closing quotation mark>
        // and is named after (single|double)_<opening quotation mark>_<closing quotation mark>.
        /* 134 */ "\u2039,\u203A",
        /* 135 */ "\u2039|\u203A,\u203A|\u2039",
        /* 136 */ "\u203A,\u2039",
        /* 137 */ "\u00AB,\u00BB",
        /* 138 */ "\u00AB|\u00BB,\u00BB|\u00AB",
        /* 139 */ "\u00BB,\u00AB",
        // The following each quotation mark triplet consists of
        // <another quotation mark>, <opening quotation mark>, <closing quotation mark>
        // and is named after (single|double)_<opening quotation mark>_<closing quotation mark>.
        /* 140 */ "\u201A,\u2018,\u2019",
        /* 141 */ "\u2019,\u201A,\u2018",
        /* 142 */ "\u2018,\u201A,\u2019",
        /* 143 */ "\u201E,\u201C,\u201D",
        /* 144 */ "\u201D,\u201E,\u201C",
        /* 145 */ "\u201C,\u201E,\u201D",
        /* 146 */ "!fixedColumnOrder!5,!text/single_quotes,!text/single_angle_quotes",
        /* 147 */ "!fixedColumnOrder!5,!text/double_quotes,!text/double_angle_quotes",
        /* 148 */ "!fixedColumnOrder!6,!text/double_quotes,!text/single_quotes,!text/double_angle_quotes,!text/single_angle_quotes",
        /* 149 */ "!icon/emoji_key|!code/key_emoji",
    };

    /* Language as: Assamese */
    private static final String[] LANGUAGE_as = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        /* 45 */ "\u0985",
        /* 46~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null,
        /* ~62 */
        /* 63 */ "\u09E7",
        /* 64 */ "\u09E8",
        /* 65 */ "\u09E9",
        /* 66 */ "\u09EA",
        /* 67 */ "\u09EB",
        /* 68 */ "\u09EC",
        /* 69 */ "\u09ED",
        /* 70 */ "\u09EE",
        /* 71 */ "\u09EF",
        /* 72 */ "\u09E6",
        /* 73 */ "\u09E7\u09E8\u09E9",
        /* 74 */ null,
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language bn: Bengali */
    private static final String[] LANGUAGE_bn = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        /* 45 */ "\u0985",
        /* 46~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null,
        /* ~62 */
        /* 63 */ "\u09E7",
        /* 64 */ "\u09E8",
        /* 65 */ "\u09E9",
        /* 66 */ "\u09EA",
        /* 67 */ "\u09EB",
        /* 68 */ "\u09EC",
        /* 69 */ "\u09ED",
        /* 70 */ "\u09EE",
        /* 71 */ "\u09EF",
        /* 72 */ "\u09E6",
        /* 73 */ "\u09E7\u09E8\u09E9",
        /* 74 */ null,
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language en: English */
    private static final String[] LANGUAGE_en = {
        // U+00E0: "à" LATIN SMALL LETTER A WITH GRAVE
        // U+00E1: "á" LATIN SMALL LETTER A WITH ACUTE
        // U+00E2: "â" LATIN SMALL LETTER A WITH CIRCUMFLEX
        // U+00E4: "ä" LATIN SMALL LETTER A WITH DIAERESIS
        // U+00E6: "æ" LATIN SMALL LETTER AE
        // U+00E3: "ã" LATIN SMALL LETTER A WITH TILDE
        // U+00E5: "å" LATIN SMALL LETTER A WITH RING ABOVE
        // U+0101: "ā" LATIN SMALL LETTER A WITH MACRON
        /* 0 */ "\u00E0,\u00E1,\u00E2,\u00E4,\u00E6,\u00E3,\u00E5,\u0101",
        // U+00E8: "è" LATIN SMALL LETTER E WITH GRAVE
        // U+00E9: "é" LATIN SMALL LETTER E WITH ACUTE
        // U+00EA: "ê" LATIN SMALL LETTER E WITH CIRCUMFLEX
        // U+00EB: "ë" LATIN SMALL LETTER E WITH DIAERESIS
        // U+0113: "ē" LATIN SMALL LETTER E WITH MACRON
        /* 1 */ "\u00E8,\u00E9,\u00EA,\u00EB,\u0113",
        // U+00EE: "î" LATIN SMALL LETTER I WITH CIRCUMFLEX
        // U+00EF: "ï" LATIN SMALL LETTER I WITH DIAERESIS
        // U+00ED: "í" LATIN SMALL LETTER I WITH ACUTE
        // U+012B: "ī" LATIN SMALL LETTER I WITH MACRON
        // U+00EC: "ì" LATIN SMALL LETTER I WITH GRAVE
        /* 2 */ "\u00EE,\u00EF,\u00ED,\u012B,\u00EC",
        // U+00F4: "ô" LATIN SMALL LETTER O WITH CIRCUMFLEX
        // U+00F6: "ö" LATIN SMALL LETTER O WITH DIAERESIS
        // U+00F2: "ò" LATIN SMALL LETTER O WITH GRAVE
        // U+00F3: "ó" LATIN SMALL LETTER O WITH ACUTE
        // U+0153: "œ" LATIN SMALL LIGATURE OE
        // U+00F8: "ø" LATIN SMALL LETTER O WITH STROKE
        // U+014D: "ō" LATIN SMALL LETTER O WITH MACRON
        // U+00F5: "õ" LATIN SMALL LETTER O WITH TILDE
        /* 3 */ "\u00F4,\u00F6,\u00F2,\u00F3,\u0153,\u00F8,\u014D,\u00F5",
        // U+00FB: "û" LATIN SMALL LETTER U WITH CIRCUMFLEX
        // U+00FC: "ü" LATIN SMALL LETTER U WITH DIAERESIS
        // U+00F9: "ù" LATIN SMALL LETTER U WITH GRAVE
        // U+00FA: "ú" LATIN SMALL LETTER U WITH ACUTE
        // U+016B: "ū" LATIN SMALL LETTER U WITH MACRON
        /* 4 */ "\u00FB,\u00FC,\u00F9,\u00FA,\u016B",
        // U+00DF: "ß" LATIN SMALL LETTER SHARP S
        /* 5 */ "\u00DF",
        // U+00F1: "ñ" LATIN SMALL LETTER N WITH TILDE
        /* 6 */ "\u00F1",
        // U+00E7: "ç" LATIN SMALL LETTER C WITH CEDILLA
        /* 7 */ "\u00E7",
    };

    /* Language hi: Hindi */
    private static final String[] LANGUAGE_hi = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        // Label for "switch to alphabetic" key.
        // U+0915: "क" DEVANAGARI LETTER KA
        // U+0916: "ख" DEVANAGARI LETTER KHA
        // U+0917: "ग" DEVANAGARI LETTER GA
        /* 45 */ "\u0915\u0916\u0917",
        /* 46~ */
        null, null, null, null, null,
        /* ~50 */
        // U+20B9: "₹" INDIAN RUPEE SIGN
        /* 51 */ "\u20B9",
        /* 52~ */
        null, null, null, null, null, null, null, null, null, null, null,
        /* ~62 */
        // U+0967: "१" DEVANAGARI DIGIT ONE
        /* 63 */ "\u0967",
        // U+0968: "२" DEVANAGARI DIGIT TWO
        /* 64 */ "\u0968",
        // U+0969: "३" DEVANAGARI DIGIT THREE
        /* 65 */ "\u0969",
        // U+096A: "४" DEVANAGARI DIGIT FOUR
        /* 66 */ "\u096A",
        // U+096B: "५" DEVANAGARI DIGIT FIVE
        /* 67 */ "\u096B",
        // U+096C: "६" DEVANAGARI DIGIT SIX
        /* 68 */ "\u096C",
        // U+096D: "७" DEVANAGARI DIGIT SEVEN
        /* 69 */ "\u096D",
        // U+096E: "८" DEVANAGARI DIGIT EIGHT
        /* 70 */ "\u096E",
        // U+096F: "९" DEVANAGARI DIGIT NINE
        /* 71 */ "\u096F",
        // U+0966: "०" DEVANAGARI DIGIT ZERO
        /* 72 */ "\u0966",
        // Label for "switch to symbols" key.
        /* 73 */ "?\u0967\u0968\u0969",
        // Label for "switch to symbols with microphone" key. This string shouldn't include the "mic"
        // part because it'll be appended by the code.
        /* 74 */ "\u0967\u0968\u0969",
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language kn: Kannada */
    private static final String[] LANGUAGE_kn = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        /* 45 */ "\u0C85",
        /* 46~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null,
        /* ~62 */
        /* 63 */ "\u0CE7",
        /* 64 */ "\u0CE8",
        /* 65 */ "\u0CE9",
        /* 66 */ "\u0CEA",
        /* 67 */ "\u0CEB",
        /* 68 */ "\u0CEC",
        /* 69 */ "\u0CED",
        /* 70 */ "\u0CEE",
        /* 71 */ "\u0CEF",
        /* 72 */ "\u0CE6",
        /* 73 */ "\u0CE7\u0CE8\u0CE9",
        /* 74 */ null,
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language ml: Malayalam */
    private static final String[] LANGUAGE_ml = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        /* 45 */ "\u0D05",
        /* 46~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~74 */
        // U+0966: "൧" MALAYALAM DIGIT ONE
        /* 75 */ "\u0D67",
        // U+0966: "൨" MALAYALAM DIGIT TWO
        /* 76 */ "\u0D68",
        // U+0966: "൩" MALAYALAM DIGIT THREE
        /* 77 */ "\u0D69",
        // U+0966: "൪" MALAYALAM DIGIT FOUR
        /* 78 */ "\u0D6A",
        // U+0966: "൫" MALAYALAM DIGIT FIVE
        /* 79 */ "\u0D6B",
        // U+0966: "൬" MALAYALAM DIGIT SIX
        /* 80 */ "\u0D6C",
        // U+0966: "൭" MALAYALAM DIGIT SEVEN
        /* 81 */ "\u0D6D",
        // U+0966: "൮" MALAYALAM DIGIT EIGHT
        /* 82 */ "\u0D6E",
        // U+0966: "൯" MALAYALAM DIGIT NINE
        /* 83 */ "\u0D6F",
        // U+0966: "൦" MALAYALAM DIGIT ZERO
        /* 84 */ "\u0D66",
    };

    /* Language mnw: mnw */
    private static final String[] LANGUAGE_mnw = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        /* 45 */ "\u1000",
        /* 46~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null,
        /* ~62 */
        /* 63 */ "\u1041",
        /* 64 */ "\u1042",
        /* 65 */ "\u1043",
        /* 66 */ "\u1044",
        /* 67 */ "\u1045",
        /* 68 */ "\u1046",
        /* 69 */ "\u1047",
        /* 70 */ "\u1048",
        /* 71 */ "\u1049",
        /* 72 */ "\u1040",
        // Label for "switch to symbols" key.
        /* 73 */ "\u1041",
        /* 74 */ null,
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language my: Burmese */
    private static final String[] LANGUAGE_my = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        /* 45 */ "\u1000",
        /* 46~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null,
        /* ~62 */
        /* 63 */ "\u1041",
        /* 64 */ "\u1042",
        /* 65 */ "\u1043",
        /* 66 */ "\u1044",
        /* 67 */ "\u1045",
        /* 68 */ "\u1046",
        /* 69 */ "\u1047",
        /* 70 */ "\u1048",
        /* 71 */ "\u1049",
        /* 72 */ "\u1040",
        // Label for "switch to symbols" key.
        /* 73 */ "\u1041",
        /* 74 */ null,
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language ne: Nepali */
    private static final String[] LANGUAGE_ne = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        // Label for "switch to alphabetic" key.
        // U+0915: "क" DEVANAGARI LETTER KA
        // U+0916: "ख" DEVANAGARI LETTER KHA
        // U+0917: "ग" DEVANAGARI LETTER GA
        /* 45 */ "\u0915\u0916\u0917",
        /* 46~ */
        null, null, null, null, null,
        /* ~50 */
        // U+0930/U+0941/U+002E "रु." NEPALESE RUPEE SIGN
        /* 51 */ "\u0930\u0941.",
        /* 52~ */
        null, null, null, null, null, null, null, null, null, null, null,
        /* ~62 */
        // U+0967: "१" DEVANAGARI DIGIT ONE
        /* 63 */ "\u0967",
        // U+0968: "२" DEVANAGARI DIGIT TWO
        /* 64 */ "\u0968",
        // U+0969: "३" DEVANAGARI DIGIT THREE
        /* 65 */ "\u0969",
        // U+096A: "४" DEVANAGARI DIGIT FOUR
        /* 66 */ "\u096A",
        // U+096B: "५" DEVANAGARI DIGIT FIVE
        /* 67 */ "\u096B",
        // U+096C: "६" DEVANAGARI DIGIT SIX
        /* 68 */ "\u096C",
        // U+096D: "७" DEVANAGARI DIGIT SEVEN
        /* 69 */ "\u096D",
        // U+096E: "८" DEVANAGARI DIGIT EIGHT
        /* 70 */ "\u096E",
        // U+096F: "९" DEVANAGARI DIGIT NINE
        /* 71 */ "\u096F",
        // U+0966: "०" DEVANAGARI DIGIT ZERO
        /* 72 */ "\u0966",
        // Label for "switch to symbols" key.
        /* 73 */ "?\u0967\u0968\u0969",
        // Label for "switch to symbols with microphone" key. This string shouldn't include the "mic"
        // part because it'll be appended by the code.
        /* 74 */ "\u0967\u0968\u0969",
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language or: Oriya */
    private static final String[] LANGUAGE_or = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        /* 45 */ "\u0B05",
        /* 46~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null,
        /* ~62 */
        /* 63 */ "\u0B67",
        /* 64 */ "\u0B68",
        /* 65 */ "\u0B69",
        /* 66 */ "\u0B6A",
        /* 67 */ "\u0B6B",
        /* 68 */ "\u0B6C",
        /* 69 */ "\u0B6D",
        /* 70 */ "\u0B6E",
        /* 71 */ "\u0B6F",
        /* 72 */ "\u0B66",
        /* 73 */ "\u0B67\u0B68\u0B69",
        /* 74 */ null,
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language pa: Panjabi */
    private static final String[] LANGUAGE_pa = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        /* 45 */ "\u0A05",
        /* 46~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null,
        /* ~62 */
        /* 63 */ "\u0A67",
        /* 64 */ "\u0A68",
        /* 65 */ "\u0A69",
        /* 66 */ "\u0A6A",
        /* 67 */ "\u0A6B",
        /* 68 */ "\u0A6C",
        /* 69 */ "\u0A6D",
        /* 70 */ "\u0A6E",
        /* 71 */ "\u0A6F",
        /* 72 */ "\u0A66",
        /* 73 */ "\u0A67\u0A68\u0A69",
        /* 74 */ null,
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language ta: Tamil */
    private static final String[] LANGUAGE_ta = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        /* 45 */ "\u0B85",
        /* 46~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null,
        /* ~62 */
        // U+0BE7: "௧" TAMIL DIGIT ONE
        /* 63 */ "\u0BE7",
        // U+0BE8: "௨" TAMIL DIGIT TWO
        /* 64 */ "\u0BE8",
        // U+0BE9: "௩" TAMIL DIGIT THREE
        /* 65 */ "\u0BE9",
        // U+0BEA: "௪" TAMIL DIGIT FOUR
        /* 66 */ "\u0BEA",
        // U+0BEB: "௫" TAMIL DIGIT FIVE
        /* 67 */ "\u0BEB",
        // U+0BEC: "௬" TAMIL DIGIT SIX
        /* 68 */ "\u0BEC",
        // U+0BED: "௭" TAMIL DIGIT SEVEN
        /* 69 */ "\u0BED",
        // U+0BEE: "௮" TAMIL DIGIT EIGHT
        /* 70 */ "\u0BEE",
        // U+0BEF: "௯" TAMIL DIGIT NINE
        /* 71 */ "\u0BEF",
        // U+0BE6: "௦" TAMIL DIGIT ZERO
        /* 72 */ "\u0BE6",
        /* 73 */ "\u0BE7\u0BE8\u0BE9",
        /* 74 */ null,
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language te: Telugu */
    private static final String[] LANGUAGE_te = {
        /* 0~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        /* ~44 */
        /* 45 */ "\u0C05",
        /* 46~ */
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null,
        /* ~62 */
        /* 63 */ "\u0C67",
        /* 64 */ "\u0C68",
        /* 65 */ "\u0C69",
        /* 66 */ "\u0C6A",
        /* 67 */ "\u0C6B",
        /* 68 */ "\u0C6C",
        /* 69 */ "\u0C6D",
        /* 70 */ "\u0C6E",
        /* 71 */ "\u0C6F",
        /* 72 */ "\u0C66",
        /* 73 */ "\u0C67\u0C68\u0C69",
        /* 74 */ null,
        /* 75 */ "1",
        /* 76 */ "2",
        /* 77 */ "3",
        /* 78 */ "4",
        /* 79 */ "5",
        /* 80 */ "6",
        /* 81 */ "7",
        /* 82 */ "8",
        /* 83 */ "9",
        /* 84 */ "0",
    };

    /* Language zz: Alphabet */
    private static final String[] LANGUAGE_zz = {
        // U+00E0: "à" LATIN SMALL LETTER A WITH GRAVE
        // U+00E1: "á" LATIN SMALL LETTER A WITH ACUTE
        // U+00E2: "â" LATIN SMALL LETTER A WITH CIRCUMFLEX
        // U+00E3: "ã" LATIN SMALL LETTER A WITH TILDE
        // U+00E4: "ä" LATIN SMALL LETTER A WITH DIAERESIS
        // U+00E5: "å" LATIN SMALL LETTER A WITH RING ABOVE
        // U+00E6: "æ" LATIN SMALL LETTER AE
        // U+0101: "ā" LATIN SMALL LETTER A WITH MACRON
        // U+0103: "ă" LATIN SMALL LETTER A WITH BREVE
        // U+0105: "ą" LATIN SMALL LETTER A WITH OGONEK
        // U+00AA: "ª" FEMININE ORDINAL INDICATOR
        /* 0 */ "\u00E0,\u00E1,\u00E2,\u00E3,\u00E4,\u00E5,\u00E6,\u00E3,\u00E5,\u0101,\u0103,\u0105,\u00AA",
        // U+00E8: "è" LATIN SMALL LETTER E WITH GRAVE
        // U+00E9: "é" LATIN SMALL LETTER E WITH ACUTE
        // U+00EA: "ê" LATIN SMALL LETTER E WITH CIRCUMFLEX
        // U+00EB: "ë" LATIN SMALL LETTER E WITH DIAERESIS
        // U+0113: "ē" LATIN SMALL LETTER E WITH MACRON
        // U+0115: "ĕ" LATIN SMALL LETTER E WITH BREVE
        // U+0117: "ė" LATIN SMALL LETTER E WITH DOT ABOVE
        // U+0119: "ę" LATIN SMALL LETTER E WITH OGONEK
        // U+011B: "ě" LATIN SMALL LETTER E WITH CARON
        /* 1 */ "\u00E8,\u00E9,\u00EA,\u00EB,\u0113,\u0115,\u0117,\u0119,\u011B",
        // U+00EC: "ì" LATIN SMALL LETTER I WITH GRAVE
        // U+00ED: "í" LATIN SMALL LETTER I WITH ACUTE
        // U+00EE: "î" LATIN SMALL LETTER I WITH CIRCUMFLEX
        // U+00EF: "ï" LATIN SMALL LETTER I WITH DIAERESIS
        // U+0129: "ĩ" LATIN SMALL LETTER I WITH TILDE
        // U+012B: "ī" LATIN SMALL LETTER I WITH MACRON
        // U+012D: "ĭ" LATIN SMALL LETTER I WITH BREVE
        // U+012F: "į" LATIN SMALL LETTER I WITH OGONEK
        // U+0131: "ı" LATIN SMALL LETTER DOTLESS I
        // U+0133: "ĳ" LATIN SMALL LIGATURE IJ
        /* 2 */ "\u00EC,\u00ED,\u00EE,\u00EF,\u0129,\u012B,\u012D,\u012F,\u0131,\u0133",
        // U+00F2: "ò" LATIN SMALL LETTER O WITH GRAVE
        // U+00F3: "ó" LATIN SMALL LETTER O WITH ACUTE
        // U+00F4: "ô" LATIN SMALL LETTER O WITH CIRCUMFLEX
        // U+00F5: "õ" LATIN SMALL LETTER O WITH TILDE
        // U+00F6: "ö" LATIN SMALL LETTER O WITH DIAERESIS
        // U+00F8: "ø" LATIN SMALL LETTER O WITH STROKE
        // U+014D: "ō" LATIN SMALL LETTER O WITH MACRON
        // U+014F: "ŏ" LATIN SMALL LETTER O WITH BREVE
        // U+0151: "ő" LATIN SMALL LETTER O WITH DOUBLE ACUTE
        // U+0153: "œ" LATIN SMALL LIGATURE OE
        // U+00BA: "º" MASCULINE ORDINAL INDICATOR
        /* 3 */ "\u00F2,\u00F3,\u00F4,\u00F5,\u00F6,\u00F8,\u014D,\u014F,\u0151,\u0153,\u00BA",
        // U+00F9: "ù" LATIN SMALL LETTER U WITH GRAVE
        // U+00FA: "ú" LATIN SMALL LETTER U WITH ACUTE
        // U+00FB: "û" LATIN SMALL LETTER U WITH CIRCUMFLEX
        // U+00FC: "ü" LATIN SMALL LETTER U WITH DIAERESIS
        // U+0169: "ũ" LATIN SMALL LETTER U WITH TILDE
        // U+016B: "ū" LATIN SMALL LETTER U WITH MACRON
        // U+016D: "ŭ" LATIN SMALL LETTER U WITH BREVE
        // U+016F: "ů" LATIN SMALL LETTER U WITH RING ABOVE
        // U+0171: "ű" LATIN SMALL LETTER U WITH DOUBLE ACUTE
        // U+0173: "ų" LATIN SMALL LETTER U WITH OGONEK
        /* 4 */ "\u00F9,\u00FA,\u00FB,\u00FC,\u0169,\u016B,\u016D,\u016F,\u0171,\u0173",
        // U+00DF: "ß" LATIN SMALL LETTER SHARP S
        // U+015B: "ś" LATIN SMALL LETTER S WITH ACUTE
        // U+015D: "ŝ" LATIN SMALL LETTER S WITH CIRCUMFLEX
        // U+015F: "ş" LATIN SMALL LETTER S WITH CEDILLA
        // U+0161: "š" LATIN SMALL LETTER S WITH CARON
        // U+017F: "ſ" LATIN SMALL LETTER LONG S
        /* 5 */ "\u00DF,\u015B,\u015D,\u015F,\u0161,\u017F",
        // U+00F1: "ñ" LATIN SMALL LETTER N WITH TILDE
        // U+0144: "ń" LATIN SMALL LETTER N WITH ACUTE
        // U+0146: "ņ" LATIN SMALL LETTER N WITH CEDILLA
        // U+0148: "ň" LATIN SMALL LETTER N WITH CARON
        // U+0149: "ŉ" LATIN SMALL LETTER N PRECEDED BY APOSTROPHE
        // U+014B: "ŋ" LATIN SMALL LETTER ENG
        /* 6 */ "\u00F1,\u0144,\u0146,\u0148,\u0149,\u014B",
        // U+00E7: "ç" LATIN SMALL LETTER C WITH CEDILLA
        // U+0107: "ć" LATIN SMALL LETTER C WITH ACUTE
        // U+0109: "ĉ" LATIN SMALL LETTER C WITH CIRCUMFLEX
        // U+010B: "ċ" LATIN SMALL LETTER C WITH DOT ABOVE
        // U+010D: "č" LATIN SMALL LETTER C WITH CARON
        /* 7 */ "\u00E7,\u0107,\u0109,\u010B,\u010D",
        // U+00FD: "ý" LATIN SMALL LETTER Y WITH ACUTE
        // U+0177: "ŷ" LATIN SMALL LETTER Y WITH CIRCUMFLEX
        // U+00FF: "ÿ" LATIN SMALL LETTER Y WITH DIAERESIS
        // U+0133: "ĳ" LATIN SMALL LIGATURE IJ
        /* 8 */ "\u00FD,\u0177,\u00FF,\u0133",
        // U+010F: "ď" LATIN SMALL LETTER D WITH CARON
        // U+0111: "đ" LATIN SMALL LETTER D WITH STROKE
        // U+00F0: "ð" LATIN SMALL LETTER ETH
        /* 9 */ "\u010F,\u0111,\u00F0",
        // U+0155: "ŕ" LATIN SMALL LETTER R WITH ACUTE
        // U+0157: "ŗ" LATIN SMALL LETTER R WITH CEDILLA
        // U+0159: "ř" LATIN SMALL LETTER R WITH CARON
        /* 10 */ "\u0155,\u0157,\u0159",
        // U+00FE: "þ" LATIN SMALL LETTER THORN
        // U+0163: "ţ" LATIN SMALL LETTER T WITH CEDILLA
        // U+0165: "ť" LATIN SMALL LETTER T WITH CARON
        // U+0167: "ŧ" LATIN SMALL LETTER T WITH STROKE
        /* 11 */ "\u00FE,\u0163,\u0165,\u0167",
        // U+017A: "ź" LATIN SMALL LETTER Z WITH ACUTE
        // U+017C: "ż" LATIN SMALL LETTER Z WITH DOT ABOVE
        // U+017E: "ž" LATIN SMALL LETTER Z WITH CARON
        /* 12 */ "\u017A,\u017C,\u017E",
        // U+0137: "ķ" LATIN SMALL LETTER K WITH CEDILLA
        // U+0138: "ĸ" LATIN SMALL LETTER KRA
        /* 13 */ "\u0137,\u0138",
        // U+013A: "ĺ" LATIN SMALL LETTER L WITH ACUTE
        // U+013C: "ļ" LATIN SMALL LETTER L WITH CEDILLA
        // U+013E: "ľ" LATIN SMALL LETTER L WITH CARON
        // U+0140: "ŀ" LATIN SMALL LETTER L WITH MIDDLE DOT
        // U+0142: "ł" LATIN SMALL LETTER L WITH STROKE
        /* 14 */ "\u013A,\u013C,\u013E,\u0140,\u0142",
        // U+011D: "ĝ" LATIN SMALL LETTER G WITH CIRCUMFLEX
        // U+011F: "ğ" LATIN SMALL LETTER G WITH BREVE
        // U+0121: "ġ" LATIN SMALL LETTER G WITH DOT ABOVE
        // U+0123: "ģ" LATIN SMALL LETTER G WITH CEDILLA
        /* 15 */ "\u011D,\u011F,\u0121,\u0123",
        /* 16 */ null,
        // U+0125: "ĥ" LATIN SMALL LETTER H WITH CIRCUMFLEX
        /* 17 */ "\u0125",
        // U+0135: "ĵ" LATIN SMALL LETTER J WITH CIRCUMFLEX
        /* 18 */ "\u0135",
        // U+0175: "ŵ" LATIN SMALL LETTER W WITH CIRCUMFLEX
        /* 19 */ "\u0175",
    };

    private static final Object[] LANGUAGES_AND_TEXTS = {
        "DEFAULT", LANGUAGE_DEFAULT, /* default */
        "as", LANGUAGE_as, /* Assamese */
        "bn", LANGUAGE_bn, /* Bengali */
        "en", LANGUAGE_en, /* English */
        "hi", LANGUAGE_hi, /* Hindi */
        "kn", LANGUAGE_kn, /* Kannada */
        "ml", LANGUAGE_ml, /* Malayalam */
        "mnw", LANGUAGE_mnw, /* mnw */
        "my", LANGUAGE_my, /* Burmese */
        "ne", LANGUAGE_ne, /* Nepali */
        "or", LANGUAGE_or, /* Oriya */
        "pa", LANGUAGE_pa, /* Panjabi */
        "ta", LANGUAGE_ta, /* Tamil */
        "te", LANGUAGE_te, /* Telugu */
        "zz", LANGUAGE_zz, /* Alphabet */
    };

    static {
        int id = 0;
        for (final String name : NAMES) {
            sNameToIdsMap.put(name, id++);
        }

        for (int i = 0; i < LANGUAGES_AND_TEXTS.length; i += 2) {
            final String language = (String)LANGUAGES_AND_TEXTS[i];
            final String[] texts = (String[])LANGUAGES_AND_TEXTS[i + 1];
            sLocaleToTextsMap.put(language, texts);
        }
    }
}
