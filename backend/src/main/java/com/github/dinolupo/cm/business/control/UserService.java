package com.github.dinolupo.cm.business.control;

import com.github.dinolupo.cm.business.boundary.exception.ElementNotFoundException;
import com.github.dinolupo.cm.business.entity.Role;
import com.github.dinolupo.cm.business.entity.RoleRepository;
import com.github.dinolupo.cm.business.entity.User;
import com.github.dinolupo.cm.business.entity.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service @Transactional
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoleRepository roleRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepo.findByUsernameAndDisabled(username, false)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found in database.", username)));
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role ->
            authorities.add(new SimpleGrantedAuthority(role.getName()))
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );



    }

    public User addRoleToUser(String username, String rolename) {
        var user = userRepo.findByUsernameAndDisabled(username, false)
                .orElseThrow(()->new ElementNotFoundException(User.class, "username="+ username));
        var role = roleRepo.findByName(rolename)
                .orElseThrow(()->new ElementNotFoundException(Role.class, "name="+ rolename));
        user.getRoles().add(role);
        var userSaved = userRepo.save(user);
        return userSaved;
    }

}
