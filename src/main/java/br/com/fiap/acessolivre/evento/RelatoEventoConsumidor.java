package br.com.fiap.acessolivre.evento;

import br.com.fiap.acessolivre.config.RabbitMqConfig;
import br.com.fiap.acessolivre.entity.EventoRelatoProcessado;
import br.com.fiap.acessolivre.repository.EventoRelatoProcessadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class RelatoEventoConsumidor {
    private final EventoRelatoProcessadoRepository repository;
    private final JsonMapper jsonMapper;

    // O container do listener consome mensagens em uma thread separada da requisição HTTP.
    @RabbitListener(queues = RabbitMqConfig.FILA)
    @Transactional
    public void consumir(String mensagemJson) {
        RelatoCriadoEvento evento = jsonMapper.readValue(mensagemJson, RelatoCriadoEvento.class);

        if (repository.existsByRelatoId(evento.relatoId())) {
            log.info("RABBITMQ DUPLICADO IGNORADO: relato={}", evento.relatoId());
            return;
        }

        repository.save(new EventoRelatoProcessado(
                evento.relatoId(), evento.recursoId(), evento.condicao()));

        log.info("RABBITMQ CONSUMIDO: relato={}, recurso={}, condicao={}",
                evento.relatoId(), evento.recursoId(), evento.condicao());

        if (!"FUNCIONANDO".equals(evento.condicao())) {
            log.warn("ATENCAO OPERACIONAL: recurso={} recebeu relato de {}",
                    evento.recursoId(), evento.condicao());
        }
    }
}
