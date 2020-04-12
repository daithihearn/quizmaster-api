package ie.daithi.quizmaster.service

import com.fasterxml.jackson.databind.ObjectMapper
import ie.daithi.quizmaster.model.Game
import ie.daithi.quizmaster.model.Player
import ie.daithi.quizmaster.model.Question
import ie.daithi.quizmaster.model.Quiz
import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.repositories.GameRepo
import ie.daithi.quizmaster.repositories.QuizRepo
import ie.daithi.quizmaster.validation.EmailValidator
import ie.daithi.quizmaster.web.exceptions.InvalidEmailException
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.web.model.PresentQuestion
import ie.daithi.quizmaster.web.model.QuestionPointer
import ie.daithi.quizmaster.web.security.model.AppUser
import ie.daithi.quizmaster.web.security.model.Authority
import org.apache.logging.log4j.LogManager
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import java.security.SecureRandom
import java.util.*

@Service
class GameService(
        private val quizRepo: QuizRepo,
        private val gameRepo: GameRepo,
        private val emailValidator: EmailValidator,
        private val emailService: EmailService,
        private val appUserRepo: AppUserRepo,
        private val passwordEncoder: BCryptPasswordEncoder,
        private val messageSender: SimpMessagingTemplate,
        private val mongoOperations: MongoOperations,
        private val objectMapper: ObjectMapper
) {
    fun create(quizMasterId: String, playerEmails: List<String>, quizId: String): Game {
        logger.info("Attempting to start a quiz $quizId")

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
        val users = arrayListOf<AppUser>()
        lowerCaseEmails.forEach{
            val passwordByte = ByteArray(16)
            secureRandom.nextBytes(passwordByte)
            val password = Base64.getEncoder().encodeToString(passwordByte)

            val user = AppUser(username = it,
                    password = passwordEncoder.encode(password),
                    authorities = listOf(Authority.PLAYER))

            val existingUser = appUserRepo.findByUsernameIgnoreCase(it)
            if (existingUser != null)
                user.id = existingUser.id
            appUserRepo.save(user)
            users.add(user)
            emailService.sendQuizInvite(it, password)
        }

        // 5. Create Game
        val game = Game(quizId = quizId, quizMasterId = quizMasterId)
        game.players = users.map { Player(id = it.username) }
        gameRepo.save(game)

        logger.info("Quiz started successfully $quizId")
        return game
    }

    fun publishQuestion(pointer: QuestionPointer) {

        // 1. Get the game
        val game = gameRepo.findById(pointer.gameId)
        if (!game.isPresent)
            throw NotFoundException("Game ${pointer.gameId} not found")

        // 2. Get question
        val question = getQuestion(game.get().quizId!!, pointer.roundIndex, pointer.questionIndex)
                ?: throw NotFoundException("Question not found ${pointer.gameId} -> ${pointer.roundIndex} -> ${pointer.questionIndex}")

        val presentQuestion = question.value?.let {
            PresentQuestion(
                    gameId = pointer.gameId,
                    roundIndex = pointer.roundIndex,
                    questionIndex = pointer.questionIndex,
                    question = it)
        }

        // 3. Publish content to all players
        val wsMessage = TextMessage(objectMapper.writeValueAsString(presentQuestion))
        game.get().players.forEach {
            messageSender.convertAndSendToUser(it.id!!, "/game", wsMessage)
        }
    }

    /**
     *   db.quizzes.aggregate([
            { $match: {_id: ObjectId('5e91bedf70416b47e5db30db')}},
            { $unwind: "$rounds"},
            { $match: {"rounds.index": 0}},
            { $unwind: "$rounds.questions"},
            { $match: {"rounds.questions.index": 0}},
            { $group: { _id: { question: "$rounds.questions"  } }},
        ])
     */
    fun getQuestion(quizId: String, roundIndex: Int, questionIndex: Int): Question? {
        val match1 = Aggregation.match(Criteria.where("id").`is`(quizId))
        val unwind1 = Aggregation.unwind("\$rounds")
        val match2 = Aggregation.match(Criteria.where("rounds.index").`is`(roundIndex))
        val unwind2 = Aggregation.unwind("\$rounds.questions")
        val match3 = Aggregation.match(Criteria.where("rounds.questions.index").`is`(questionIndex))
        val group = Aggregation.group("\$rounds.questions")
        val project = Aggregation.project()
                .and("\$_id.index").`as`("index")
                .and("\$_id.value").`as`("value")
                .and("\$_id.imageUri").`as`("imageUri")
                .and("\$_id.type").`as`("type")
                .and("\$_id.answer").`as`("answer")
                .and("\$_id.options").`as`("options")

        val aggregation = Aggregation.newAggregation(match1, unwind1, match2, unwind2, match3, group, project)
        return mongoOperations.aggregate(aggregation, Quiz::class.java, Question::class.java).uniqueMappedResult
    }

    fun get(id: String): Game {
        val game = gameRepo.findById(id)
        if (!game.isPresent)
            throw NotFoundException("Game $id not found")
        return game.get()
    }

    fun delete(id: String) {
        gameRepo.deleteById(id)
    }

    fun getAll(): List<Game> {
        return gameRepo.findAll()
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
        private val secureRandom = SecureRandom()
    }

}