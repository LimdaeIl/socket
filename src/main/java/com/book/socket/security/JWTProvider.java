package com.book.socket.security;

import com.book.socket.common.constnat.Constants;
import com.book.socket.common.exception.CustomException;
import com.book.socket.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class JWTProvider {

    private static String secretKey;
    private static String refreshSecretKey;
    private static long tokenTimeForMinute;
    private static long refreshTokenTimeForMinute;

    @Value("${token.secret-key}")
    public void setSecretKey(String secretKey) {
        JWTProvider.secretKey = secretKey;
    }

    @Value("${token.refresh-secret-key}")
    public void setRefreshSecretKey(String refreshSecretKey) {
        JWTProvider.refreshSecretKey = refreshSecretKey;
    }

    @Value("${token.token-time}")
    public void setTokenTime(long tokenTime) {
        JWTProvider.tokenTimeForMinute = tokenTime;
    }

    @Value("${token.refresh-token-time}")
    public void setRefreshTokenTime(long refreshTokenTime) {
        JWTProvider.refreshTokenTimeForMinute = refreshTokenTime;
    }

    // jjwt 0.12.x에서 권장하는 방식: SecretKey 사용
    private static SecretKey accessKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private static SecretKey refreshKey() {
        return Keys.hmacShaKeyFor(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public static String createToken(String name) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenTimeForMinute * Constants.ON_MINUTE_TO_MILLIS);

        return Jwts.builder()
                .subject(name)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(accessKey()) // ★ 알고리즘은 키 길이로 자동 결정
                .compact();
    }

    public static String createRefreshToken(String name) {
        Date now = new Date();
        Date expiry = new Date(
                now.getTime() + refreshTokenTimeForMinute * Constants.ON_MINUTE_TO_MILLIS);

        return Jwts.builder()
                .subject(name)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(refreshKey()) // ★ 여기서도 key만 넘김
                .compact();
    }

    /**
     * 리프레시 발급용: 액세스 토큰이 아직 만료 안됐으면 예외, 만료됐으면 Claims 반환
     */
    public static Claims checkTokenForRefresh(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(accessKey())   // SecretKey 타입
                    .build()
                    .parseSignedClaims(token);

            log.error("token must be expired : {}", jws.getPayload().getSubject());
            throw new CustomException(ErrorCode.ACCESS_TOKEN_IS_NOT_EXPIRED);

        } catch (ExpiredJwtException e) {
            // jjwt는 만료된 토큰이라도 ExpiredJwtException 안에 Claims를 들고 있음
            return e.getClaims();

        } catch (JwtException e) {
            // 서명 불일치, 알고리즘 불일치, 잘못된 형식 등
            throw new CustomException(ErrorCode.TOKEN_IS_INVALID);
        }
    }

    public static Claims decodeAccessToken(String token) {
        return parseAndValidate(token, accessKey());
    }

    public static Claims decodeRefreshToken(String token) {
        return parseAndValidate(token, refreshKey());
    }

    private static Claims parseAndValidate(String token, SecretKey key) {
        log.info("jwt provider token: {}", token);
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            log.info("jws.getBody().getSubject(): {}", jws.getBody().getSubject());
            return jws.getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_IS_EXPIRED);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.TOKEN_IS_INVALID);
        }
    }

    /**
     * 서명 검증 없이 payload만 보고 싶은 경우. (가능하면 decodeAccessToken(...)을 사용하는 게 더 안전합니다.)
     */
    public static Claims decodedJWT(String token) {
        try {
            // verifyWith 없이도 파싱은 가능하지만, 서명 검증은 안 됩니다.
            Jws<Claims> jws = Jwts.parser()
                    .build()
                    .parseSignedClaims(token);
            return jws.getPayload();
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.TOKEN_IS_INVALID);
        }
    }

    public static String extractToken(String header) {
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        } else {
            throw new IllegalArgumentException("Invalid Auth Header");
        }
    }

    public static String getUserFromToken(String token) {
        // 액세스 토큰 기준으로 subject 읽어온다고 가정
        Claims claims = decodeAccessToken(token);
        return claims.getSubject();
    }
}
