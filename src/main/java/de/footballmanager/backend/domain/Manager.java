package de.footballmanager.backend.domain;

public class Manager {
    private String firstName;
    private String lastName;
    private boolean computerManaged = true;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isComputerManaged() {
        return computerManaged;
    }

    public void setComputerManaged(boolean computerManaged) {
        this.computerManaged = computerManaged;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", computerManaged=" + computerManaged +
                '}';
    }
}
