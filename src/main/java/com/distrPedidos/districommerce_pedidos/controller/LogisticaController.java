package com.distrPedidos.districommerce_pedidos.controller;

import com.distrPedidos.districommerce_pedidos.config.RabbitMQConfig;
import com.distrPedidos.districommerce_pedidos.entity.Logistica;
import com.distrPedidos.districommerce_pedidos.service.LogisticaService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logistica")
public class LogisticaController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private LogisticaService logisticaService;

    @PostMapping("/iniciar")
    public ResponseEntity<String> iniciarLogistica(@RequestBody Logistica logistica) {
        // Envia a logística para a fila de processamento
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_LOGISTICA,
            RabbitMQConfig.QUEUE_LOGISTICA_INICIAR,
            logistica
        );
        return ResponseEntity.ok("Logística iniciada");
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Logistica> buscarPorPedidoId(@PathVariable String pedidoId) {
        Logistica logistica = logisticaService.buscarPorPedidoId(pedidoId);
        if (logistica == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(logistica);
    }
}
