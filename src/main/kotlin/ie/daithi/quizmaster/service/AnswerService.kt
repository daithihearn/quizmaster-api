package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.repositories.AnswerRepo
import ie.daithi.quizmaster.web.model.Score
import ie.daithi.quizmaster.web.model.enums.PublishContentType
import org.apache.logging.log4j.LogManager
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service

@Service
class AnswerService(
    private val answerRepo: AnswerRepo,
    private val gameService: GameService,
    private val scoringService: ScoringService,
    private val mongoOperations: MongoOperations,
    private val publishService: PublishService
) {

    fun submitAnswer(id: String, gameId: String, roundIndex: Int, questionIndex: Int, answer: String) {

        val answerObj = Answer(playerId = id, gameId = gameId, roundIndex = roundIndex, questionIndex = questionIndex, answer = answer)

        // 1. Attempt to correct
        val game = gameService.get(gameId)
        val question = gameService.getQuestion(game.quizId!!, roundIndex, questionIndex)
        scoringService.attemptScore(question!!.answer!!, answerObj, question.points)

        // 2. Store answer
        answerRepo.save(answerObj)

        // 3. Publish to Quiz Master if not able to correct
        if (answerObj.score == null)
            publishService.publishContent(game.quizMasterId!!, "/scoring", answerObj)
    }

    fun getUnscoredAnswers(gameId: String): List<Answer> {
        return answerRepo.findByGameIdAndScore(gameId = gameId, score = null)
    }

    /**
        db.answers.aggregate([
        { "$match" : { gameId : "5e932fb170416b4231a2fa43" }},
        { "$group" : { "_id" : "$playerId" , score: {$sum: { "$toDouble": "$score"}}}},
        { "$project": { "playerId": "$_id", "score":"$score"}}
        ])
     */
    fun getLeaderboard(id: String): List<Score> {
        val match = Aggregation.match(Criteria.where("gameId").`is`(id))
        val group = Aggregation.group("\$playerId").sum("score").`as`("score")
        val project = Aggregation.project()
                .and("\$_id").`as`("playerId")
                .and("\$score").`as`("score")

        val aggregation = Aggregation.newAggregation(match, group, project)
        return mongoOperations.aggregate(aggregation, Answer::class.java, Score::class.java).mappedResults
    }

    fun save(answer: Answer) {
        answerRepo.save(answer)
    }

    fun publishLeaderboard(id: String) {
        // 1. Get the leaderboard
        val leaderboard = getLeaderboard(id)

        // 2. Get the game
        val game = gameService.get(id)

        // 3. Publish the leaderboard
        publishService.publishContent(game.players.map { it.displayName }, "/game", leaderboard, id, PublishContentType.LEADERBOARD )

    }

    fun hasAnswered(gameId: String, playerId: String): Boolean {
        return answerRepo.existsByGameIdAndPlayerId(gameId, playerId)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}