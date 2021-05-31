package utils.database;

public class RequestParameter {
    public String name;
    public String[] value;

    public RequestParameter(String name, String... value) {
        this.name = name;
        this.value = value;
    }
}
