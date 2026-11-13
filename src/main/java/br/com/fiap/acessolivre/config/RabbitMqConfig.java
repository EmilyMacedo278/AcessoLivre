package br.com.fiap.acessolivre.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {
    public static final String EXCHANGE = "acessolivre.eventos";
    public static final String FILA = "acessolivre.relatos.criados";
    public static final String ROUTING_KEY = "relato.criado";

    @Bean
    public DirectExchange eventosExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue relatosCriadosQueue() {
        return new Queue(FILA, true);
    }

    @Bean
    public Binding relatosCriadosBinding(Queue relatosCriadosQueue, DirectExchange eventosExchange) {
        return BindingBuilder.bind(relatosCriadosQueue)
                .to(eventosExchange)
                .with(ROUTING_KEY);
    }
}
