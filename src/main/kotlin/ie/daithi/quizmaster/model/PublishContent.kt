package ie.daithi.quizmaster.model

import ie.daithi.quizmaster.web.model.enums.PublishContentType

data class PublishContent(
        val content: Any,
        val type: PublishContentType
)