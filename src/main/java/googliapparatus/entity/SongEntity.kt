package googliapparatus.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class SongEntity {
    @Id
    var id: String? = null

    @Column(nullable = false)
    var link: String? = null

    @Column(length = 10485760)
    var lyrics: String? = null

    @Column
    var lyricsBy: String? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false)
    var nameLower: String? = null
}