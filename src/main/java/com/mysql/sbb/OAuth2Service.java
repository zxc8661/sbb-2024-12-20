package com.mysql.sbb;

import com.mysql.sbb.user.SiteUser;
import com.mysql.sbb.user.UserProfile;
import com.mysql.sbb.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 로그인을 수행한 서비스의 이름

        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // PK가 되는 정보

        Map<String, Object> attributes = oAuth2User.getAttributes(); // 사용자가 가지고 있는 정보

        UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);
        userProfile.setProvider(registrationId);

        updateOrSaveUser(userProfile);

        Map<String, Object> customAttribute =
                getCustomAttribute(registrationId, userNameAttributeName, attributes, userProfile);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                customAttribute,
                userNameAttributeName);
    }

    public Map getCustomAttribute(String registrationId,
                                  String userNameAttributeName,
                                  Map<String, Object> attributes,
                                  UserProfile userProfile) {
        Map<String, Object> customAttribute = new ConcurrentHashMap<>();

        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("provider", registrationId);
        customAttribute.put("name", userProfile.getUsername());
        customAttribute.put("email", userProfile.getEmail());


        return customAttribute;
    }

    public SiteUser updateOrSaveUser(UserProfile userProfile) {
        // 이메일과 Provider를 기반으로 기존 사용자 확인
        Optional<SiteUser> existingUser = userRepository.findUserByEmailAndProvider(
                userProfile.getEmail(),
                userProfile.getProvider()
        );

        if (existingUser.isPresent()) {
            // 기존 사용자 정보 업데이트
            SiteUser user = existingUser.get();
            user.setUsername(userProfile.getUsername()); // 필요한 경우 이름 업데이트
            return userRepository.save(user);
        } else {
            // 새로운 사용자 생성
            SiteUser newUser = userProfile.toEntity();
            return userRepository.save(newUser);
        }
    }
}
