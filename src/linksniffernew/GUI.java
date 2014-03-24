/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package linksniffernew;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Chase Willden
 */
public class GUI extends Application{
    
    private final LinkSnifferNew ls = new LinkSnifferNew();
    private List allCourses = new ArrayList();
    private String courseid = "";
    
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        startLogin(primaryStage);
    }
    
    public void startLogin(final Stage primaryStage){
        primaryStage.setTitle("Link Sniffer Login");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Brainhoney Login");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        final TextField username = new TextField();
        username.setPromptText("username");
        grid.add(username, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        final PasswordField password = new PasswordField();
        password.setPromptText("Password");
        grid.add(password, 1, 2);
        
        Label pr = new Label("Prefix:");
        grid.add(pr, 0, 3);
        
        final TextField prefix = new TextField();
        prefix.setPromptText("i.e. byui");
        grid.add(prefix, 1, 3);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if (ls.login(username.getText(), password.getText(), prefix.getText())){
                    System.out.println("Logged in!");
                    domainScreen(primaryStage);
                }
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void domainScreen(final Stage primaryStage){
        primaryStage.setTitle("Link Sniffer ~ Domainid");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Domain Selection");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label domainidlabel = new Label("Domain id: ");
        grid.add(domainidlabel, 0, 1);

        final TextField domainid = new TextField();
        domainid.setPromptText("1234567");
        grid.add(domainid, 1, 1);

        Button generate = new Button("Next");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(generate);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        
        generate.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if (ls.getDomainCourses(domainid.getText())){
                    allCourses = ls.getAllCourses();
                    selectCourseScreen(primaryStage);
                }
            }
        });
        
        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    public void selectCourseScreen(final Stage primaryStage){
        primaryStage.setTitle("Link Sniffer ~ select course");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Select Course");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        final ListView<String> list = new ListView<String>();
        ObservableList<String> items =FXCollections.observableArrayList (ls.getAllCourses());
        list.setItems(items);
        list.setPrefWidth(300);
        grid.add(list, 0, 2);

        Button generate = new Button("Next");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(generate);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        
        generate.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                actiontarget.setText("It worked");
                int idx = list.getSelectionModel().getSelectedIndex();
                courseid = ls.getAllCourses().get(idx).split("::")[1];
                audit(primaryStage);
            }
        });
        
        Scene scene = new Scene(grid, 300, 325);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void audit(final Stage primaryStage){
        primaryStage.setTitle("Link Sniffer ~ Audit");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Please wait...");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        
        Label display = new Label();
        grid.add(display, 0, 1);
        display.setMinWidth(300);
        
        Scene scene = new Scene(grid, 300, 335);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        ls.dlapGetItemList(this.courseid);
        ls.run();
        display.setText(ls.displayBrokenLinks());
    }
}
