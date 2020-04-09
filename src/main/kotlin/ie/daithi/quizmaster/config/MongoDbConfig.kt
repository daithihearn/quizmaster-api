package ie.daithi.quizmaster.config

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["ie.daithi.quizmaster"])
class MongoDbConfig (
        @Value("\${mongodb.uri}")
        private val mongoDbUri: String,
        @Value("\${mongodb.dbname}")
        private val dbname: String
): AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String {
        return dbname
    }

    override fun mongoClient(): MongoClient {
        return MongoClients.create(mongoDbUri)
    }

}