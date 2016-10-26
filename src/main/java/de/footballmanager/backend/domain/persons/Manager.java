package de.footballmanager.backend.domain.persons;

public class Manager extends Person {

    private boolean computerManaged = true;

    public boolean isComputerManaged() {
        return computerManaged;
    }

    public void setComputerManaged(boolean computerManaged) {
        this.computerManaged = computerManaged;
    }

    @Override
    public String toString() {
        return "Manager{" +
                ", computerManaged=" + computerManaged +
                '}';
    }
}
