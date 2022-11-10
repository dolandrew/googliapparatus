package googliapparatus.dto;

import java.util.List;

public record GoogliResponseDto(List<SongDto> songs,
                                List<SimilarResult> similarResults) {
}
