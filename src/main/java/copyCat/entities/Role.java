package copyCat.entities;

public enum Role {
    SERVER("server"),
    CLIENT("client");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
