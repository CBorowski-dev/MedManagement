package de.medmanagement.rights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> optionalUser = usersRepository.findByName(userName);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            // System.out.println("--> Username: " + user.getName());
            // System.out.println("--> Password: " + user.getPassword());
            // System.out.println("--> Id: " + user.getId());
            // System.out.println("--> Encoded password: " + bCryptPasswordEncoder.encode(user.getPassword()));

            List<String> roleList = new ArrayList<String>();
            for(Role role:user.getRoles()) {
                roleList.add(role.getRoleName());
            }

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getName())
                    //change here to store encoded password in db
                    //.password( bCryptPasswordEncoder.encode(user.getPassword()) )
                    .password(user.getPassword())
                    .disabled(user.isDisabled())
                    .accountExpired(user.isAccountExpired())
                    .accountLocked(user.isAccountLocked())
                    .credentialsExpired(user.isCredentialsExpired())
                    .roles(roleList.toArray(new String[0]))
                    .build();
        } else {
            throw new UsernameNotFoundException("User Name is not Found");
        }
    }

}