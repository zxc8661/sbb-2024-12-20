package com.mysql.sbb;

import com.mysql.sbb.user.UserProfile;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {

    GOOGLE("google", (attribute) -> {
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername((String) attribute.get("name")); // 사용자 이름
        userProfile.setEmail((String) attribute.get("email"));   // 사용자 이메일
        return userProfile;
    }),

    NAVER("naver", (attribute) -> {
        UserProfile userProfile = new UserProfile();
        Map<String, String> responseValue = (Map<String, String>) attribute.get("response");
        userProfile.setUsername(responseValue.get("name"));     // 사용자 이름
        userProfile.setEmail(responseValue.get("email"));       // 사용자 이메일
        return userProfile;
    });
    private final String registrationId; // 로그인한 서비스 ID (ex. google, naver)
    private final Function<Map<String, Object>, UserProfile> of; // 사용자 정보를 UserProfile로 매핑

    OAuthAttributes(String registrationId, Function<Map<String, Object>, UserProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserProfile extract(String registrationId, Map<String, Object> attributes) {
        // registrationId에 해당하는 로그인 서비스의 사용자 정보를 추출
        return Arrays.stream(values())
                .filter(value -> registrationId.equals(value.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}

