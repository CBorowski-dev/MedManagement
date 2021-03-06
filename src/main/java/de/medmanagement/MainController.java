package de.medmanagement;

import de.medmanagement.model.*;
import de.medmanagement.rights.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController implements WebMvcConfigurer {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final String GREEN = "rgb(34,139,34)";
    private final String ORANGE = "rgb(255,140,0)";
    private final String RED = "rgb(255,0,0)";

    private final int GREEN_MIN = 15;
    private final int ORANGE_MIN = 8;

    // Shows the application name when the URL ends with /medmanagement
    public String index() {
        return "MedManagement Application";
    }

    /**
     *
     * @param registry
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    /**
     * Get info about currently logged in user
     * @return UserDetails if found in the context, null otherwise
     */
    protected UserDetails getUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = null;
        if (principal instanceof UserDetails) {
            userDetails = (UserDetails) principal;
        }
        return userDetails;
    }

    /**
     *
     * @return Username of the current user.
     */
    private String getUserName() {
        UserDetails userDetails = getUserDetails();
        return  userDetails.getUsername();
    }

    /**
     *
     * @param role
     * @return
     */
    protected final boolean hasRole(String role) {
        UserDetails userDetails = getUserDetails();
        if (userDetails != null) {
            Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) userDetails.getAuthorities();
            if (isRolePresent(authorities, role)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Check if a role is present in the authorities of current user
     * @param authorities all authorities assigned to current user
     * @param role required authority
     * @return true if role is present in list of authorities assigned to current user, false otherwise
     */
    private boolean isRolePresent(Collection<GrantedAuthority> authorities, String role) {
        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals(role))
                return true;
        }
        return false;
    }

    /**
     *
     * @param model
     * @return
     */
    @GetMapping(path="/")
    public String home(Model model) {
        return hasRole(Role.ADMIN) ? showUsers(model) : showDrugs(model);
    }

    /**
     *
     * @param model
     * @return
     */
    @GetMapping(path="/showDrugs")
    public String showDrugs(Model model) {

        // ToDo... Entities nicht an das View weitergeben
        ArrayList<Drug> drugs = userDataService.getDrugs(getUserName());
        model.addAttribute("drugs", drugs);

        //horizontal axis
        String label[] = new String[drugs.size()];

        //The vertical axis
        int point[] = new int[drugs.size()];

        // color
        String color[] = new String[drugs.size()];

        for (int i=0; i<drugs.size(); i++) {
            label[i] = drugs.get(i).getName();
            point[i] = drugs.get(i).getDaysLeft();
            color[i] = GREEN;
            if (point[i] < ORANGE_MIN) {
                color[i] = RED;
            } else if (point[i] < GREEN_MIN) {
                color[i] = ORANGE;
            }
        }

        //and stores it in the model
        model.addAttribute("label",label);
        model.addAttribute("point",point);
        model.addAttribute("color", color);

        return "showDrugs";
    }

    /**
     *
     * @param model
     * @return
     */
    @GetMapping(path="/showUsers")
    public String showUsers(Model model) {

        List<User> users = usersRepository.findAll();
        model.addAttribute("users", users);

        return "showUsers";
    }

    /**
     *
     * @param model
     * @return
     */
    @GetMapping(path="/createUser")
    public String createUser(Model model) {

        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("roles", rolesRepository.findAll());

        return "createUserForm";
    }

    /**
     *
     * @param userDTO
     * @return
     */
    @PostMapping(path="/createUser")
    public RedirectView createUserSubmit(@ModelAttribute("userDTO") UserDTO userDTO) {
        User newUser = new User(userDTO.firstname,
                userDTO.lastname,
                userDTO.email,
                userDTO.name,
                passwordEncoder.encode(userDTO.password),
                userDTO.address,
                userDTO.phoneNumber,
                userDTO.disabled,
                userDTO.accountExpired,
                userDTO.accountLocked,
                userDTO.credentialsExpired);
        Optional<Role> optionalRole = rolesRepository.findByRoleName(userDTO.rolename);
        if (optionalRole.isPresent()) {
            newUser.addRole(optionalRole.get());
        }
        usersRepository.save(newUser);

        return new RedirectView("showUsers");
    }

    /**
     *
     * @param userId
     * @param model
     * @return
     */
    @PostMapping(path="/editUser") // Map ONLY POST Requests
    public String editUser(@RequestParam("id") Integer userId, Model model) {

        Optional<User> optionalUser = usersRepository.findById(userId);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("userDTO", new UserDTO(user));
            model.addAttribute("roles", rolesRepository.findAll());
        }

        return "editUserForm";
    }

    /**
     *
     * @param userDTO
     * @return
     */
    @PostMapping(path="/editUserForm") // Map ONLY POST Requests
    public RedirectView editUserSubmit(@ModelAttribute("userDTO") UserDTO userDTO) {

        // userDataService.updateDrug(drugData, getUserName());
        Optional<User> optionalUser = usersRepository.findById(userDTO.id);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();

            user.setFirstname(userDTO.firstname);
            user.setLastname(userDTO.lastname);
            user.setEmail(userDTO.email);
            user.setName(userDTO.name);
            user.setAddress(userDTO.address);
            user.setPhoneNumber(userDTO.phoneNumber);
            user.setDisabled(userDTO.disabled);
            user.setAccountExpired(userDTO.accountExpired);
            user.setAccountLocked(userDTO.accountLocked);
            user.setCredentialsExpired(userDTO.credentialsExpired);

            usersRepository.save(user);
        }
        return new RedirectView("showUsers");
    }

    /**
     *
     * @param userId
     * @return
     */
    @PostMapping(path="/deleteUser") // Map ONLY POST Requests
    public RedirectView deleteUserSubmit(@RequestParam("id") Integer userId) {

        Optional<User> optionalUser = usersRepository.findById(userId);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            // delete all drugs and drug history for the user
            userDataService.deleteAllDrugs(user.getName());
            // delete user
            usersRepository.deleteById(userId);
        }

        return new RedirectView("showUsers");
    }


    /**
     *
     * @param model
     * @return
     */
    @GetMapping(path="/createDrug")
    public String createDrug(Model model) {

        model.addAttribute("drugData", new DrugDTO());

        return "createDrugForm";
    }

    /**
     *
     * @param drugData
     * @return
     */
    @PostMapping(path="/createDrug") // Map ONLY POST Requests
    public RedirectView createDrugSubmit(@ModelAttribute("drugData") DrugDTO drugData) {

        userDataService.createDrug(drugData, getUserName());

        return new RedirectView("showDrugs");
    }

    /**
     *
     * @param drugId
     * @param model
     * @return
     */
    @PostMapping(path="/editDrug") // Map ONLY POST Requests
    public String editDrug(@RequestParam("id") Integer drugId, Model model) {

        DrugDTO drugData = userDataService.getDrugData(drugId, getUserName());
        model.addAttribute("drugData", drugData);

        return "editDrugForm";
    }

    /**
     *
     * @param drugData
     * @return
     */
    @PostMapping(path="/editDrugForm") // Map ONLY POST Requests
    public RedirectView editDrugSubmit(@ModelAttribute("drugData") DrugDTO drugData) {

        userDataService.updateDrug(drugData, getUserName());

        return new RedirectView("showDrugs");
    }

    /**
     *
     * @param drugId
     * @return
     */
    @PostMapping(path="/deleteDrug") // Map ONLY POST Requests
    public RedirectView deleteDrugSubmit(@RequestParam("id") Integer drugId) {

        userDataService.deleteDrug(drugId, getUserName());

        return new RedirectView("showDrugs");
    }

    /**
     *
     * @param drugId
     * @param model
     * @return
     */
    @PostMapping(path="/changeDrugCount") // Map ONLY POST Requests
    public String changeDrugCountSubmit(@RequestParam("id") Integer drugId, Model model) {

        ChangeDrugCountData changeDrugCountData = new ChangeDrugCountData(userDataService.getDrug(drugId, getUserName()));
        model.addAttribute("changeDrugCountData", changeDrugCountData);

        return "changeDrugCountForm";
    }

    /**
     *
     * @param data
     * @return
     */
    @PostMapping(path="/changeDrugCountForm") // Map ONLY POST Requests
    public RedirectView changeDrugCountFormSubmit(@ModelAttribute("changeDrugCountData") ChangeDrugCountData data) {

        userDataService.changeDrugCount(data.getId(), data.getCount(), data.getComment(), getUserName());

        return new RedirectView("showDrugs");
    }

    /**
     *
     * @param model
     * @return
     */
    @GetMapping(path="/createOrder")
    public String createOrder(Model model) {

        // ToDo... Entities nicht an das View weitergeben
        model.addAttribute("drugs", userDataService.getDrugs(getUserName()));
        model.addAttribute("orderItems", userDataService.getOrderItems(getUserName()));

        return "createOrder";
    }

    /**
     *
     * @param drugId
     * @param model
     * @return
     */
    @PostMapping(path="/createOrderItem")
    public String createOrderItemSubmit(@RequestParam("id") Integer drugId, Model model) {

        OrderItem orderItem = userDataService.getNewOrderItem(drugId, getUserName());
        model.addAttribute("orderItem", orderItem);

        return "createOrderItemForm";
    }

    /**
     *
     * @param drugId
     * @param orderItem
     * @return
     */
    @PostMapping(path="/addToOrder") // Map ONLY POST Requests
    public RedirectView addToOrderSubmit(@RequestParam("id") Integer drugId, @ModelAttribute("orderItem") OrderItem orderItem) {

        userDataService.addOrderItem(orderItem, getUserName());

        return new RedirectView("createOrder");
    }

    /**
     *
     * @param drugId
     * @return
     */
    @PostMapping(path="/deleteFromOrder") // Map ONLY POST Requests
    public RedirectView deleteFromOrder(@RequestParam("id") Integer drugId) {

        userDataService.deleteOrderItem(drugId, getUserName());

        return new RedirectView("createOrder");
    }

    /**
     *
     * @return
     */
    @GetMapping(path="/clearOrder")
    public RedirectView clearOrder() {

        userDataService.getClearOrder(getUserName());

        return new RedirectView("createOrder");
    }

    /**
     * sdlfksldfkjsldf
     * @param model
     * @return
     */
    @GetMapping(path="/generateOrder")
    public String generateOrder(Model model) {

        Optional<User> optionalUser = usersRepository.findByName(getUserName());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("firstname", user.getFirstname());
            model.addAttribute("lastname", user.getLastname());
            model.addAttribute("phoneNumber", user.getPhoneNumber());
            String address = user.getAddress();
            String[] a = address.split("\n");
            model.addAttribute("street", a[0]);
            model.addAttribute("city", a[1]);
        }
        model.addAttribute("orderItemsGeneric", userDataService.getOrderItems(false, getUserName()));
        model.addAttribute("orderItemsOriginal", userDataService.getOrderItems(true, getUserName()));

        return "showOrder";
    }

    /**
     *
     * @param model
     * @return
     */
    @GetMapping(path="/changePassword")
    public String changePassword(Model model) {

        model.addAttribute("passwordDTO", new PasswordDTO());

        return "changePasswordForm";
    }

    /**
     *
     * @param passwordDTO
     * @return
     */
    @PostMapping(path="/changePasswordSubmit")
    public String changePasswordSubmit(@ModelAttribute("passwordDTO") PasswordDTO passwordDTO) {
        String newPassword = passwordDTO.getNewPassword();
        String newPasswordConfirmation = passwordDTO.getNewPasswordConfirmation();

        if (newPassword != null && !newPassword.trim().equals("")
            && newPasswordConfirmation != null && !newPasswordConfirmation.trim().equals("")
            && newPassword.equals(newPasswordConfirmation)) {

            Optional<User> optionalUser = usersRepository.findByName(getUserName());
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setPassword(passwordEncoder.encode(newPassword));
                usersRepository.save(user);
            }
            // return new RedirectView("passwordChangeSucceeded");
            return "passwordChangeSucceeded";
        } else {
            // password and password conformation are not equal or one or both are null
            // return new RedirectView("passwordChangeFailed");
            return "passwordChangeFailed";
        }
    }
} // MainController