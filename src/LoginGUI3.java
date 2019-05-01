import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxUser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class LoginGUI3 extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage stage) {
        // Create the WebView
        WebView webView = new WebView();

        // Update the stage title when a new web page title is available
        webView.getEngine().titleProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov,
                            final String oldvalue, final String newvalue) {
                        // Set the Title of the Stage
                        stage.setTitle(newvalue);
                    }
                });

        webView.getEngine().locationProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov,
                            final String oldvalue, final String newvalue) {
                        // Print out the new URL
                        System.out.println(newvalue);
                        //Get the code
                        String newValue = newvalue;
                        int startIndex = newValue.indexOf("code=");
                        if (startIndex >= 0) {
                            newValue = newValue.substring(startIndex + 5);
                            System.out.println("Code: " + newValue);
                            //find out if there are other fields given
                            //get rid of them if there are
                            int endIndex = newValue.indexOf("&");
                            if (endIndex >= 0) {
                                newValue = newValue.substring(0, endIndex);
                            }
                            //Get tokens and API class
                            BoxAPIConnection api = new BoxAPIConnection(
                                    Config.client_id, Config.client_secret,
                                    newValue);

                            BoxUser.Info userInfo = BoxUser.getCurrentUser(api)
                                    .getInfo();
                            System.out.format("Welcome, %s <%s>!\n\n",
                                    userInfo.getName(), userInfo.getLogin());

                            BoxFolder rootFolder = BoxFolder.getRootFolder(api);
                            listFolder(rootFolder, 0);
                            //Open the next GUI and close this one
                            /*
                             * Create instances of the model, view, and
                             * controller objects, and initialize them; view
                             * needs to know about controller, and controller
                             * needs to know about model and view
                             */
                            SessionTypeModel model = new SessionTypeModel1(api);
                            SessionTypeView view = new SessionTypeView1();
                            SessionTypeController controller = new SessionTypeController1(
                                    model, view);
                            view.registerObserver(controller);

                            Platform.exit();
                        }
                    }
                });

        // Load the Google web page
        String homePageUrl = Config.box_redirect
                + "?response_type=code&client_id=" + Config.client_id
                + "&redirect_uri=" + Config.redirect_uri + "&state="
                + Config.client_secret;

        // Create the Navigation Bar
        NavigationBar navigationBar = new NavigationBar(webView, homePageUrl,
                true);

        // Create the VBox
        VBox root = new VBox(navigationBar, webView);

        // Set the Style-properties of the VBox
        root.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

        // Create the Scene
        Scene scene = new Scene(root);
        // Add the Scene to the Stage
        stage.setScene(scene);
        // Display the Stage
        stage.show();
    }

    private static void listFolder(BoxFolder folder, int depth) {
        int MAX_DEPTH = 1;
        for (BoxItem.Info itemInfo : folder) {
            String indent = "";
            for (int i = 0; i < depth; i++) {
                indent += "    ";
            }

            System.out.println(indent + itemInfo.getName());
            if (itemInfo instanceof BoxFolder.Info) {
                BoxFolder childFolder = (BoxFolder) itemInfo.getResource();
                if (depth < MAX_DEPTH) {
                    listFolder(childFolder, depth + 1);
                }
            }
        }
    }
}
