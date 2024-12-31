package com.mysql.sbb.user;


import com.mysql.sbb.DataNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BCryptPasswordEncoder encoder;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }
    public SiteUser getUser(String username){


        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if(siteUser.isPresent()){
            return siteUser.get();
        }
        else{
            throw new DataNotFoundException("siteuser  not found : "+username);
        }

    }

    public void modifyPassword(SiteUser user, String password){
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        this.userRepository.save(user);
    }

    public boolean matches(String newPassword, String currentPassword){
        return encoder.matches(newPassword,currentPassword);
    }
}