package com.distrPedidos.districommerce_pedidos.repository;

import com.distrPedidos.districommerce_pedidos.entity.Logistica;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogisticaRepository extends MongoRepository<Logistica, String> {
    Logistica findByPedidoId(String pedidoId);
}
