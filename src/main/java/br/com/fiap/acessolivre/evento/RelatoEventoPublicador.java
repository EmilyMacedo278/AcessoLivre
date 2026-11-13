package br.com.fiap.acessolivre.evento;

import br.com.fiap.acessolivre.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class RelatoEventoPublicador {
    private final RabbitTemplate rabbitTemplate;
    private final JsonMapper jsonMapper;

    // Só envia ao RabbitMQ depois que o registro do relato foi confirmado no MySQL.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publicar(RelatoCriadoEvento evento) {
        try {
            String json = jsonMapper.writeValueAsString(evento);
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, json);
            log.info("RABBITMQ PUBLICADO: relato={}, recurso={}", evento.relatoId(), evento.recursoId());
        } catch (Exception ex) {
            // Uma falha de mensageria não desfaz a transação já confirmada do relato.
            // Para produção, adicionar outbox e retentativas persistentes.
            log.error("Falha ao publicar evento do relato {} no RabbitMQ", evento.relatoId(), ex);
        }
    }
}
