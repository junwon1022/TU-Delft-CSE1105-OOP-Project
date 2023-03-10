package server.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * add a web socket STOMP endpoint for the specified http path
     * @param reg the stomp endpoint that will be used for registration
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry reg){
        reg.addEndpoint("/websocket");
    }

    /**
     * Configures the message broker for the web socket communication
     * @param config the MessageBrokerRegistry that will be used for configuration
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
}
