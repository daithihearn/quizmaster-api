package ie.daithi.quizmaster.config

import ie.daithi.quizmaster.web.security.HttpHandshakeInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig (
        @Value("#{'\${cors.whitelist}'.split(',')}")
        private val allowedOrigins: List<String>,
        @Value("\${security.jwt.secret}")
        private val securitySecret: String
): WebSocketMessageBrokerConfigurer {


    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic", "/queue")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {

        // TODO: Only supports one origin
        registry.addEndpoint("/websocket/")
                .setAllowedOrigins(allowedOrigins.first())
                .setHandshakeHandler(HttpHandshakeInterceptor(securitySecret))
                .withSockJS()
    }

}