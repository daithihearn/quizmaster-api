package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.PublishContent
import ie.daithi.quizmaster.repositories.CurrentContentRepo
import org.apache.logging.log4j.LogManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CurrentContentService(
        private val currentContentRepo: CurrentContentRepo
) {

    fun get(gameId: String): PublishContent? {
        return currentContentRepo.findByIdOrNull(gameId)
    }

    fun save(content: PublishContent) {
        currentContentRepo.save(content)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}