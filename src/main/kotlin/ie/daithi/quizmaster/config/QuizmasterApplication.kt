package ie.daithi.quizmaster.config

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class QuizmasterApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(QuizmasterApplication::class.java, *args)
        }
    }
}