/*
 * FFmpegMediaMetadataRetriever: A unified interface for retrieving frame 
 * and meta data from an input media file.
 *
 * Copyright 2015 William Seemann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yepeer.le;

import java.util.ArrayList;

import wseemann.media.FFmpegMediaMetadataRetriever;
import yepeer.le.objects.TargetForeign;

public class Constants {
    public static final String SKU_REMOVE_ADS="yepeer.removeads";
    public static final String SKU_REMOVE_ADS_GIVE="yepeer.removeads.give";
    public static final String SKU_GIVE="yepeer.give";
    public static final String BASE_64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl7r+1eddgMyMQMm2rN9l8P2gwCw/imJUfyMG7S8xGQ7dIT1Q7rohpCT1DSwz3rC1jZ5HXpYwHwMWZI3JzopNNfKGlc8HR7+mfYKwF9uduqeukOZ2P6qlrqbbPIWNckY0YMk87km2PXU05B9all9lJfda7PvpzP2ywrz7eSH9hyrN9Ck4gdzIVgZF3zHuoj9c85dF8JNCl1ZcemHD/Wpj+gqiCOlyuKU+Xjj/VFAYoSr8XqJMdF4kC0QvgQ7yfkpvnmLbNh7l9sguPH39QLetsi4WK80XsMuI6ZU9miQknkRO6tyHSlJPoqEQypIzvswmD9i1To9xP8f373WUnw8KjwIDAQAB";
    public static final String[] METADATA_KEYS = {
            FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM,
            FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM_ARTIST,
            FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST,
            FFmpegMediaMetadataRetriever.METADATA_KEY_COMMENT,
            FFmpegMediaMetadataRetriever.METADATA_KEY_COMPOSER,
            FFmpegMediaMetadataRetriever.METADATA_KEY_COPYRIGHT,
            FFmpegMediaMetadataRetriever.METADATA_KEY_CREATION_TIME,
            FFmpegMediaMetadataRetriever.METADATA_KEY_DATE,
            FFmpegMediaMetadataRetriever.METADATA_KEY_DISC,
            FFmpegMediaMetadataRetriever.METADATA_KEY_ENCODER,
            FFmpegMediaMetadataRetriever.METADATA_KEY_ENCODED_BY,
            FFmpegMediaMetadataRetriever.METADATA_KEY_FILENAME,
            FFmpegMediaMetadataRetriever.METADATA_KEY_GENRE,
            FFmpegMediaMetadataRetriever.METADATA_KEY_LANGUAGE,
            FFmpegMediaMetadataRetriever.METADATA_KEY_PERFORMER,
            FFmpegMediaMetadataRetriever.METADATA_KEY_PUBLISHER,
            FFmpegMediaMetadataRetriever.METADATA_KEY_SERVICE_NAME,
            FFmpegMediaMetadataRetriever.METADATA_KEY_SERVICE_PROVIDER,
            FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE,
            FFmpegMediaMetadataRetriever.METADATA_KEY_TRACK,
            FFmpegMediaMetadataRetriever.METADATA_KEY_VARIANT_BITRATE,
            FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION,
            FFmpegMediaMetadataRetriever.METADATA_KEY_AUDIO_CODEC,
            FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_CODEC,
            FFmpegMediaMetadataRetriever.METADATA_KEY_ICY_METADATA,
            FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION,
            FFmpegMediaMetadataRetriever.METADATA_KEY_FRAMERATE,
            FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE
    };
    private static ArrayList<TargetForeign> lists = null;

    public static ArrayList<TargetForeign> getTargetForeign() {
        if (lists == null) {
            lists = new ArrayList<TargetForeign>();
            lists.add(new TargetForeign("af", "Afrikaans"));
            lists.add(new TargetForeign("sq", "Albanian"));
            lists.add(new TargetForeign("ar", "Arabic"));
            lists.add(new TargetForeign("hy", "Armenian"));
            lists.add(new TargetForeign("az", "Azerbaijani"));
            lists.add(new TargetForeign("eu", "Basque"));
            lists.add(new TargetForeign("be", "Belarusian"));
            lists.add(new TargetForeign("bn", "Bengali"));
            lists.add(new TargetForeign("bs", "Bosnlan"));
            lists.add(new TargetForeign("bg", "Bulgarlan"));
            lists.add(new TargetForeign("ca", "Catalan"));
            lists.add(new TargetForeign("ceb", "Cebuano"));
            lists.add(new TargetForeign("ny", "Chichewa"));
            lists.add(new TargetForeign("zh-CN", "Chinese(Simplified)"));
            lists.add(new TargetForeign("zh-TW", "Chinese(Traditional)"));
            lists.add(new TargetForeign("hr", "Croatian"));
            lists.add(new TargetForeign("cs", "Czech"));
            lists.add(new TargetForeign("da", "Danish"));
            lists.add(new TargetForeign("nl", "Dutch"));
            lists.add(new TargetForeign("en", "English"));
            lists.add(new TargetForeign("eo", "Esperanto"));
            lists.add(new TargetForeign("et", "Estonian"));
            lists.add(new TargetForeign("tl", "Filipino"));
            lists.add(new TargetForeign("fi", "Finnish"));
            lists.add(new TargetForeign("fr", "French"));
            lists.add(new TargetForeign("gl", "Galician"));
            lists.add(new TargetForeign("ka", "Geogrian"));
            lists.add(new TargetForeign("ge", "German"));
            lists.add(new TargetForeign("el", "Greek"));
            lists.add(new TargetForeign("gu", "Gujarati"));
            lists.add(new TargetForeign("ht", "Haitian Creole"));
            lists.add(new TargetForeign("ha", "Hausa"));
            lists.add(new TargetForeign("iw", "Hebrew"));
            lists.add(new TargetForeign("hi", "Hindi"));
            lists.add(new TargetForeign("hmn", "Hmong"));
            lists.add(new TargetForeign("hu", "Hungarian"));
            lists.add(new TargetForeign("is", "Icelandic"));
            lists.add(new TargetForeign("ig", "Igbo"));
            lists.add(new TargetForeign("id", "Indonesian"));
            lists.add(new TargetForeign("ga", "Irish"));
            lists.add(new TargetForeign("it", "Italian"));
            lists.add(new TargetForeign("ja", "Japanese"));
            lists.add(new TargetForeign("jw", "Javanese"));
            lists.add(new TargetForeign("kn", "Kannada"));
            lists.add(new TargetForeign("kk", "Kazakh"));
            lists.add(new TargetForeign("km", "Khmer"));
            lists.add(new TargetForeign("ko", "Korean"));
            lists.add(new TargetForeign("lo", "Lao"));
            lists.add(new TargetForeign("la", "Latin"));
            lists.add(new TargetForeign("lv", "Latvian"));
            lists.add(new TargetForeign("lt", "Lithuanian"));
            lists.add(new TargetForeign("mk", "Macedonian"));
            lists.add(new TargetForeign("mg", "Malagasy"));
            lists.add(new TargetForeign("ms", "Malay"));
            lists.add(new TargetForeign("ml", "Malayalam"));
            lists.add(new TargetForeign("mt", "Maltese"));
            lists.add(new TargetForeign("mi", "Maori"));
            lists.add(new TargetForeign("mr", "Marathi"));
            lists.add(new TargetForeign("mn", "Mongolian"));
            lists.add(new TargetForeign("my", "Myanmar(Burmese)"));
            lists.add(new TargetForeign("ne", "Nepali"));
            lists.add(new TargetForeign("no", "Norwegian"));
            lists.add(new TargetForeign("fa", "Persian"));
            lists.add(new TargetForeign("pl", "Polish"));
            lists.add(new TargetForeign("pt", "Portuguese"));
            lists.add(new TargetForeign("pa", "Punjabi"));
            lists.add(new TargetForeign("ro", "Romanian"));
            lists.add(new TargetForeign("ru", "Russian"));
            lists.add(new TargetForeign("sr", "Serbian"));
            lists.add(new TargetForeign("st", "Sesotho"));
            lists.add(new TargetForeign("si", "Sinhala"));
            lists.add(new TargetForeign("sk", "Slovak"));
            lists.add(new TargetForeign("sl", "Slovenlan"));
            lists.add(new TargetForeign("so", "Somali"));
            lists.add(new TargetForeign("es", "Spanish"));
            lists.add(new TargetForeign("su", "Sundanese"));
            lists.add(new TargetForeign("sv", "Swedish"));
            lists.add(new TargetForeign("tg", "Tajik"));
            lists.add(new TargetForeign("ta", "Tamil"));
            lists.add(new TargetForeign("te", "Telugu"));
            lists.add(new TargetForeign("th", "Thai"));
            lists.add(new TargetForeign("tr", "Turkish"));
            lists.add(new TargetForeign("uk", "Ukrainian"));
            lists.add(new TargetForeign("ur", "Urdu"));
            lists.add(new TargetForeign("uz", "Uzbek"));
            lists.add(new TargetForeign("vi", "Vietnamese"));
            lists.add(new TargetForeign("cy", "Welsh"));
            lists.add(new TargetForeign("yi", "Yiddish"));
            lists.add(new TargetForeign("yo", "Yoruba"));
            lists.add(new TargetForeign("zu", "Zulu"));
        }
        return lists;
    }

}
