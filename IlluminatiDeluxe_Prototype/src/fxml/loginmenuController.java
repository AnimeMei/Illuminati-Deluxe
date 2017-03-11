package fxml;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.UUID;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import Firebase.AccessFirebase; //using method from another package
import fxml.splashController.SplashScreen;

import Firebase.User;


//public class loginmenuController implements Runnable  {
public class loginmenuController{// extends User {
	
//    static User currentUser = new User();

    Stage popupStage = new Stage(StageStyle.TRANSPARENT);

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private AnchorPane rootloginpane;
	
    @FXML
    private AnchorPane signinrightpane;

    @FXML
    private Button playBtn;

    @FXML
    private AnchorPane signinleftpane;
    
    @FXML
    void toCreateMenu_ACTION(MouseEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
		    	toCreateAcctMenu();
			}	
		});
    }

    @FXML
    void playBtn_action(ActionEvent event) {
    	loading();
    	// Get a reference to the database
		final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
		final DatabaseReference ref = rootRef.child("profile");

		// Attach a listener to read the data at our posts reference
		//specifically query this user's name
		ref.orderByChild("name").equalTo(usernameField.getText().toLowerCase()).addValueEventListener(new ValueEventListener() {

		    @Override
		    public void onDataChange(DataSnapshot dataSnapshot) {	
		    	//System.out.println(dataSnapshot);
		    	//iterate each node to get the children value
		    	for (DataSnapshot messageSnapshot:dataSnapshot.getChildren()){		    		
		    		//parse DataSnapshot to String
		    		String dataPassword = (String)messageSnapshot.child("password").getValue();
		    		//validate password 
		    		if (dataPassword.equals(passwordField.getText())){
		    			System.out.println("User found.");
		    			ref.removeEventListener(this); //disconnect the network listener
		    			
		    			//using runlater because in javaFX only the FX thread can modify the ui elements 
						Platform.runLater(new Runnable() {
							@Override
							public void run() {

								User currentUser = User.getInstance();
								currentUser.setUID(messageSnapshot.getKey());
								rootloginpane.setEffect(null);
								popupStage.hide();
								login();	//switch to another playersreen
							}	
						});
						
		    		} else {
		    			Platform.runLater(new Runnable() {
							@Override
							public void run() {
								takenAlert();
							}	
						});
		    		}
		    	}
		    }

		    @Override
		    public void onCancelled(DatabaseError databaseError) {
		        System.out.println("The read failed: " + databaseError.getCode());
		    }
		});

    }
    
    public void login(){
    	BorderPane pane = null;
    	try {
    		pane = FXMLLoader.load(getClass().getResource("playerScreenFX.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	//override the previouse scene content with current border pane's content
    	rootloginpane.getChildren().setAll(pane); 
    }
    
    public void toCreateAcctMenu(){
    	AnchorPane apane = null;
    	try {
    		apane = FXMLLoader.load(getClass().getResource("createmenuFX.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	//override the previouse scene content with current border pane's content
    	rootloginpane.getChildren().setAll(apane);     	
    }
    
    private RotateTransition createAnimation(ImageView imageView) {
    	RotateTransition animation = new RotateTransition(Duration.seconds(0.5), imageView);
        animation.setByAngle(90);
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setAutoReverse(true);
        animation.play();
        return animation;
    }
    
    private void loading(){
//      Rectangle rect = new Rectangle(50, 50, 50, 50);
//      rect.setFill(Color.CORAL);
//      RotateTransition animation = createAnimation(rect);
      
  	String path = new File("appicon.png").getAbsolutePath();
		Image image = new Image(new File(path).toURI().toString());
      
		ImageView imageView = new ImageView(image);
      RotateTransition animation = createAnimation(imageView);

      Pane pane = new Pane(imageView);
//      pane.setMinSize(100, 100);

      rootloginpane.setEffect(new GaussianBlur());

      VBox pauseRoot = new VBox(5);
      pauseRoot.getChildren().add(new Label("Loading"));
      pauseRoot.getChildren().add(pane);

      pauseRoot.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");
      pauseRoot.setAlignment(Pos.CENTER);
      pauseRoot.setPadding(new Insets(20));

      Stage primaryStage = (Stage) rootloginpane.getScene().getWindow();
      popupStage.initOwner(primaryStage);
      popupStage.initModality(Modality.APPLICATION_MODAL);
      popupStage.setScene(new Scene(pauseRoot, Color.TRANSPARENT));
      popupStage.show();
    }
   
    
    public void takenAlert(){
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Alert");
    	alert.setHeaderText("Password incorrect");
    	alert.setContentText("Username and password do not match.");
    	alert.showAndWait();
    }

 }

