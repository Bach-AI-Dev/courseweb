package com.baitapnhom.courseweb.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
// import java.util.Collection;
import java.util.Date;
// import java.util.StringJoiner;

import com.baitapnhom.courseweb.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baitapnhom.courseweb.dto.request.AuthenticationRequest;
import com.baitapnhom.courseweb.dto.request.IntrospectRequest;
import com.baitapnhom.courseweb.dto.response.AuthenticationResponse;
import com.baitapnhom.courseweb.dto.response.IntrospectResponse;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
// import com.nimbusds.jose.util.CollectionUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiratime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
            .valid(verified && expiratime.after(new Date()))
            .build();
    }

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                user.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(token);
        response.setAuthenticated(true);

        return response;
    }

    private String generateToken(User user) {

        // Tạo header
        // Dùng thuật toán HS512 đủ mạnh để bảo vệ token an toàn
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Tạo 
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("kieudat.com")
                .issueTime(new Date())
                // Hết hạn sau 1 giờ
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope", builScope(user))
                .build();

        // Tạo Payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            System.err.println("Không thể tạo token: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private String builScope(User user) {
        if (user.getRole() != null) {
            return user.getRole().name();
        }
        return "";
        }
    }
