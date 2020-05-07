package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.PublishContent
import org.springframework.data.repository.CrudRepository

interface CurrentContentRepo: CrudRepository<PublishContent, String> {
}