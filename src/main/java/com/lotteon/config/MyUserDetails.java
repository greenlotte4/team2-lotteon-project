package com.lotteon.config;

import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
public class MyUserDetails implements UserDetails, OAuth2User{
    private Member user;
    private Customer customer;
    private String accessToken;
    private Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_"+user.getMemRole())); // 계정 권한 앞에 접두어 ROLE_ 붙여야 됨
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getMemPwd();
    }

    @Override
    public String getUsername() {
        return user.getMemUid();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return String.valueOf(user.getId());
    }

//    @Override
//    public String getCustName(){
//        return customer.getCustName(); // 사용자 이름
//    }


}
