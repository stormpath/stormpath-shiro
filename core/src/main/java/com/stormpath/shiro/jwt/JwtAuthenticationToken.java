package com.stormpath.shiro.jwt;


import org.apache.shiro.authc.UsernamePasswordToken;

public class JwtAuthenticationToken extends UsernamePasswordToken {
    public JwtAuthenticationToken(final String username, final String password) {
        super(username, password);
    }
}
