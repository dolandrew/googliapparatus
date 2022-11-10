package googliapparatus.helper;

import googliapparatus.dto.SongDto;
import googliapparatus.entity.SongEntity;

public final class SnippetHelper {
    public static void findRelevantLyrics(final String filter,
                                          final SongEntity songEntity,
                                          final SongDto songDTO) {
        String lyrics = songEntity.getLyrics();
        if (lyrics != null) {
            int index = lyrics.indexOf(filter);
            int length = 20;
            while (index >= 0) {
                var snippet = getSnippet(filter, lyrics, index, length);
                snippet = capitalize(snippet);
                snippet = highlightFilter(filter, snippet);
                songDTO.getLyricSnippets().add(snippet.trim());
                index = getNextFilterOccurence(filter, lyrics, index);
            }
        }
    }

    private static String capitalize(final String snippet) {
        return snippet.replace(" i ", " I ");
    }

    private static int getNextFilterOccurence(final String filter,
                                              final String lyrics,
                                              final int index) {
        return lyrics.indexOf(filter, index + 1);
    }

    private static String getSnippet(final String filter,
                                     final String lyrics,
                                     final int index,
                                     final int snippetLength) {
        return lyrics.substring(Math.max(0, index - snippetLength),
                Math.min(filter.length() + index
                        + snippetLength, lyrics.length())) + "...";
    }

    private static String highlightFilter(final String filter,
                                          final String snippet) {
        return snippet.replace(filter, "<b>" + filter + "</b>");
    }
}
