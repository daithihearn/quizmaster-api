package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.repositories.AnswerRepo
import ie.daithi.quizmaster.web.exceptions.AnswerResubmissionException
import ie.daithi.quizmaster.web.model.QuestionAnswerWrapper
import ie.daithi.quizmaster.web.model.Score
import ie.daithi.quizmaster.web.model.enums.PublishContentType
import org.apache.logging.log4j.LogManager
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

        // 1. Check if they already submitted an answer
        if (answerRepo.existsByGameIdAndPlayerIdAndRoundIndexAndQuestionIndex(
                playerId = id,
                gameId = gameId,
                roundIndex = roundIndex,
                questionIndex = questionIndex)) throw AnswerResubmissionException("Player already submitted and answer")

        // 2. Attempt to correct
        val game = gameService.get(gameId)
        val answerObj = Answer(playerId = id, quizId = game.quizId!!, gameId = gameId, roundIndex = roundIndex, questionIndex = questionIndex, answer = answer)
        val question = gameService.getQuestion(game.quizId!!, roundIndex, questionIndex)
        scoringService.attemptScore(question!!.answer!!, answerObj, question.points)

        // 3. Store answer
        answerRepo.save(answerObj)

        // 4. Publish to Quiz Master if not able to correct
        if (answerObj.score == null)
            publishService.publishContent(game.quizMasterId!!, "/scoring", QuestionAnswerWrapper(question, answerObj))
    }

    fun getUnscoredAnswers(gameId: String): List<QuestionAnswerWrapper> {
        val answers = answerRepo.findByGameIdAndScore(gameId = gameId, score = null)
        if(answers.isEmpty())
            return emptyList()
        return answers.map {
            val question = gameService.getQuestion(it.quizId, it.roundIndex, it.questionIndex)
            QuestionAnswerWrapper(question!!, it)
        }
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

    fun hasAnswered(gameId: String, playerId: String, roundIndex: Int, questionIndex: Int): Boolean {
        return answerRepo.existsByGameIdAndPlayerIdAndRoundIndexAndQuestionIndex(gameId, playerId, roundIndex, questionIndex)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}