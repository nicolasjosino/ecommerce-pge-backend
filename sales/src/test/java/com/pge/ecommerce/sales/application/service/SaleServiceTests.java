package com.pge.ecommerce.sales.application.service;

import com.pge.ecommerce.sales.application.dto.ProductResponse;
import com.pge.ecommerce.sales.application.dto.SaleRequest;
import com.pge.ecommerce.sales.application.dto.SaleResponse;
import com.pge.ecommerce.sales.domain.model.Sale;
import com.pge.ecommerce.sales.domain.repository.SaleRepository;
import com.pge.ecommerce.sales.infrastructure.client.ProductClient;
import feign.FeignException;
import feign.Request;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private SaleService saleService;

    private SaleRequest request;
    private ProductResponse productResponse;
    private Sale mockSale;

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                "joao@email.com", null, Collections.emptyList()
        );
        var context = new SecurityContextImpl(auth);
        SecurityContextHolder.setContext(context);

        request = new SaleRequest(1L, 2);

        productResponse = new ProductResponse(
                1L,
                "Notebook Dell Inspiron",
                "ELECTRONICS",
                new BigDecimal("3499.90")
        );

        mockSale = Sale.create(
                "joao@email.com",
                1L,
                "Notebook Dell Inspiron",
                2,
                new BigDecimal("3499.90")
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("createSale: deve retornar SaleResponse quando produto existe e usuario autenticado")
    void createSale_deveRetornarSaleResponse_quandoDadosValidos() {
        when(productClient.findById(1L)).thenReturn(productResponse);
        when(saleRepository.save(any(Sale.class))).thenReturn(mockSale);

        SaleResponse response = saleService.createSale(request);

        assertThat(response.userEmail()).isEqualTo("joao@email.com");
        assertThat(response.productId()).isEqualTo(1L);
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.unitPrice()).isEqualByComparingTo(new BigDecimal("3499.90"));
        assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("6999.80"));
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    @DisplayName("createSale: deve usar email do SecurityContext, nao de parametro externo")
    void createSale_deveUsarEmailDoSecurityContext() {
        when(productClient.findById(any())).thenReturn(productResponse);
        when(saleRepository.save(any(Sale.class))).thenReturn(mockSale);

        SaleResponse response = saleService.createSale(request);

        assertThat(response.userEmail()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("createSale: deve calcular totalPrice como unitPrice * quantity")
    void createSale_deveCalcularTotalPrice() {
        when(productClient.findById(any())).thenReturn(productResponse);
        when(saleRepository.save(any(Sale.class))).thenReturn(mockSale);

        SaleResponse response = saleService.createSale(request);

        BigDecimal esperado = new BigDecimal("3499.90").multiply(BigDecimal.valueOf(2));
        assertThat(response.totalPrice()).isEqualByComparingTo(esperado);
    }

    @Test
    @DisplayName("createSale: deve lancar EntityNotFoundException quando produto nao encontrado no Feign")
    void createSale_deveLancarEntityNotFoundException_quandoProdutoNaoEncontrado() {
        FeignException.NotFound notFound = mockFeignNotFound();
        when(productClient.findById(1L)).thenThrow(notFound);

        assertThatThrownBy(() -> saleService.createSale(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("1");

        verify(saleRepository, never()).save(any());
    }

    @Test
    @DisplayName("createSale: deve lancar IllegalStateException quando Products Service indisponivel")
    void createSale_deveLancarIllegalStateException_quandoFeignGenerico() {
        FeignException feignException = mockFeignServiceUnavailable();
        when(productClient.findById(1L)).thenThrow(feignException);

        assertThatThrownBy(() -> saleService.createSale(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Erro ao consultar Products Service");

        verify(saleRepository, never()).save(any());
    }

    @Test
    @DisplayName("createSale: deve lancar NullPointerException quando SecurityContext sem autenticacao")
    void createSale_deveLancarExcecao_quandoSecurityContextVazio() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> saleService.createSale(request))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("createSale: nao deve persistir venda quando Feign falha")
    void createSale_naoDevePersistir_quandoFeignFalha() {
        when(productClient.findById(any())).thenThrow(mockFeignNotFound());

        assertThatThrownBy(() -> saleService.createSale(request))
                .isInstanceOf(EntityNotFoundException.class);

        verify(saleRepository, never()).save(any());
    }

    @Test
    @DisplayName("findMySales: deve retornar apenas vendas do usuario autenticado")
    void findMySales_deveRetornarVendasDoUsuarioAutenticado() {
        when(saleRepository.findByUserEmail("joao@email.com"))
                .thenReturn(List.of(mockSale));

        List<SaleResponse> result = saleService.findMySales();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userEmail()).isEqualTo("joao@email.com");
        verify(saleRepository).findByUserEmail("joao@email.com");
    }

    @Test
    @DisplayName("findMySales: deve retornar lista vazia quando usuario nao tem vendas")
    void findMySales_deveRetornarListaVazia_quandoSemVendas() {
        when(saleRepository.findByUserEmail("joao@email.com"))
                .thenReturn(List.of());

        List<SaleResponse> result = saleService.findMySales();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findMySales: nao deve retornar vendas de outros usuarios")
    void findMySales_naoDeveRetornarVendasDeOutrosUsuarios() {
        when(saleRepository.findByUserEmail("joao@email.com"))
                .thenReturn(List.of(mockSale));

        List<SaleResponse> result = saleService.findMySales();

        assertThat(result).allMatch(s -> s.userEmail().equals("joao@email.com"));
        verify(saleRepository).findByUserEmail("joao@email.com");
    }

    @Test
    @DisplayName("findMySales: deve lancar NullPointerException quando SecurityContext sem autenticacao")
    void findMySales_deveLancarExcecao_quandoSecurityContextVazio() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> saleService.findMySales())
                .isInstanceOf(NullPointerException.class);
    }

    private FeignException.NotFound mockFeignNotFound() {
        Request dummyRequest = Request.create(
                Request.HttpMethod.GET,
                "http://products-service/api/products/1",
                Map.of(),
                null,
                null,
                null
        );
        return new FeignException.NotFound("Not Found", dummyRequest, null, Map.of());
    }

    private FeignException mockFeignServiceUnavailable() {
        Request dummyRequest = Request.create(
                Request.HttpMethod.GET,
                "http://products-service/api/products/1",
                Map.of(),
                null,
                null,
                null
        );
        return new FeignException.ServiceUnavailable(
                "Service Unavailable", dummyRequest, null, Map.of()
        );
    }
}