package client;
import java.io.*;
import java.net.Socket;

public class ClientBean{
    private String clientName;
    private PrintWriter out;
    private Socket s;


    public ClientBean(String clientName,PrintWriter out,Socket s) {
        this.clientName=clientName;
        this.out=out;
        this.s=s;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public void setS(Socket s) {
        this.s = s;
    }

    public String getClientName() {
        return clientName;
    }

    public Socket getS() {
        return s;
    }

    public String toString()
    {
        return clientName;
    }
}
