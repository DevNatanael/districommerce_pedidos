package com.distrPedidos.districommerce_pedidos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_PAGAMENTO_PROCESSAR = "pagamento.processar";
    public static final String QUEUE_PAGAMENTO_RESULTADO = "pagamento.resultado";
    public static final String EXCHANGE_PAGAMENTO = "pagamento.exchange";

    public static final String QUEUE_LOGISTICA_INICIAR = "logistica.iniciar";
    public static final String QUEUE_LOGISTICA_ATUALIZACAO = "logistica.atualizacao";
    public static final String EXCHANGE_LOGISTICA = "logistica.exchange";

    @Bean
    public Queue queuePagamentoProcessar() {
        return new Queue(QUEUE_PAGAMENTO_PROCESSAR);
    }

    @Bean
    public Queue queuePagamentoResultado() {
        return new Queue(QUEUE_PAGAMENTO_RESULTADO);
    }

    @Bean
    public Queue queueLogisticaIniciar() {
        return new Queue(QUEUE_LOGISTICA_INICIAR);
    }

    @Bean
    public Queue queueLogisticaAtualizacao() {
        return new Queue(QUEUE_LOGISTICA_ATUALIZACAO);
    }

    @Bean
    public DirectExchange exchangePagamento() {
        return new DirectExchange(EXCHANGE_PAGAMENTO);
    }

    @Bean
    public DirectExchange exchangeLogistica() {
        return new DirectExchange(EXCHANGE_LOGISTICA);
    }

    @Bean
    public Binding bindingProcessar(Queue queuePagamentoProcessar, DirectExchange exchangePagamento) {
        return BindingBuilder.bind(queuePagamentoProcessar).to(exchangePagamento).with(QUEUE_PAGAMENTO_PROCESSAR);
    }

    @Bean
    public Binding bindingResultado(Queue queuePagamentoResultado, DirectExchange exchangePagamento) {
        return BindingBuilder.bind(queuePagamentoResultado).to(exchangePagamento).with(QUEUE_PAGAMENTO_RESULTADO);
    }

    @Bean
    public Binding bindingLogisticaIniciar(Queue queueLogisticaIniciar, DirectExchange exchangeLogistica) {
        return BindingBuilder.bind(queueLogisticaIniciar).to(exchangeLogistica).with(QUEUE_LOGISTICA_INICIAR);
    }

    @Bean
    public Binding bindingLogisticaAtualizacao(Queue queueLogisticaAtualizacao, DirectExchange exchangeLogistica) {
        return BindingBuilder.bind(queueLogisticaAtualizacao).to(exchangeLogistica).with(QUEUE_LOGISTICA_ATUALIZACAO);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
