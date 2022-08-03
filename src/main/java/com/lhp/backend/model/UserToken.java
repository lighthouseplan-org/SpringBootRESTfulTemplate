package com.lhp.backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "user_token", schema = "shopdb")
public class UserToken {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;
    @Basic
    @Column(name = "token")
    private String token;
    @Basic
    @Column(name = "user_id")
    private String userId;

    @Basic
    @Column(name = "expiry_date", columnDefinition = "TIMESTAMP DEFAULT TIMESTAMPADD(MINUTE, 30, CURRENT_TIMESTAMP)")
    private Timestamp expiryDate = new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000);

    public UserToken(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserToken userToken = (UserToken) o;
        return id != null && Objects.equals(id, userToken.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
