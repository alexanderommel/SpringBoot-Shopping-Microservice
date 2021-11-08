package com.example.tongue.core.utilities;

import com.example.tongue.core.authentication.Role;
import com.example.tongue.core.authentication.User;
import com.example.tongue.core.authentication.RoleRepository;
import com.example.tongue.core.authentication.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

@RestController
public class CustomerRestController {

    private UserRepository repository;
    private RoleRepository roleRepository;

    /**
     * ---------------------------- CONTROLLER JUST FOR TESTING PURPOSES ---------------------
     * @param repository
     * @param roleRepository
     */

    public CustomerRestController(@Autowired UserRepository repository,@Autowired RoleRepository roleRepository){
        this.repository=repository;
        this.roleRepository=roleRepository;
    }

    @GetMapping("/customer/login")
    public void login(HttpServletRequest request){

        System.out.println("Spring generated session: "+request.getSession().getId());
        System.out.println(request.getHeader("Cookie"));
        //System.out.println(request.getHeader("Authorization"));
        System.out.println(request.getUserPrincipal().getName());
        //initialize session attributes
        request.getSession().setAttribute("USER",request.getUserPrincipal().getName());
    }

    @GetMapping("/customer/register")
    public void register(HttpServletRequest request){
        //we obtain the user credentials by parsing authentication header
        /*
        Temporal///////////////
        Insert users to our server
         */
        Role roleUser = new Role();
        Role roleAdmin = new Role();
        Role roleDriver = new Role();
        roleUser.setName("USER");
        roleAdmin.setName("ADMIN");
        roleDriver.setName("DRIVER");


        User user1 = new User();
        user1.setActive(true);
        user1.setUsername("rommel");
        user1.setPassword(new BCryptPasswordEncoder().encode("password"));

        Set<Role> roles = new HashSet<Role>();
        roles.add(roleUser);
        roles.add(roleAdmin);
        user1.setRoles(roles);

        User user2 = new User();
        user2.setActive(true);
        user2.setUsername("sheccid");
        user2.setPassword(new BCryptPasswordEncoder().encode("password"));

        Set<Role> roles2 = new HashSet<Role>();
        roles2.add(roleDriver);
        user2.setRoles(roles2);

        this.roleRepository.save(roleUser);
        this.roleRepository.save(roleAdmin);
        this.roleRepository.save(roleDriver);
        this.repository.save(user1);
        this.repository.save(user2);

        System.out.println(repository.getUserByUsername("rommel"));
        System.out.println(repository.getUserByUsername("sheccid"));

    }

    @GetMapping("/customer/cerrar")
    public String cerrar(HttpServletRequest request){
        request.getSession().invalidate();
        return "CERRAR SESION";
    }
}
