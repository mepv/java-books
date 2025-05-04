package com.accenture.booklibrary.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AuthRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        List<String> roles = source.getClaimAsStringList("roles");
        List<String> scopes = source.getClaimAsStringList("scope");
        List<String> categories = source.getClaimAsStringList("categories");
        List<String> authors = source.getClaimAsStringList("authors");
        roles.addAll(scopes);
        roles.addAll(categories);
        roles.addAll(authors);

        return roles
                .stream()
                .map(roleName -> {
                    if (roleName.equals("ADMIN") || roleName.equals("CLIENT")) roleName = "ROLE_" + roleName;
                    return roleName;
                })
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
