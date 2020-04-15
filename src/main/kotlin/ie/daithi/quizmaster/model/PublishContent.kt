package ie.daithi.quizmaster.model

import ie.daithi.quizmaster.web.model.enums.PublishContentType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="currentContent")
data class PublishContent(
        @Id
        var gameId: String? = null,
        var content: Any? = null,
        var type: PublishContentType? = null
)