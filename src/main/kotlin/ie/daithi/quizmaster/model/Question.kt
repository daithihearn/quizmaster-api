package ie.daithi.quizmaster.model

data class Question(
        val id: String,
        val question: String,
        val imageUri: String? = null,
        val mediaUri: String? = null,
        val answer: String,
        val options: List<String>? = null,
        val points: Float,
        val forceManualCorrection: Boolean
)