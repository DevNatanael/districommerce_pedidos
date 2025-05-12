package com.distrPedidos.districommerce_pedidos.controller;

import com.distrPedidos.districommerce_pedidos.config.RabbitMQConfig;
import com.distrPedidos.districommerce_pedidos.entity.Pagamento;
import com.distrPedidos.districommerce_pedidos.service.PagamentoService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/processar")
    public ResponseEntity<String> processarPagamento(@RequestBody Pagamento pagamento) {
        // Envia o pagamento para a fila de processamento
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_PAGAMENTO,
            RabbitMQConfig.QUEUE_PAGAMENTO_PROCESSAR,
            pagamento
        );
        return ResponseEntity.ok("Pagamento enviado para processamento");
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Pagamento> buscarPorPedidoId(@PathVariable String pedidoId) {
        Pagamento pagamento = pagamentoService.buscarPorPedidoId(pedidoId);
        if (pagamento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pagamento);
    }
}
