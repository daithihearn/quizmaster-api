package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.enumeration.GameStatus
import ie.daithi.quizmaster.model.Game
import ie.daithi.quizmaster.model.PublishContent
import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.repositories.GameRepo
import ie.daithi.quizmaster.validation.EmailValidator
import ie.daithi.quizmaster.web.exceptions.InvalidStatusException
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.web.model.PresentQuestion
import ie.daithi.quizmaster.web.model.QuestionPointer
import ie.daithi.quizmaster.web.model.enums.PublishContentType
import org.apache.logging.log4j.LogManager
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class GameService(
        private val gameRepo: GameRepo,
        private val appUserRepo: AppUserRepo,
        private val quizService: QuizService,
        private val answerService: AnswerService,
        private val publishService: PublishService
) {
    fun create(quizMasterId: String, name: String, players: List<String>, quizId: String): Game {
        logger.info("Attempting to start a game for quizId: $quizId")

        // 1. Check that quiz exists
        if (!quizService.exists(quizId))
            throw NotFoundException("Quiz $quizId not found")

        // 2. Create Game
        val game = Game(quizId = quizId,
                quizMasterId = quizMasterId,
                status = GameStatus.ACTIVE,
                name = name,
                players = players)

        gameRepo.save(game)

        logger.info("Game started successfully ${game.id}")
        return game
    }

    fun removePlayer(gameId: String, playerId: String) {
        // 1. Get game
        val gameOpt = gameRepo.findById(gameId)
        if (!gameOpt.isPresent) throw NotFoundException("Game $gameId not found")
        val game = gameOpt.get()

        // 2. Remove Player
        val userOpt = appUserRepo.findById(playerId)
        if (!userOpt.isPresent) throw NotFoundException("User $playerId not found")
        val user = userOpt.get()

        game.players = game.players.minus(user.id)

        // 3. Save Game
        gameRepo.save(game)
    }

    fun addPlayer(gameId: String, playerId: String) {

        // 1. Get game
        val gameOpt = gameRepo.findById(gameId)
        if (!gameOpt.isPresent) throw NotFoundException("Game $gameId not found")
        val game = gameOpt.get()

        // 2. Add player
        game.players = game.players.plus(playerId)

        // 3. Save Game
        gameRepo.save(game)
    }

    fun publishQuestion(pointer: QuestionPointer) {

        // 1. Get the game
        val gameOpt = gameRepo.findById(pointer.gameId)
        if (!gameOpt.isPresent)
            throw NotFoundException("Game ${pointer.gameId} not found")
        val game = gameOpt.get()

        // 2. Get question
        val question = quizService.getQuestion(game.quizId, pointer.roundId, pointer.questionId)

        val presentQuestion = PresentQuestion(
                gameId = pointer.gameId,
                roundId = pointer.roundId,
                questionId = pointer.questionId,
                question = question.question,
                imageUri = question.imageUri,
                audioUri = question.audioUri,
                videoUri = question.videoUri)

        // 3. Set question as published
        game.publishedQuestions = game.publishedQuestions.plus(question.id)

        // 4. Set Current content
        val content = PublishContent(type = PublishContentType.QUESTION, content = presentQuestion)
        game.currentContent = content

        gameRepo.save(game)

        // 5. Publish content to all players
        publishService.publishGame(game = game, recipients = game.players)
    }

    fun publishLeaderboard(gameId: String, roundId: String?) {
        // 1. Get the leaderboard
        val leaderboard = if (roundId == null) answerService.getLeaderboard(gameId)
        else answerService.getLeaderboard(gameId, roundId)

        // 2. Get the game
        val game = get(gameId)

        // 3. Set Current content
        val content = PublishContent(type = PublishContentType.LEADERBOARD, content = leaderboard)
        game.currentContent = content

        gameRepo.save(game)

        // 4. Publish the leaderboard
        publishService.publishGame(game = game, recipients = game.players)

    }

    fun publishAnswersForRound(gameId: String, roundId: String) {
        // 1. Get game
        val game = get(gameId)

        // 2. Get quiz
        val quiz = quizService.get(game.quizId)
        val round = quiz.rounds.first { it.id == roundId }

        // 3. Set Current content
        val content = PublishContent(type = PublishContentType.ROUND_SUMMARY, content = round)
        game.currentContent = content

        gameRepo.save(game)

        // 4. Publish the leaderboard
        publishService.publishGame(game = game, recipients = game.players)

    }

    fun get(gameId: String): Game {
        val game = gameRepo.findById(gameId)
        if (!game.isPresent)
            throw NotFoundException("Game $gameId not found")
        return game.get()
    }

    fun delete(gameId: String) {
        gameRepo.deleteById(gameId)
    }

    fun getAll(): List<Game> {
        return gameRepo.findAll()
    }

    fun getActiveGamesForQuizmaster(quizMasterId: String): List<Game> {
        return gameRepo.findAllByQuizMasterIdAndStatus(quizMasterId, GameStatus.ACTIVE)
    }

    fun finish(gameId: String) {
        val game = get(gameId)
        if( game.status == GameStatus.CANCELLED) throw InvalidStatusException("Game has been cancelled")
        else if (game.status == GameStatus.COMPLETED) throw InvalidStatusException("Game is already completed")
        game.status = GameStatus.COMPLETED
        gameRepo.save(game)
    }

    fun cancel(gameId: String) {
        val game = get(gameId)
        if( game.status == GameStatus.CANCELLED) throw InvalidStatusException("Game is already in CANCELLED state")
        game.status = GameStatus.CANCELLED
        gameRepo.save(game)
    }

    fun getMyActive(playerId: String): List<Game> {
        return gameRepo.findByPlayerIdAndStatus(playerId, GameStatus.ACTIVE)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
        private val secureRandom = SecureRandom()
    }

}