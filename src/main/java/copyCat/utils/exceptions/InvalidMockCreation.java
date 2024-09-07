package copyCat.utils.exceptions;

public class InvalidMockCreation extends Exception{

    public InvalidMockCreation() {
        super("Invalid ApiMock creation was failed");
    }

    public InvalidMockCreation(String message) {
        super(message);
    }
}
