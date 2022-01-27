package com.dantts.authenticationJWT.service;

import com.dantts.authenticationJWT.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
public class JwtService {
    private final int expires = 1800000;
    private final String key = "asdajsdkj23490zcxziasdlnasd29348";

    public Long getLoginFromToken(String token) {
        return Long.valueOf((Integer) getClaim(token).get("userId"));
    }

    public String generateToken(User user) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("userRole", user.getRole());
        claims.put("userLogin", user.getLogin());

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expires))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public Claims getClaim(String token) {
        Claims claims = Jwts.parser().setSigningKey(key)
                .parseClaimsJws(token).getBody();

        return claims;
    }


}
