package com.distrPedidos.districommerce_pedidos.service;

import com.distrPedidos.districommerce_pedidos.config.RabbitMQConfig;
import com.distrPedidos.districommerce_pedidos.entity.Pagamento;
import com.distrPedidos.districommerce_pedidos.repository.PagamentoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAGAMENTO_PROCESSAR)
    public void processarPagamento(Pagamento pagamento) {
        // Simula processamento do pagamento (aprovado ou recusado)
        boolean aprovado = new Random().nextBoolean();

        if (aprovado) {
            pagamento.setStatus("APROVADO");
        } else {
            pagamento.setStatus("RECUSADO");
        }

        // Salva o resultado no MongoDB
        pagamentoRepository.save(pagamento);

        // Publica o resultado na fila de resultados
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_PAGAMENTO,
            RabbitMQConfig.QUEUE_PAGAMENTO_RESULTADO,
            pagamento
        );
    }

    public Pagamento buscarPorPedidoId(String pedidoId) {
        return pagamentoRepository.findByPedidoId(pedidoId);
    }
}
