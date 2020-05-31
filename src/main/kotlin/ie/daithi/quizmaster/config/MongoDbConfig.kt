package ie.daithi.quizmaster.config

import ie.daithi.quizmaster.model.Answer
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import javax.annotation.PostConstruct

@Configuration
@DependsOn("mongoTemplate")
@EnableMongoRepositories(basePackages = ["ie.daithi.quizmaster"])
class MongoDbConfig(
        private val mongoTemplate: MongoTemplate
) {

    @PostConstruct
    fun initIndices() {

        mongoTemplate.indexOps(Answer::class.java).ensureIndex(Index().on("playerId", Sort.Direction.ASC)
                .on("gameId", Sort.Direction.ASC)
                .on("roundId", Sort.Direction.ASC)
                .on("questionId", Sort.Direction.ASC)
                .unique().sparse())
    }
}