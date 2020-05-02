package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.enumeration.GameStatus
import ie.daithi.quizmaster.model.Game
import ie.daithi.quizmaster.model.Player
import ie.daithi.quizmaster.model.Question
import ie.daithi.quizmaster.model.Quiz
import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.repositories.GameRepo
import ie.daithi.quizmaster.repositories.QuizRepo
import ie.daithi.quizmaster.validation.EmailValidator
import ie.daithi.quizmaster.web.exceptions.InvalidEmailException
import ie.daithi.quizmaster.web.exceptions.InvalidSatusException
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.web.model.PresentQuestion
import ie.daithi.quizmaster.web.model.QuestionPointer
import ie.daithi.quizmaster.web.model.enums.PublishContentType
import ie.daithi.quizmaster.web.security.model.AppUser
import ie.daithi.quizmaster.web.security.model.Authority
import org.apache.logging.log4j.LogManager
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.security.SecureRandom

@Service
class GameService(
        private val quizRepo: QuizRepo,
        private val gameRepo: GameRepo,
        private val emailValidator: EmailValidator,
        private val emailService: EmailService,
        private val appUserRepo: AppUserRepo,
        private val passwordEncoder: BCryptPasswordEncoder,
        private val mongoOperations: MongoOperations,
        private val publishService: PublishService,
        private val currentContentService: CurrentContentService
) {
    fun create(quizMasterId: String, name: String, playerEmails: List<String>, quizId: String): Game {
        logger.info("Attempting to start a game for quizId: $quizId")

        // 1. Check that quiz exists
        if (!quizRepo.existsById(quizId))
            throw NotFoundException("Quiz $quizId not found")

        // 2. Put all emails to lower case
        val lowerCaseEmails = playerEmails.map(String::toLowerCase)

        // 3. Validate Emails
        lowerCaseEmails.forEach {
            if (!emailValidator.isValid(it))
                throw InvalidEmailException("Invalid email $it")
        }

        // 4. Create Players and Issue emails
        val players = arrayListOf<Player>()
        lowerCaseEmails.forEach{
            players.add(createPlayer(it))
        }

        // 5. Create Game
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

        game.players = game.players.minus(Player(id = user.id!!, displayName = user.username!!))

        // 3. Save Game
        gameRepo.save(game)
    }

    fun addPlayer(gameId: String, playerEmail: String) {

        // 1. Validate email
        if (!emailValidator.isValid(playerEmail))
            throw InvalidEmailException("Invalid email $playerEmail")

        // 2. Get game
        val gameOpt = gameRepo.findById(gameId)
        if (!gameOpt.isPresent) throw NotFoundException("Game $gameId not found")
        val game = gameOpt.get()

        // 3. Create player
        val player = createPlayer(playerEmail)

        // 4. Add player
        game.players = game.players.plus(player)

        // 5. Save Game
        gameRepo.save(game)
    }

    fun createPlayer(username: String): Player {
        val passwordByte = ByteArray(16)
        secureRandom.nextBytes(passwordByte)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(passwordByte)
        val password = digest.fold("", { str, byt -> str + "%02x".format(byt) })

        val user = AppUser(username = username,
                password = passwordEncoder.encode(password),
                authorities = listOf(Authority.PLAYER))

        appUserRepo.deleteByUsernameIgnoreCase(username)
        appUserRepo.save(user)
        emailService.sendQuizInvite(username, password)

        return Player(id = user.id!!, displayName = user.username!!)
    }

    fun publishQuestion(pointer: QuestionPointer) {

        // 1. Get the game
        val game = gameRepo.findById(pointer.gameId)
        if (!game.isPresent)
            throw NotFoundException("Game ${pointer.gameId} not found")

        // 2. Get question
        val question = getQuestion(game.get().quizId, pointer.roundId, pointer.questionId)

        val presentQuestion = PresentQuestion(
                gameId = pointer.gameId,
                roundId = pointer.roundId,
                questionId = pointer.questionId,
                question = question.question,
                imageUri = question.imageUri,
                mediaUri = question.mediaUri)

        // 3. Publish content to all players
        currentContentService.save(
                publishService.publishContent(recipients = game.get().players.map { it.displayName },
                        topic = "/game",
                        content = presentQuestion,
                        gameId = pointer.gameId,
                        contentType = PublishContentType.QUESTION)
        )
    }

    /**
     *   db.quizzes.aggregate([
            { $match: {_id: ObjectId('5e91bedf70416b47e5db30db')}},
            { $unwind: "$rounds"},
            { $match: {"rounds._id": 0}},
            { $unwind: "$rounds.questions"},
            { $match: {"rounds.questions._id": 0}},
            { $group: { _id: { question: "$rounds.questions"  } }},
        ])
     */
    fun getQuestion(quizId: String, roundId: String, questionId: String): Question {
        val match1 = Aggregation.match(Criteria.where("id").`is`(quizId))
        val unwind1 = Aggregation.unwind("\$rounds")
        val match2 = Aggregation.match(Criteria.where("rounds._id").`is`(roundId))
        val unwind2 = Aggregation.unwind("\$rounds.questions")
        val match3 = Aggregation.match(Criteria.where("rounds.questions._id").`is`(questionId))
        val group = Aggregation.group("\$rounds.questions")
        val project = Aggregation.project()
                .and("\$_id.id").`as`("id")
                .and("\$_id.question").`as`("question")
                .and("\$_id.imageUri").`as`("imageUri")
                .and("\$_id.type").`as`("type")
                .and("\$_id.answer").`as`("answer")
                .and("\$_id.options").`as`("options")

        val aggregation = Aggregation.newAggregation(match1, unwind1, match2, unwind2, match3, group, project)
        return mongoOperations.aggregate(aggregation, Quiz::class.java, Question::class.java).uniqueMappedResult
                ?: throw NotFoundException("Can't find question $quizId -> $roundId -> $questionId")
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

    fun getActive(): List<Game> {
        return gameRepo.findAllByStatus(GameStatus.ACTIVE)
    }

    fun getByPlayerId(gameId: String): Game {
        return gameRepo.getByPlayerId(gameId)
    }

    fun finish(gameId: String) {
        val game = get(gameId)
        if( game.status == GameStatus.ACTIVE) throw InvalidSatusException("Can only finish a game that is in STARTED state not ${game.status}")
        game.status = GameStatus.COMPLETED
        gameRepo.save(game)
    }

    fun cancel(gameId: String) {
        val game = get(gameId)
        if( game.status == GameStatus.CANCELLED) throw InvalidSatusException("Game is already in CANCELLED state")
        game.status = GameStatus.CANCELLED
        gameRepo.save(game)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
        private val secureRandom = SecureRandom()
    }

}