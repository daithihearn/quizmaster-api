package ie.daithi.quizmaster.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "appUsers")
data class AppUser (
    @Id
    var id: String? = null,
    @Indexed(unique = true)
    val subject: String,
    @Indexed(unique = true)
    val email: String,
    val name: String,
    val picture: String? = null
)