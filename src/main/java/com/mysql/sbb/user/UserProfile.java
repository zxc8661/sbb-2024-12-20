package com.mysql.sbb.user;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserProfile {
    private String username;
    private String provider;
    private String email;


    public SiteUser toEntity(){
        SiteUser siteUser = new SiteUser();
        siteUser.setUsername(this.username);
        siteUser.setEmail(this.email);
        siteUser.setProvider(this.provider);

        return siteUser;
    }
}
