package sample;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.w3c.dom.Text;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;

public class Controller implements Initializable {
    @FXML
    private ListView<Integer> NumberListView;
    @FXML
    private Label RandomNumberLabel;
    @FXML
    private TextField MaxTextField;
    @FXML
    private TextField MinTextField;
    @FXML
    private Button runbutton;
    @FXML
    private Button loadbutton;
    @FXML
    private TextField RandomNumberTextField;

    final String DB_URL = "jdbc:derby:NumberDB;create=true";


    public void initialize(URL url, ResourceBundle resourceBundle) {

        try{
            Connection c = DriverManager.getConnection(DB_URL);
            Statement s = c.createStatement();
            try
            {
                s.execute("CREATE TABLE Numtbl (" +
                        "RandomNumber INTEGER)");
                System.out.println("TABLE CREATED");
            }
            catch (Exception ex)
            {
                System.out.println("TABLE ALREADY EXISTS, NOT CREATED");
            }
            s.close();
            c.close();
        }
        catch (Exception ex)
        {
            var msg = ex.getMessage();
            System.out.println(msg);
        }

        ObservableList<Integer> items = NumberListView.getItems();
        EventHandler<ActionEvent> add = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int min_num = Integer.parseInt(MinTextField.getText());
                int max_num = Integer.parseInt(MaxTextField.getText());

                Random r = new Random();
                int random = r.nextInt((max_num - min_num) + 1) + min_num; //https://mkyong.com/java/java-generate-random-integers-in-a-range/
                //items.add(random);

                String ran = Integer.toString(random);
                RandomNumberLabel.setText("Random Number: " + ran);

                try {
                    Connection c = DriverManager.getConnection(DB_URL);
                    Statement s = c.createStatement();
                    System.out.println(random);

                    String sql = "INSERT INTO Numtbl VALUES(" + random + ")";
                    s.executeUpdate(sql);
                    System.out.println("Successful!");
                }catch(Exception ex){
                    System.out.println("DATA NOT LOADED");
                    System.out.println(ex.getMessage());
                }
            }
        };
        runbutton.setOnAction(add);

        EventHandler<ActionEvent> load = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    Connection conn = DriverManager.getConnection(DB_URL);
                    Statement stmt = conn.createStatement();

                    String sqlStatement = "SELECT RandomNumber FROM Numtbl";
                    ResultSet result = stmt.executeQuery(sqlStatement);
                    ObservableList<Integer> dbNumberList = FXCollections.observableArrayList();
                    while (result.next())
                    {
                        Number num = new Number();
                        num.random = Integer.parseInt(result.getString("RandomNumber"));
                        dbNumberList.add(num.random);
                    }
                    NumberListView.setItems(dbNumberList);
                    try{
                        Connection c = DriverManager.getConnection(DB_URL);
                        Statement s = conn.createStatement();
                        s.execute("DROP TABLE Employee");
                        s.close();
                        c.close();
                        System.out.println("TABLE DROPPED");
                    }
                    catch (Exception ex)
                    {
                        var msg = ex.getMessage();
                        System.out.println("TABLE NOT DROPPED");
                        System.out.println(msg);
                    }


                    System.out.println("DATA LOADED");

                    stmt.close();
                    conn.close();
                }
                catch (Exception ex)
                {
                    var msg = ex.getMessage();
                    System.out.println("DATA NOT LOADED");
                    System.out.println(msg);
                }


            }
        };
        loadbutton.setOnAction(load);


    }


}
