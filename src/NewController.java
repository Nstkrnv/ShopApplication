package info_about_product;
//import java.lang.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sample.Controller;
import sample.DbHandler;
import sample.InfoAboutProduct;
import sample.Product;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static java.lang.String.valueOf;

public class NewController {
    private ObservableList<InfoAboutProduct> productInfo = FXCollections.observableArrayList();

    @FXML
    private TableView<InfoAboutProduct> tab1;
    @FXML
    private TableColumn<InfoAboutProduct, String> dateProductionColumn;
    @FXML
    private TableColumn<InfoAboutProduct, Integer> quantityColumn;

    private String getChosenCell() {
        Scene chose = tab1.getScene();
        Stage chosenCell = (Stage) chose.getWindow();
        String chose2 = chosenCell.getTitle();
        String delete = "Информация о товаре: ";
        chose2 = chose2.replace(delete, "");

        return chose2;
    }

    // инициализируем форму данными
    public void initializeTable() {

        dateProductionColumn.setCellValueFactory(new PropertyValueFactory<InfoAboutProduct, String>("dateOfProduction"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<InfoAboutProduct, Integer>("quantity"));

        try {
            DbHandler dbHandler = DbHandler.getInstance();
            productInfo = FXCollections.observableArrayList( dbHandler.getPartions(getChosenCell()));
            tab1.setItems(productInfo);
        }
        catch (SQLException e) {
              e.printStackTrace();
        }
    }
}
