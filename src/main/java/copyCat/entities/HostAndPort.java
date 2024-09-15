package copyCat.entities;

public record HostAndPort(String host, int port){

    public String toString(){
        return host + ":" + port;
    }
}
