package com.distrPedidos.districommerce_pedidos.dto;

public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private Double preco;
    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
}

