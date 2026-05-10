package edu.cit.fernandez.goatedmeals.auth;

public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String role;

    // Private constructor for the builder
    private UserResponseDTO(Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.firstname = builder.firstname;
        this.lastname = builder.lastname;
        this.role = builder.role;
    }

    // Static method to initiate the builder
    public static Builder builder() {
        return new Builder();
    }

    // Standard Getters (Required for JSON serialization)
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getRole() { return role; }

    // Static Inner Builder Class
    public static class Builder {
        private Long id;
        private String email;
        private String firstname;
        private String lastname;
        private String role;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder firstname(String firstname) { this.firstname = firstname; return this; }
        public Builder lastname(String lastname) { this.lastname = lastname; return this; }
        public Builder role(String role) { this.role = role; return this; }

        public UserResponseDTO build() {
            return new UserResponseDTO(this);
        }
    }
}