package com.distrPedidos.districommerce_pedidos.controller;

import com.distrPedidos.districommerce_pedidos.dto.PedidoDTO;
import com.distrPedidos.districommerce_pedidos.entity.Pedido;
import com.distrPedidos.districommerce_pedidos.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoService.criarPedido(pedidoDTO);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable String id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> buscarPorUsuario(@PathVariable Long usuarioId) {
        List<Pedido> pedidos = pedidoService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }
}
