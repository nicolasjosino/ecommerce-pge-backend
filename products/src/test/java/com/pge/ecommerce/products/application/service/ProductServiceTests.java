package com.pge.ecommerce.products.application.service;

import com.pge.ecommerce.products.application.dto.ProductRequest;
import com.pge.ecommerce.products.application.dto.ProductResponse;
import com.pge.ecommerce.products.domain.model.Product;
import com.pge.ecommerce.products.domain.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest request;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        request = new ProductRequest(
                "Notebook Dell Inspiron",
                "ELECTRONICS",
                new BigDecimal("3499.90")
        );

        mockProduct = Product.create(
                "Notebook Dell Inspiron",
                "ELECTRONICS",
                new BigDecimal("3499.90")
        );
    }

    @Test
    @DisplayName("create: deve retornar ProductResponse quando produto nao existe")
    void create_deveRetornarResponse_quandoProdutoNaoExiste() {
        when(productRepository.findByDescription("Notebook Dell Inspiron"))
                .thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        ProductResponse response = productService.create(request);

        assertThat(response.description()).isEqualTo("Notebook Dell Inspiron");
        assertThat(response.category()).isEqualTo("ELECTRONICS");
        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("3499.90"));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("create: deve lancar IllegalArgumentException quando descricao ja existe")
    void create_deveLancarExcecao_quandoDescricaoJaExiste() {
        when(productRepository.findByDescription("Notebook Dell Inspiron"))
                .thenReturn(Optional.of(mockProduct));

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Produto já existe");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("findAll: deve retornar lista de produtos")
    void findAll_deveRetornarListaDeProdutos() {
        Product outro = Product.create("Mouse Logitech", "ELECTRONICS", new BigDecimal("199.90"));
        when(productRepository.findAll()).thenReturn(List.of(mockProduct, outro));

        List<ProductResponse> result = productService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProductResponse::description)
                .containsExactlyInAnyOrder("Notebook Dell Inspiron", "Mouse Logitech");
    }

    @Test
    @DisplayName("findAll: deve retornar lista vazia quando nao ha produtos")
    void findAll_deveRetornarListaVazia_quandoNaoHaProdutos() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponse> result = productService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById: deve retornar produto quando id existe")
    void findById_deveRetornarProduto_quandoIdExiste() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        ProductResponse response = productService.findById(1L);

        assertThat(response.description()).isEqualTo("Notebook Dell Inspiron");
        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("3499.90"));
    }

    @Test
    @DisplayName("findById: deve lancar EntityNotFoundException quando id nao existe")
    void findById_deveLancarExcecao_quandoIdNaoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("update: deve retornar produto atualizado quando id existe")
    void update_deveRetornarProdutoAtualizado_quandoIdExiste() {
        ProductRequest updateRequest = new ProductRequest(
                "Notebook Dell XPS",
                "ELECTRONICS",
                new BigDecimal("5999.90")
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        ProductResponse response = productService.update(1L, updateRequest);

        assertThat(response.description()).isEqualTo("Notebook Dell XPS");
        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("5999.90"));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("update: deve lancar EntityNotFoundException quando id nao existe")
    void update_deveLancarExcecao_quandoIdNaoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete: deve deletar produto quando id existe")
    void delete_deveDeletarProduto_quandoIdExiste() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        productService.delete(1L);

        verify(productRepository).deleteById(mockProduct.getId());
    }

    @Test
    @DisplayName("delete: deve lancar EntityNotFoundException quando id nao existe")
    void delete_deveLancarExcecao_quandoIdNaoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete: nao deve chamar deleteById quando produto nao existe")
    void delete_naoDeveChamarDeleteById_quandoProdutoNaoExiste() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(1L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(productRepository, never()).deleteById(any());
    }
}