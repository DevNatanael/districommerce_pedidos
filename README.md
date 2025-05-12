# DistriCommerce - Serviço de Pedidos

Este é o serviço de pedidos do DistriCommerce, responsável por gerenciar pedidos, pagamentos e logística.

## Requisitos

- Java 21
- MongoDB
- RabbitMQ
- Docker e Docker Compose (opcional)

## Configuração do Ambiente

1. Clone o repositório
2. Configure o MongoDB e RabbitMQ usando Docker Compose:
```bash
docker-compose up -d
```

## Endpoints da API

### Pedidos

#### Criar Pedido
- **Método**: POST
- **URL**: `http://localhost:8081/api/pedidos`
- **Body**:
```json
{
  "usuarioId": 123,
  "itens": [
    {
      "produtoId": 456,
      "quantidade": 2
    }
  ]
}
```

#### Buscar Pedido por ID
- **Método**: GET
- **URL**: `http://localhost:8081/api/pedidos/{id}`

#### Buscar Pedidos por Usuário
- **Método**: GET
- **URL**: `http://localhost:8081/api/pedidos/usuario/{usuarioId}`

### Pagamentos

#### Processar Pagamento
- **Método**: POST
- **URL**: `http://localhost:8081/api/pagamentos/processar`
- **Body**:
```json
{
  "pedidoId": "68193bbd5f61ab4eaf5dffbe",
  "valor": 100,
  "emailUsuario": "usuario@exemplo.com",
  "descricaoPedido": "Pedido de 3 produtos eletrônicos",
  "enderecoUsuario": "Rua Exemplo, 123 - Bairro - Cidade/UF"
}
```

#### Consultar Status do Pagamento
- **Método**: GET
- **URL**: `http://localhost:8081/api/pagamentos/pedido/{pedidoId}`

### Logística

#### Iniciar Processo de Logística
- **Método**: POST
- **URL**: `http://localhost:8081/api/logistica/iniciar`
- **Body**:
```json
{
  "pedidoId": "68193bbd5f61ab4eaf5dffbe",
  "enderecoEntrega": "Rua Exemplo, 123 - Bairro - Cidade/UF",
  "emailUsuario": "usuario@exemplo.com",
  "descricaoPedido": "Pedido de 3 produtos eletrônicos",
  "precoPedido": 150.00
}
```

#### Consultar Status da Logística
- **Método**: GET
- **URL**: `http://localhost:8081/api/logistica/pedido/{pedidoId}`

## Fluxo de Processamento

### Fluxo de Pagamento
1. Envie uma requisição POST para `/api/pagamentos/processar`
2. O sistema enviará o pagamento para a fila `pagamento.processar`
3. O serviço processará o pagamento (simulado com aprovação/recusa aleatória)
4. O resultado será salvo no MongoDB
5. Uma atualização será publicada na fila `pagamento.resultado`
6. Consulte o status usando GET em `/api/pagamentos/pedido/{pedidoId}`

### Fluxo de Logística
1. Envie uma requisição POST para `/api/logistica/iniciar`
2. O sistema enviará a logística para a fila `logistica.iniciar`
3. O serviço processará as etapas:
   - Separação (5 segundos)
   - Envio (10 segundos)
   - Entrega
4. Cada atualização será salva no MongoDB
5. Atualizações serão publicadas na fila `logistica.atualizacao`
6. Consulte o status usando GET em `/api/logistica/pedido/{pedidoId}`

## Monitoramento

### RabbitMQ Management Console
- **URL**: http://localhost:15673
- **Usuário**: guest
- **Senha**: guest

No console, você pode:
- Monitorar as filas:
  - `pagamento.processar`
  - `pagamento.resultado`
  - `logistica.iniciar`
  - `logistica.atualizacao`
- Ver mensagens sendo processadas
- Verificar status das filas

### MongoDB
- **URL**: mongodb://localhost:27017
- **Database**: districommerce
- **Collections**:
  - pedidos
  - pagamentos
  - logisticas

## Testando com Insomnia

1. **Criar um Pedido**:
   - POST `http://localhost:8081/api/pedidos`
   - Salve o ID do pedido retornado

2. **Processar Pagamento**:
   - POST `http://localhost:8081/api/pagamentos/processar`
   - Use o ID do pedido criado
   - Verifique o status com GET `http://localhost:8081/api/pagamentos/pedido/{pedidoId}`

3. **Iniciar Logística**:
   - POST `http://localhost:8081/api/logistica/iniciar`
   - Use o ID do pedido criado
   - Verifique o status com GET `http://localhost:8081/api/logistica/pedido/{pedidoId}`

## Observações

- O serviço de pagamento simula aprovação/recusa aleatoriamente
- O serviço de logística simula delays para demonstrar o processo assíncrono
- Todas as atualizações são persistidas no MongoDB
- As filas do RabbitMQ garantem o processamento assíncrono e a comunicação entre serviços
