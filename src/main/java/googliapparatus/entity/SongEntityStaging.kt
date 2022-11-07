package googliapparatus.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class SongEntityStaging {
    @Id
    var id: String? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false)
    var nameLower: String? = null

    @Column(nullable = false)
    var link: String? = null

    @Column
    var lyrics: String? = null

    @Column
    var lyricsBy: String? = null
}