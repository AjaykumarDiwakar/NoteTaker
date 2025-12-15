package com.notetaker.auth.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@NotBlank(message = "The name field cannot be blank")
	private String name;

	@NotBlank(message = "The username field cannot be blank")
	@Column(unique = true, name = "username")
	private String actualUsername;

	@NotBlank(message = "The email field cannot be empty")
	@Column(unique = true)
	@Email(message = "Please enter email in valid format")
	private String email;

	@NotBlank(message = "The password field cannot be blank")
	@Size(min = 5, message = "The password must have atleast 5 characters")
	private String password;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	@OneToOne(mappedBy = "user")
	private RefreshToken refreshToken;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(userRole.name()));
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

	public String getUsername() {
		return email;
	}

	public String getPassword() {
		return password;
	}

}
