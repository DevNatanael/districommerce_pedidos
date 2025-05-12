package com.distrPedidos.districommerce_pedidos.repository;
import com.distrPedidos.districommerce_pedidos.entity.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findByUsuarioId(Long usuarioId);
}
