package googliapparatus.helper;

import googliapparatus.dto.SongDto;
import googliapparatus.entity.SongEntity;

public class SnippetHelper {
    public static void findRelevantLyrics(String filter, SongEntity songEntity, SongDto songDTO) {
        String lyrics = songEntity.getLyrics();
        if (lyrics != null) {
            int index = lyrics.indexOf(filter);
            int snippetLength = 20;
            while (index >= 0) {
                String snippet = getSnippet(filter, lyrics, index, snippetLength);
                snippet = capitalize(snippet);
                snippet = highlightFilter(filter, snippet);
                songDTO.getLyricSnippets().add(snippet.trim());
                index = getNextFilterOccurencce(filter, lyrics, index);
            }
        }
    }

    private static String capitalize(String snippet) {
        return snippet.replace(" i ", " I ");
    }

    private static int getNextFilterOccurencce(String filter, String lyrics, int index) {
        return lyrics.indexOf(filter, index + 1);
    }

    private static String getSnippet(String filter, String lyrics, int index, int snippetLength) {
        return lyrics.substring(Math.max(0, index - snippetLength),
                Math.min(filter.length() + index + snippetLength, lyrics.length())) + "...";
    }

    private static String highlightFilter(String filter, String snippet) {
        return snippet.replace(filter, "<b>" + filter + "</b>");
    }
}