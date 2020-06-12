package ie.daithi.quizmaster.service

import com.fasterxml.jackson.databind.ObjectMapper
import ie.daithi.quizmaster.model.Game
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage

@Service
class PublishService(
        private val messageSender: SimpMessagingTemplate,
        private val objectMapper: ObjectMapper
) {

    fun publishGame(game: Game, recipients: List<String>) {
        val wsMessage = TextMessage(objectMapper.writeValueAsString(game))
        recipients.forEach { recipient ->
            messageSender.convertAndSendToUser(recipient+game.id, "/game", wsMessage)
        }
    }

    fun publishAnsweredTopic(content: Any, gameId: String, recipients: List<String>) {
        val wsMessage = TextMessage(objectMapper.writeValueAsString(content))
        recipients.forEach { recipient ->
            messageSender.convertAndSendToUser(recipient + gameId, "/answered", wsMessage)
        }
    }

    fun publishToUnscoredTopic(content: Any, gameId: String, recipients: List<String>) {
        val wsMessage = TextMessage(objectMapper.writeValueAsString(content))
        recipients.forEach { recipient ->
            messageSender.convertAndSendToUser(recipient + gameId, "/unscored", wsMessage)
        }
    }

}