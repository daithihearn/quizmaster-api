package ie.daithi.quizmaster.web.model

data class UpdateProfile (
    val name: String,
    val email: String,
    val picture: String? = null
)