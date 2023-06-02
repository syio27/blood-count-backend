package com.pja.bloodcount.model;

import com.pja.bloodcount.model.enums.GroupType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "groups")
public class Group implements Serializable {

    @Id
    private String groupNumber;
    @Enumerated(EnumType.STRING)
    private GroupType groupType;
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        users.add(user);
        user.setGroup(this);
    }

    public void removeTag(User user) {
        if (this.users != null) {
            this.users.remove(user);
            user.setGroup(null);
        }
    }
}
