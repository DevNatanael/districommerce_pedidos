package com.distrPedidos.districommerce_pedidos.service;

import com.distrPedidos.districommerce_pedidos.dto.PedidoDTO;
import com.distrPedidos.districommerce_pedidos.dto.ProdutoResponseDTO;
import com.distrPedidos.districommerce_pedidos.dto.UsuarioResponseDTO;
import com.distrPedidos.districommerce_pedidos.entity.Pedido;
import com.distrPedidos.districommerce_pedidos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;

@Configuration
class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String USUARIO_SERVICE_URL = "http://localhost:8080/api/usuarios/";
    private final String PRODUTO_SERVICE_URL = "http://localhost:8080/api/produtos/";

    public Pedido criarPedido(PedidoDTO dto) {
        // Validar usuário via HTTP
        UsuarioResponseDTO usuario = restTemplate.getForObject(USUARIO_SERVICE_URL + dto.getUsuarioId(), UsuarioResponseDTO.class);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(dto.getUsuarioId());

        List<Pedido.ItemPedido> itens = new ArrayList<>();
        double total = 0.0;

        // Validar produtos via HTTP e calcular total
        for (PedidoDTO.ItemPedidoDTO itemDTO : dto.getItens()) {
            ProdutoResponseDTO produto = restTemplate.getForObject(PRODUTO_SERVICE_URL + itemDTO.getProdutoId(), ProdutoResponseDTO.class);
            if (produto == null) {
                throw new RuntimeException("Produto não encontrado: " + itemDTO.getProdutoId());
            }

            Pedido.ItemPedido item = new Pedido.ItemPedido();
            item.setProdutoId(produto.getId());
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            itens.add(item);
            total += produto.getPreco() * itemDTO.getQuantidade();
        }

        pedido.setItens(itens);
        pedido.setTotal(total);

        return pedidoRepository.save(pedido);
    }

    public Pedido buscarPorId(String id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    public List<Pedido> buscarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }
}