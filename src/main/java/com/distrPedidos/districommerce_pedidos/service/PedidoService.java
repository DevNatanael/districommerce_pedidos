package com.distrPedidos.districommerce_pedidos.service;

import com.distrPedidos.districommerce_pedidos.dto.PedidoDTO;
import com.distrPedidos.districommerce_pedidos.dto.ProdutoResponseDTO;
import com.distrPedidos.districommerce_pedidos.dto.UsuarioResponseDTO;
import com.distrPedidos.districommerce_pedidos.entity.Pedido;
import com.distrPedidos.districommerce_pedidos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Atualize as URLs conforme sua configuração (a nova porta para produtos é 8082)
    private final String USUARIO_SERVICE_URL = "http://localhost:8080/api/usuarios/";
    private final String PRODUTO_SERVICE_URL = "http://localhost:8082/api/produtos/";

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

        // Para cada item, valida o produto, calcula total e atualiza estoque
        for (PedidoDTO.ItemPedidoDTO itemDTO : dto.getItens()) {
            ProdutoResponseDTO produto = restTemplate.getForObject(PRODUTO_SERVICE_URL + itemDTO.getProdutoId(), ProdutoResponseDTO.class);
            if (produto == null) {
                throw new RuntimeException("Produto não encontrado: " + itemDTO.getProdutoId());
            }

            // Atualiza o estoque: novoEstoque = estoque atual - quantidade pedida
            int novoEstoque = produto.getEstoque() - itemDTO.getQuantidade();
            if(novoEstoque < 0) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getId());
            }
            String putUrl = PRODUTO_SERVICE_URL + produto.getId() + "/estoque";
            Map<String, Integer> estoqueBody = new HashMap<>();
            estoqueBody.put("estoque", novoEstoque);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(estoqueBody, headers);
            restTemplate.put(putUrl, entity);

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
