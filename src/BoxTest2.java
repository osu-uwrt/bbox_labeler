import java.awt.Desktop;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public final class BoxTest2 {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        // Redirect user to login with Box
        String box_redirect = Config.box_redirect + "?response_type=code"
                + "&client_id=" + Config.client_id + "&redirect_uri="
                + Config.redirect_uri;

        if (Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                //Choose a port to listen to
                ServerSocket serverSocket = new ServerSocket(8080);
                System.out.println(
                        "Listening on port: " + serverSocket.getLocalPort());

                //Open a web browser with an address that lets the user log in
                //and grant authorization to the program
                Desktop.getDesktop().browse(new URI(Config.box_redirect
                        + "?response_type=code&client_id=" + Config.client_id
                        + "&redirect_uri=" + Config.redirect_uri + ":"
                        + serverSocket.getLocalPort() + "&state="
                        + Config.client_secret));

                //Start listening
                Socket socket = serverSocket.accept();
                System.out.print("Here");
                ObjectInputStream ois = new ObjectInputStream(
                        socket.getInputStream());
                String message = (String) ois.readObject();
                System.out.println("Message: " + message);

                //Stop listening on the ports
                ois.close();
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
