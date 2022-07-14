package add_product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sample.AddProduct;
import sample.Controller;
import sample.DbHandler;
import sample.Product;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerAddProducts extends Controller implements Initializable {
    public Controller mainController;

    public void setMainController(Controller old){
        mainController=old;
    }

    public TextField addQuantity;
    public TextField addDateOfProduction;
    public HBox hb;
    public Button btn1;


    private String getProductName() {
        Scene s = hb.getScene();
        Stage st = (Stage) s.getWindow();
        String productName = st.getTitle();
        String delete = "Новая партия товара: ";
        productName = productName.replace(delete, "");

        return productName;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addQuantity.setPromptText("ГГГГ-ММ-ДД");
        addDateOfProduction.setPromptText("Количество");
        hb.setSpacing(20.0);

    }

    public void Click(ActionEvent actionEvent) {
        String quantityStr = addQuantity.getText();
        String dateProdStr = addDateOfProduction.getText();
        AddProduct product = new AddProduct(quantityStr, dateProdStr);



        DbHandler dbHandler = null;
        try {
            dbHandler = DbHandler.getInstance();

            int quantityOfPartions = dbHandler.getDatesOfProduction(getProductName()).size();
            int discount=0;
            List<String> datesOfProduction = dbHandler.getDatesOfProduction(getProductName());
            int discountArr[];
            discountArr = new int[datesOfProduction.size()];
            for (int i=0; i<quantityOfPartions; i++){
                discount+=15;
                discountArr[i]=discount;
                dbHandler.updateDiscount(discount, getProductName(), datesOfProduction.get(i));
            }

            dbHandler.addingProduct(product, getProductName());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Товар "+ getProductName());
            alert.setContentText("Партия успешно добавлена");
            alert.showAndWait();
            Stage stage = (Stage) btn1.getScene().getWindow();
            stage.close();
            mainController.toDoBox(getProductName(), datesOfProduction, discountArr);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
