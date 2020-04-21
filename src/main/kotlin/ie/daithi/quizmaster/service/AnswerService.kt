package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.repositories.AnswerRepo
import ie.daithi.quizmaster.web.exceptions.AnswerResubmissionException
import ie.daithi.quizmaster.web.exceptions.NotFoundException
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
    private val quizService: QuizService,
    private val scoringService: ScoringService,
    private val mongoOperations: MongoOperations,
    private val publishService: PublishService
) {

    fun submitAnswer(id: String, gameId: String, roundId: String, questionId: String, answer: String) {

        // 1. Check if they already submitted an answer
        if (answerRepo.existsByGameIdAndPlayerIdAndRoundIdAndQuestionId(
                playerId = id,
                gameId = gameId,
                roundId = roundId,
                questionId = questionId)) throw AnswerResubmissionException("Player already submitted and answer")

        // 2. Attempt to correct
        val game = gameService.get(gameId)
        val answerObj = Answer(playerId = id, quizId = game.quizId, gameId = gameId, roundId = roundId, questionId = questionId, answer = answer)
        val question = gameService.getQuestion(game.quizId, roundId, questionId)
        if (!question.forceManualCorrection)
            scoringService.attemptScore(question.answer, answerObj, question.points)

        // 3. Store answer
        answerRepo.save(answerObj)

        // 4. Publish to Quiz Master if not able to correct
        if (answerObj.score == null)
            publishService.publishContent(game.quizMasterId,
                    "/scoring",
                    QuestionAnswerWrapper(question, answerObj),
                    game.id!!,
                    PublishContentType.QUESTION_AND_ANSWER)
    }

    fun getUnscoredAnswers(gameId: String): List<QuestionAnswerWrapper> {
        val answers = answerRepo.findByGameIdAndScore(gameId = gameId, score = null)
        if(answers.isEmpty())
            return emptyList()
        return answers.map {
            val question = gameService.getQuestion(it.quizId, it.roundId, it.questionId)
            QuestionAnswerWrapper(question, it)
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
        publishService.publishContent(recipients = game.players.map { it.displayName },
                topic = "/game",
                content = leaderboard,
                gameId = id,
                contentType = PublishContentType.LEADERBOARD )

    }

    fun publishAnswersForRound(gameId: String, roundId: String) {
        // 1. Get game
        val game = gameService.get(gameId)

        // 2. Get quiz
        val quiz = quizService.get(game.quizId)
        val round = quiz.rounds.first { it.id == roundId }

        // 3. Publish round
        publishService.publishContent(recipients = game.players.map { it.displayName },
                topic = "/game",
                content = round,
                gameId = gameId,
                contentType = PublishContentType.ROUND_SUMMARY )
    }

    fun hasAnswered(gameId: String, playerId: String, roundId: String, questionId: String): Boolean {
        return answerRepo.existsByGameIdAndPlayerIdAndRoundIdAndQuestionId(gameId, playerId, roundId, questionId)
    }

    fun getAnswers(gameId: String): List<QuestionAnswerWrapper> {
        val answers = answerRepo.findByGameId(gameId = gameId)
        if(answers.isEmpty())
            return emptyList()
        return answers.map {
            val question = gameService.getQuestion(it.quizId, it.roundId, it.questionId)
            QuestionAnswerWrapper(question, it)
        }
    }

    fun getAnswers(gameId: String, roundId: String): List<QuestionAnswerWrapper> {
        val answers = answerRepo.findByGameIdAndRoundId(gameId = gameId, roundId = roundId )
        if(answers.isEmpty())
            return emptyList()
        return answers.map {
            val question = gameService.getQuestion(it.quizId, it.roundId, it.questionId)
            QuestionAnswerWrapper(question, it)
        }
    }

    fun getAnswers(gameId: String, roundId: String, playerId: String): List<QuestionAnswerWrapper> {
        val answers = answerRepo.findByGameIdAndRoundIdAndPlayerId(gameId = gameId, roundId = roundId, playerId = playerId )
        if(answers.isEmpty())
            return emptyList()
        return answers.map {
            val question = gameService.getQuestion(it.quizId, it.roundId, it.questionId)
            QuestionAnswerWrapper(question, it)
        }
    }

    fun getAnswersForPlayer(gameId: String, playerId: String): List<QuestionAnswerWrapper> {
        val answers = answerRepo.findByGameIdAndPlayerId(gameId = gameId, playerId = playerId)
        if(answers.isEmpty())
            return emptyList()
        return answers.map {
            val question = gameService.getQuestion(it.quizId, it.roundId, it.questionId)
            QuestionAnswerWrapper(question, it)
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}