package ie.daithi.quizmaster.web.model

data class Leaderboard (
        val gameId: String,
        val roundId: String? = null,
        val scores: List<Score>
)