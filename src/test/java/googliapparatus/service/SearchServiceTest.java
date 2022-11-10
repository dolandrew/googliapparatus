package googliapparatus.service;

import googliapparatus.dto.SongDto;
import googliapparatus.entity.SongEntity;
import googliapparatus.repository.SongEntityRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    private static final List<SongEntity> SONGS = new ArrayList<>();

    @Mock
    private GoogliTweeter googliTweeter;

    @Mock
    private WordsApiProxyService wordsApiProxyService;

    @Mock
    private SongEntityRepository repository;

    @InjectMocks
    private SearchService underTest;

    @BeforeAll
    static void setUp() {
        SongEntity song = new SongEntity();
        song.setName("The Lizards");
        SONGS.add(song);
    }

    @Test
    void searchEmptyFilter() {
        assertEquals(emptyList(), underTest.search("").songs());
    }

    @Test
    void searchFilterContainsSpaces() {
        lenient().doReturn(SONGS).when(repository)
                .findByLyricsContainsOrNameLowerContains("land", "land");
        List<SongDto> result = underTest.search(" land ").songs();
        assertEquals(1, result.size());
        assertEquals("The Lizards", result.get(0).getName());
    }

    @Test
    void searchFilterIsNotCaseSensitive() {
        lenient().doReturn(SONGS).when(repository)
                .findByLyricsContainsOrNameLowerContains("land", "land");
        List<SongDto> result = underTest.search(" LaNd ").songs();
        assertEquals(1, result.size());
        assertEquals("The Lizards", result.get(0).getName());
    }

    @Test
    void searchByPhrase() {
        lenient().doReturn(SONGS).when(repository)
                .findByLyricsContainsOrNameLowerContains(
                        "come from the land", "come from the land");
        List<SongDto> result = underTest.search(" come from the land ").songs();
        assertEquals(1, result.size());
        assertEquals("The Lizards", result.get(0).getName());
    }
}
