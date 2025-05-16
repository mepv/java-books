package com.accenture.authserver.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String CATEGORY_PREFIX = "CATEGORY_";
    public static final String AUTHOR_PREFIX = "AUTHOR_";
    public static final String ROLE_CLIENT = "CLIENT";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String CATEGORY_NOVELA = "CATEGORY_NOVELA";
    public static final String CATEGORY_ENSAYO = "CATEGORY_ENSAYO";
    public static final String CATEGORY_POESIA = "CATEGORY_POESIA";
    public static final String AUTHOR_CORTAZAR = "AUTHOR_CORTAZAR";
    public static final String AUTHOR_GARCIA_MARQUEZ = "AUTHOR_GARCIA_MARQUEZ";
    public static final String AUTHOR_VARGAS_LLOSA = "AUTHOR_VARGAS_LLOSA";
    public static final String AUTHOR_GALLEGOS = "AUTHOR_GALLEGOS";

    @Value("${book.library.spa-client-id}")
    public String spaClientId;
    @Value("${book.library.postman-client-id}")
    public String postmanClientId;
    @Value("${book.library.postman-client-secret}")
    public String postmanClientSecret;
    @Value("${book.library.user-password}")
    public String userPassword;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        return http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .cors(corsConfig -> corsConfig.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setExposedHeaders(List.of("Authorization"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                .with(authorizationServerConfigurer, authServer -> authServer.oidc(Customizer.withDefaults())) // Enable OpenID Connect 1.0
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("http://localhost:4200/"))
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.builder()
                .username("user")
                .password(passwordEncoder().encode(userPassword))
                .roles(ROLE_CLIENT, CATEGORY_NOVELA, AUTHOR_CORTAZAR)
                .build();
        UserDetails userDetails1 = User.builder()
                .username("user-1")
                .password(passwordEncoder().encode(userPassword))
                .roles(ROLE_CLIENT, CATEGORY_NOVELA, CATEGORY_ENSAYO, AUTHOR_CORTAZAR, AUTHOR_GARCIA_MARQUEZ)
                .build();
        UserDetails userDetails2 = User.builder()
                .username("user-2")
                .password(passwordEncoder().encode(userPassword))
                .roles(ROLE_CLIENT, CATEGORY_POESIA, AUTHOR_VARGAS_LLOSA, AUTHOR_GALLEGOS)
                .build();
        UserDetails userDetails3 = User.builder()
                .username("user-3")
                .password(passwordEncoder().encode(userPassword))
                .roles(ROLE_CLIENT, CATEGORY_ENSAYO, "ALL")
                .build();
        UserDetails adminDetails = User.builder()
                .username("admin")
                .password(passwordEncoder().encode(userPassword))
                .roles(ROLE_ADMIN, "ALL")
                .build();

        return new InMemoryUserDetailsManager(userDetails, userDetails1, userDetails2, userDetails3, adminDetails);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient postmanClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(postmanClientId)
                .clientSecret(passwordEncoder().encode(postmanClientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.EMAIL)
                .scope("book:write")
                .scope("book:read")
                .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(60))
                        .refreshTokenTimeToLive(Duration.ofHours(8)).reuseRefreshTokens(false)
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED).build())
                .build();

        RegisteredClient pkceClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(spaClientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:4200/oauth/callback")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.EMAIL)
                .scope("book:write")
                .scope("book:read")
                .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(60))
                        .refreshTokenTimeToLive(Duration.ofHours(8)).reuseRefreshTokens(false)
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED).build())
                .build();

        return new InMemoryRegisteredClientRepository(pkceClient, postmanClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                context.getClaims().claims(claims -> {
                    Set<String> authorities = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                            .stream()
                            .map(authority -> authority.replaceFirst(ROLE_PREFIX, ""))
                            .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
                    // roles
                    Set<String> roles = authorities
                            .stream()
                            .filter(authority -> !authority.startsWith(CATEGORY_PREFIX)
                                    && !authority.startsWith(AUTHOR_PREFIX) && !authority.equals("ALL"))
                            .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
                    // categories
                    Set<String> categories = authorities
                            .stream()
                            .filter(authority -> (!authority.startsWith(CATEGORY_PREFIX) || !authority.startsWith(AUTHOR_PREFIX))
                                    && ((!authority.startsWith(AUTHOR_PREFIX) && authority.startsWith(CATEGORY_PREFIX)) || authority.equals("ALL")))
                            .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
                    // authors
                    Set<String> authors = authorities
                            .stream()
                            .filter(authority -> (!authority.startsWith(CATEGORY_PREFIX) || !authority.startsWith(AUTHOR_PREFIX))
                                    && ((!authority.startsWith(CATEGORY_PREFIX) && authority.startsWith(AUTHOR_PREFIX)) || authority.equals("ALL")))
                            .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
                    claims.put("roles", roles);
                    claims.put("categories", categories);
                    claims.put("authors", authors);
                });
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
