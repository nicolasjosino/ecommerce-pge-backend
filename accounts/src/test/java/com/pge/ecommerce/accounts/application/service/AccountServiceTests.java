package com.pge.ecommerce.accounts.application.service;

import com.pge.ecommerce.accounts.application.dto.LoginRequest;
import com.pge.ecommerce.accounts.application.dto.LoginResponse;
import com.pge.ecommerce.accounts.application.dto.RegisterRequest;
import com.pge.ecommerce.accounts.domain.model.Account;
import com.pge.ecommerce.accounts.domain.repository.AccountRepository;
import com.pge.ecommerce.accounts.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AccountService accountService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Account mockAccount;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "joao@email.com",
                "senha123",
                null
        );

        loginRequest = new LoginRequest(
                "joao@email.com",
                "senha123"
        );

        mockAccount = Account.create(
                "joao@email.com",
                "$2a$10$hashedpassword",
                null
        );
    }

    // ─────────────────────────────────────────────
    // register()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("register: deve salvar conta quando email nao esta cadastrado")
    void register_deveсалvarConta_quandoEmailDisponivel() {
        when(accountRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$hashedpassword");

        accountService.register(registerRequest);

        verify(accountRepository).existsByEmail("joao@email.com");
        verify(passwordEncoder).encode("senha123");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("register: deve lancar IllegalArgumentException quando email ja cadastrado")
    void register_deveLancarExcecao_quandoEmailJaCadastrado() {
        when(accountRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> accountService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("E-mail já cadastrado.");

        verify(accountRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("register: nao deve salvar senha em texto plano")
    void register_naoDeveSalvarSenhaEmTextoPlano() {
        when(accountRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$hashedpassword");

        accountService.register(registerRequest);

        verify(accountRepository).save(argThat(account ->
                !account.getPasswordHash().equals("senha123")
        ));
    }

    // ─────────────────────────────────────────────
    // login()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("login: deve retornar token e email quando credenciais validas")
    void login_deveRetornarTokenEEmail_quandoCredenciaisValidas() {
        when(accountRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(mockAccount));
        when(passwordEncoder.matches("senha123", mockAccount.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generateToken("joao@email.com")).thenReturn("jwt.token.aqui");

        LoginResponse response = accountService.login(loginRequest);

        assertThat(response.token()).isEqualTo("jwt.token.aqui");
        assertThat(response.email()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("login: deve lancar IllegalArgumentException quando usuario nao encontrado")
    void login_deveLancarExcecao_quandoUsuarioNaoEncontrado() {
        when(accountRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário não encontrado.");

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("login: deve lancar IllegalArgumentException quando senha incorreta")
    void login_deveLancarExcecao_quandoSenhaIncorreta() {
        when(accountRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(mockAccount));
        when(passwordEncoder.matches("senha123", mockAccount.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> accountService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Credenciais inválidas.");

        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("login: nao deve gerar token quando senha incorreta")
    void login_naoDeveGerarToken_quandoSenhaIncorreta() {
        when(accountRepository.findByEmail(any())).thenReturn(Optional.of(mockAccount));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> accountService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class);

        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("login: deve usar o email da conta para gerar o token, nao o email do request")
    void login_deveUsarEmailDaConta_paraGerarToken() {
        when(accountRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(mockAccount));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken("joao@email.com")).thenReturn("jwt.token.aqui");

        accountService.login(loginRequest);

        verify(jwtUtil).generateToken("joao@email.com");
    }
}