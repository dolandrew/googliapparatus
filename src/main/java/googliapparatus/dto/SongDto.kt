package googliapparatus.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class SongDto {
    var link: String? = null
    var lyricSnippets: Set<String> = HashSet()
    var name: String? = null
}