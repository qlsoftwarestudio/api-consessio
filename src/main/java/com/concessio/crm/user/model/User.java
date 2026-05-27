package com.concessio.crm.user.model;

import com.concessio.crm.tenant.model.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email", "tenant_id"})
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant; // Sucursal asignada

    @NotBlank
    private String name;
    
    @NotBlank
    private String lastname;
    
    @NotNull
    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    private String phone; // Teléfono del vendedor/a
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @NotNull
    private boolean isActive;
    
    @NotBlank
    private String password;

    public User(String name, String lastname, String email, Role role, boolean isActive, Tenant tenant) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.tenant = tenant;
    }

    // Constructor para compatibilidad con tests (tenant se asigna después)
    public User(String name, String lastname, String email, Role role, boolean isActive) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean getIsActive() { return isActive; }
    public Role getRole() { return role; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(Role role) { this.role = role; }
    public void setActive(boolean active) { this.isActive = active; }
    public void setPassword(String password) { this.password = password; }
    public Tenant getTenant() {
        return tenant;
    }
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}
