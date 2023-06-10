package com.pja.bloodcount;

import com.pja.bloodcount.model.*;
import com.pja.bloodcount.model.enums.AffectedGender;
import com.pja.bloodcount.model.enums.GroupType;
import com.pja.bloodcount.model.enums.LevelType;
import com.pja.bloodcount.model.enums.Role;
import com.pja.bloodcount.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final BCReferenceRepository bcReferenceRepository;
    private final CaseRepository caseRepository;
    private final PasswordEncoder passwordEncoder;


    private static final String ADMINISTRATION_GROUP = "ADMIN_GR";
    private static final String NO_GROUP = "NO_GR";

    @Autowired
    public DataLoader(GroupRepository groupRepository,
                      UserRepository userRepository,
                      BCReferenceRepository bcReferenceRepository,
                      CaseRepository caseRepository,
                      PasswordEncoder passwordEncoder) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.bcReferenceRepository = bcReferenceRepository;
        this.caseRepository = caseRepository;
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

        final BloodCountReference WBC = BloodCountReference
                .builder()
                .parameter("WBC")
                .unit("10^9/L")
                .minFemale(4.0)
                .maxFemale(11.0)
                .minMale(4.0)
                .maxMale(11.0)
                .build();

        final BloodCountReference RBC = BloodCountReference
                .builder()
                .parameter("RBC")
                .unit("10^12/L")
                .minFemale(4.0)
                .maxFemale(5.2)
                .minMale(4.6)
                .maxMale(5.7)
                .build();

        final BloodCountReference HGB = BloodCountReference
                .builder()
                .parameter("HGB")
                .unit("g/dl")
                .minFemale(11.5)
                .maxFemale(16.4)
                .minMale(13.5)
                .maxMale(18.0)
                .build();

        final BloodCountReference HCT = BloodCountReference
                .builder()
                .parameter("HCT")
                .unit("%")
                .minFemale(37.0)
                .maxFemale(47.0)
                .minMale(42.0)
                .maxMale(52.0)
                .build();

        final BloodCountReference MCV = BloodCountReference
                .builder()
                .parameter("MCV")
                .unit("fl")
                .minFemale(81d)
                .maxFemale(99d)
                .minMale(80d)
                .maxMale(94d)
                .build();

        final BloodCountReference MCH = BloodCountReference
                .builder()
                .parameter("MCH")
                .unit("pg")
                .minFemale(27d)
                .maxFemale(31d)
                .minMale(27d)
                .maxMale(31d)
                .build();

        final BloodCountReference MCHC = BloodCountReference
                .builder()
                .parameter("MCHC")
                .unit("g/dl")
                .minFemale(33d)
                .maxFemale(47d)
                .minMale(33d)
                .maxMale(37d)
                .build();

        final BloodCountReference PLT = BloodCountReference
                .builder()
                .parameter("PLT")
                .unit("10^9/L")
                .minFemale(130.0)
                .maxFemale(450.0)
                .minMale(130.0)
                .maxMale(450.0)
                .build();

        final BloodCountReference RDW_CV = BloodCountReference
                .builder()
                .parameter("RDW_CV")
                .unit("%")
                .minFemale(11.6)
                .maxFemale(14.4)
                .minMale(11.6)
                .maxMale(14.4)
                .build();

        final BloodCountReference RDW_SD = BloodCountReference
                .builder()
                .parameter("RDW_SD")
                .unit("fl")
                .minFemale(35.1)
                .maxFemale(43.9)
                .minMale(35.1)
                .maxMale(43.9)
                .build();

        final BloodCountReference NEU = BloodCountReference
                .builder()
                .parameter("NEU")
                .unit("10^9/L")
                .minFemale(2d)
                .maxFemale(7d)
                .minMale(2d)
                .maxMale(7d)
                .build();

        final BloodCountReference LYM = BloodCountReference
                .builder()
                .parameter("LYM")
                .unit("10^9/L")
                .minFemale(1d)
                .maxFemale(4.5)
                .minMale(1d)
                .maxMale(4.5)
                .build();

        final BloodCountReference MONO = BloodCountReference
                .builder()
                .parameter("MONO")
                .unit("10^9/L")
                .minFemale(0.19)
                .maxFemale(0.77)
                .minMale(0.19)
                .maxMale(0.77)
                .build();

        final BloodCountReference EOS = BloodCountReference
                .builder()
                .parameter("EOS")
                .unit("10^9/L")
                .minFemale(0.02)
                .maxFemale(0.5)
                .minMale(0.02)
                .maxMale(0.5)
                .build();

        final BloodCountReference BASO = BloodCountReference
                .builder()
                .parameter("BASO")
                .unit("10^9/L")
                .minFemale(0.02)
                .maxFemale(0.1)
                .minMale(0.02)
                .maxMale(0.1)
                .build();

        final BloodCountReference NEU_p = BloodCountReference
                .builder()
                .parameter("NEU")
                .unit("%")
                .minFemale(40d)
                .maxFemale(80d)
                .minMale(40d)
                .maxMale(80d)
                .build();

        final BloodCountReference LYM_p = BloodCountReference
                .builder()
                .parameter("LYM")
                .unit("%")
                .minFemale(20d)
                .maxFemale(40d)
                .minMale(20d)
                .maxMale(40d)
                .build();

        final BloodCountReference MONO_p = BloodCountReference
                .builder()
                .parameter("MONO")
                .unit("%")
                .minFemale(2d)
                .maxFemale(10d)
                .minMale(2d)
                .maxMale(10d)
                .build();

        final BloodCountReference EOS_p = BloodCountReference
                .builder()
                .parameter("EOS")
                .unit("%")
                .minFemale(1d)
                .maxFemale(6d)
                .minMale(1d)
                .maxMale(6d)
                .build();

        final BloodCountReference BASO_p = BloodCountReference
                .builder()
                .parameter("BASO")
                .unit("%")
                .minFemale(0d)
                .maxFemale(2d)
                .minMale(0d)
                .maxMale(2d)
                .build();

        Case case1 = Case
                .builder()
                .firstMinAge(18)
                .firstMaxAge(75)
                .affectedGender(AffectedGender.FEMALE)
                .anemiaType("N. mikrocytowa")
                .diagnosis("Niedokrwistość z niedoboru żelaza z powodu nadmiernych miesiączek")
                .build();

        BloodCountAbnormality abnormality = BloodCountAbnormality
                .builder()
                .parameter("HGB")
                .unit("g/dl")
                .minValue(9.5)
                .maxValue(10.9)
                .type(LevelType.DECREASED)
                .build();


        case1.addAbnormality(abnormality);
        caseRepository.save(case1);

        defaultAdminGroup.addUser(defaultAdmin);
        bcReferenceRepository.saveAll(
                List.of(
                WBC, RBC, HGB, HCT, MCV, MCH, MCHC, PLT,
                RDW_CV, RDW_SD,
                NEU, LYM, MONO, EOS, BASO,
                NEU_p, LYM_p, MONO_p, EOS_p, BASO_p));
        userRepository.save(defaultAdmin);
    }
}
