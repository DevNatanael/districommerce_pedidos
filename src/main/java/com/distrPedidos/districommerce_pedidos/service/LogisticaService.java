package com.distrPedidos.districommerce_pedidos.service;

import com.distrPedidos.districommerce_pedidos.config.RabbitMQConfig;
import com.distrPedidos.districommerce_pedidos.entity.Logistica;
import com.distrPedidos.districommerce_pedidos.repository.LogisticaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogisticaService {

    @Autowired
    private LogisticaRepository logisticaRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_LOGISTICA_INICIAR)
    public void processarLogistica(Logistica logistica) throws InterruptedException {
        // Etapa 1: Separação do pedido
        logistica.setStatus("SEPARACAO");
        logisticaRepository.save(logistica);
        enviarAtualizacao(logistica);
        Thread.sleep(5000); // Simula 5 segundos de separação

        // Etapa 2: Envio do pedido
        logistica.setStatus("ENVIADO");
        logisticaRepository.save(logistica);
        enviarAtualizacao(logistica);
        Thread.sleep(10000); // Simula 10 segundos de envio

        // Etapa 3: Entrega do pedido
        logistica.setStatus("ENTREGUE");
        logisticaRepository.save(logistica);
        enviarAtualizacao(logistica);
    }

    private void enviarAtualizacao(Logistica logistica) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_LOGISTICA,
            RabbitMQConfig.QUEUE_LOGISTICA_ATUALIZACAO,
            logistica
        );
    }

    public Logistica buscarPorPedidoId(String pedidoId) {
        return logisticaRepository.findByPedidoId(pedidoId);
    }
}
