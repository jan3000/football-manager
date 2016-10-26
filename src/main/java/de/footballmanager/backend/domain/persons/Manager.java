package de.footballmanager.backend.domain.persons;

public class Manager extends Person {

    private boolean computerManaged = true;

    public Manager(String firstName, String lastName) {
        super(firstName, lastName);
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
                super.toString() +
                ", computerManaged=" + computerManaged +
                '}';
    }
}
