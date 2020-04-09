package ie.daithi.quizmaster.config

import com.mongodb.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["ie.daithi.quizmaster"])
class MongoDbConfig (
        @Value("\${mongodb.hostname}")
        private val hostname: String,
        @Value("\${mongodb.port}")
        private val port: Int,
        @Value("\${mongodb.dbname}")
        private val dbname: String
): AbstractMongoConfiguration() {

    override fun getDatabaseName(): String {
        return dbname
    }

    override fun mongoClient(): MongoClient {
        return MongoClient(hostname, port)
    }

}