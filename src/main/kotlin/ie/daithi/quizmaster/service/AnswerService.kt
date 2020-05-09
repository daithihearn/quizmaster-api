package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.repositories.AnswerRepo
import ie.daithi.quizmaster.web.exceptions.AnswerResubmissionException
import ie.daithi.quizmaster.web.model.Leaderboard
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
        private val publishService: PublishService,
        private val currentContentService: CurrentContentService
) {

    fun submitAnswer(playerId: String, gameId: String, roundId: String, questionId: String, answer: String) {

        // 1. Check if they already submitted an answer
        if (answerRepo.existsByGameIdAndPlayerIdAndRoundIdAndQuestionId(
                        gameId = gameId,
                        playerId = playerId,
                        roundId = roundId,
                        questionId = questionId)) throw AnswerResubmissionException("You have already submitted and answer for this question")

        // 2. Attempt to correct
        val game = gameService.get(gameId)
        val answerObj = Answer(playerId = playerId, quizId = game.quizId, gameId = gameId, roundId = roundId, questionId = questionId, answer = answer)
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
        else
            publishService.publishContent(game.quizMasterId,
                    "/scoring",
                    playerId,
                    game.id!!,
                    PublishContentType.AUTO_ANSWERED)
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
    fun getLeaderboard(gameId: String): Leaderboard {
        val match = Aggregation.match(Criteria.where("gameId").`is`(gameId))
        val group = Aggregation.group("\$playerId").sum("score").`as`("score")
        val project = Aggregation.project()
                .and("\$_id").`as`("playerId")
                .and("\$score").`as`("score")

        val aggregation = Aggregation.newAggregation(match, group, project)
        val scores = mongoOperations.aggregate(aggregation, Answer::class.java, Score::class.java).mappedResults
        return Leaderboard(gameId = gameId, scores = scores)
    }

    /**
    db.answers.aggregate([
    { "$match" : { gameId : "5e932fb170416b4231a2fa43", roundId: "id45" }},
    { "$group" : { "_id" : "$playerId" , score: {$sum: { "$toDouble": "$score"}}}},
    { "$project": { "playerId": "$_id", "score":"$score"}}
    ])
     */
    fun getLeaderboard(gameId: String, roundId: String): Leaderboard {
        val match = Aggregation.match(Criteria.where("gameId").`is`(gameId).and("roundId").`is`(roundId))
        val group = Aggregation.group("\$playerId").sum("score").`as`("score")
        val project = Aggregation.project()
                .and("\$_id").`as`("playerId")
                .and("\$score").`as`("score")

        val aggregation = Aggregation.newAggregation(match, group, project)
        val scores = mongoOperations.aggregate(aggregation, Answer::class.java, Score::class.java).mappedResults
        return Leaderboard(gameId = gameId, roundId = roundId, scores = scores)
    }

    fun save(answer: Answer) {
        answerRepo.save(answer)
    }

    fun publishLeaderboard(gameId: String, roundId: String?) {
        // 1. Get the leaderboard
        val leaderboard = if (roundId == null) getLeaderboard(gameId)
        else getLeaderboard(gameId, roundId)

        // 2. Get the game
        val game = gameService.get(gameId)

        // 3. Publish the leaderboard
        currentContentService.save(
                publishService.publishContent(recipients = game.players.map { it.displayName },
                topic = "/game",
                content = leaderboard,
                gameId = gameId,
                contentType = PublishContentType.LEADERBOARD )
        )

    }

    fun publishAnswersForRound(gameId: String, roundId: String) {
        // 1. Get game
        val game = gameService.get(gameId)

        // 2. Get quiz
        val quiz = quizService.get(game.quizId)
        val round = quiz.rounds.first { it.id == roundId }

        // 3. Publish round
        currentContentService.save(
                publishService.publishContent(recipients = game.players.map { it.displayName },
                topic = "/game",
                content = round,
                gameId = gameId,
                contentType = PublishContentType.ROUND_SUMMARY )
        )
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

    fun getQuestionsAndAnswersForPlayer(gameId: String, playerId: String): List<QuestionAnswerWrapper> {
        val answers = getAnswersForPlayer(gameId = gameId, playerId = playerId)
        return answers.map {
            val question = gameService.getQuestion(it.quizId, it.roundId, it.questionId)
            QuestionAnswerWrapper(question, it)
        }
    }

    fun getAnswersForPlayer(gameId: String, playerId: String): List<Answer> {
        val answers = answerRepo.findByGameIdAndPlayerId(gameId = gameId, playerId = playerId)
        if(answers.isEmpty())
            return emptyList()
        return answers
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}