package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.model.Game
import ie.daithi.quizmaster.model.PublishContent
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
import java.time.LocalDateTime

@Service
class AnswerService(
        private val answerRepo: AnswerRepo,
        private val quizService: QuizService,
        private val scoringService: ScoringService,
        private val mongoOperations: MongoOperations,
        private val publishService: PublishService
) {

    fun submitAnswer(playerId: String, game: Game, roundId: String, questionId: String, answer: String, multipleChoice: Boolean) {

        // 1. Check if they already submitted an answer
        if (answerRepo.existsByGameIdAndPlayerIdAndRoundIdAndQuestionId(
                        gameId = game.id!!,
                        playerId = playerId,
                        roundId = roundId,
                        questionId = questionId)) throw AnswerResubmissionException("You have already submitted and answer for this question")

        // 2. Attempt to correct
        val answerObj = Answer(playerId = playerId, quizId = game.quizId, gameId = game.id!!,
                roundId = roundId, questionId = questionId,
                answer = answer, timestamp = LocalDateTime.now())

        val question = quizService.getQuestion(game.quizId, roundId, questionId)
        if (multipleChoice || !question.forceManualCorrection)
            scoringService.attemptScore(question.answer, answerObj, question.points, multipleChoice)

        // 3. Store answer
        answerRepo.save(answerObj)

        // 4. Publish the remaining unscored questions
        val unscored = getUnscoredAnswers(gameId = game.id!!)
        publishService.publishToUnscoredTopic(content = unscored, gameId = game.id!!, recipients = listOf(game.quizMasterId))

        // 5. Publish answer event
        val answered = getHasAnswered(gameId = game.id!!, roundId = roundId, questionId = questionId)
        val recipients = emptyList<String>().plus(game.players).plus(game.quizMasterId)
        publishService.publishAnsweredTopic(content = answered, gameId = game.id!!, recipients = recipients)
    }

    fun getUnscoredAnswers(gameId: String): List<QuestionAnswerWrapper> {
        val answers = answerRepo.findByGameIdAndScore(gameId = gameId, score = null)
        if(answers.isEmpty())
            return emptyList()
        return answers.map {
            val question = quizService.getQuestion(it.quizId, it.roundId, it.questionId)
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

    fun hasAnswered(gameId: String, playerId: String, roundId: String, questionId: String): Boolean {
        return answerRepo.existsByGameIdAndPlayerIdAndRoundIdAndQuestionId(gameId, playerId, roundId, questionId)
    }

    fun getAnswers(gameId: String): List<QuestionAnswerWrapper> {
        val answers = answerRepo.findByGameId(gameId = gameId)
        if(answers.isEmpty())
            return emptyList()
        return answers.map { answer ->
            val question = quizService.getQuestion(answer.quizId, answer.roundId, answer.questionId)
            QuestionAnswerWrapper(question, answer)
        }
    }

    fun getAnswers(gameId: String, roundId: String): List<QuestionAnswerWrapper> {
        val answers = answerRepo.findByGameIdAndRoundId(gameId = gameId, roundId = roundId )
        if(answers.isEmpty())
            return emptyList()
        return answers.map { answer ->
            val question = quizService.getQuestion(answer.quizId, answer.roundId, answer.questionId)
            QuestionAnswerWrapper(question, answer)
        }
    }

    fun getAnswers(gameId: String, roundId: String, playerId: String): List<QuestionAnswerWrapper> {
        val answers = answerRepo.findByGameIdAndRoundIdAndPlayerId(gameId = gameId, roundId = roundId, playerId = playerId )
        if(answers.isEmpty())
            return emptyList()
        return answers.map { answer ->
            val question = quizService.getQuestion(answer.quizId, answer.roundId, answer.questionId)
            QuestionAnswerWrapper(question, answer)
        }
    }

    fun getQuestionsAndAnswersForPlayer(gameId: String, playerId: String): List<QuestionAnswerWrapper> {
        val answers = getAnswersForPlayer(gameId = gameId, playerId = playerId)
        return answers.map { answer ->
            val question = quizService.getQuestion(answer.quizId, answer.roundId, answer.questionId)
            QuestionAnswerWrapper(question, answer)
        }
    }

    fun getAnswersForPlayer(gameId: String, playerId: String): List<Answer> {
        val answers = answerRepo.findByGameIdAndPlayerId(gameId = gameId, playerId = playerId)
        if(answers.isEmpty())
            return emptyList()
        return answers
    }

    fun getHasAnswered(gameId: String, roundId: String, questionId: String): List<String> {
        val answered = answerRepo.findByGameIdAndRoundIdAndQuestionId(gameId = gameId, roundId = roundId, questionId = questionId)
        return answered.map { answer -> answer.playerId }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}