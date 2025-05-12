package com.distrPedidos.districommerce_pedidos.repository;

import com.distrPedidos.districommerce_pedidos.entity.Pagamento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PagamentoRepository extends MongoRepository<Pagamento, String> {
    Pagamento findByPedidoId(String pedidoId);
}
