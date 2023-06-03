package com.pja.bloodcount;

import com.pja.bloodcount.model.Group;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.enums.GroupType;
import com.pja.bloodcount.model.enums.Role;
import com.pja.bloodcount.repository.GroupRepository;
import com.pja.bloodcount.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    private static final String ADMINISTRATION_GROUP = "ADMIN_GR";
    private static final String NO_GROUP = "NO_GR";

    @Autowired
    public DataLoader(GroupRepository groupRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Group defaultAdminGroup = Group
                .builder()
                .groupNumber(ADMINISTRATION_GROUP)
                .groupType(GroupType.ADMIN_GROUP)
                .build();

        Group noGroup = Group
                .builder()
                .groupNumber(NO_GROUP)
                .groupType(GroupType.STUDENT_GROUP)
                .build();
        groupRepository.saveAll(List.of(defaultAdminGroup, noGroup));

        User defaultAdmin = User
                .builder()
                .email("defAdmin@gmail.com")
                .password(passwordEncoder.encode("aaa1212AAA#"))
                .role(Role.ADMIN)
                .build();


        defaultAdminGroup.addUser(defaultAdmin);
        userRepository.save(defaultAdmin);
    }
}
