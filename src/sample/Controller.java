package sample;

import add_product.ControllerAddProducts;
import info_about_product.NewController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {

    public HBox buttons;


    public static class HBoxCell extends HBox {
        Label label = new Label();
        Button button = new Button();

        HBoxCell(String labelText, Controller contr) {
            super();
            label.setText(labelText);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);
            button.setText("Добавить");
            button.setOnAction(actionEvent -> {
                String chosenProduct = labelText;
                try {
                    DbHandler dbHandler = DbHandler.getInstance();

                    List<Product> mproducts0;
                    mproducts0 = dbHandler.getAllProducts();
                    for(int i=0; i<mproducts0.size(); i++) {

                        if (chosenProduct.equals(mproducts0.get(i).name)){
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getResource("add_product.fxml"));

                            try {
                                Scene scene = new Scene(loader.load(), 450, 100);
                                Stage stage = new Stage();
                                stage.setScene(scene);
                                stage.setTitle("Новая партия товара: " + mproducts0.get(i).name );

                                ((ControllerAddProducts)loader.getController()).setMainController(contr);
                                stage.show();
                            }
                            catch (IOException e) {
                                Logger logger = Logger.getLogger(getClass().getName());
                                logger.log(Level.SEVERE, "Failed to create new Window.", e);
                            }
                            break;

                        }

                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            });

            this.getChildren().addAll(label, button);
        }
    }

    public static class HboxCell2 extends HBox{
        Label label = new Label();
        Button button = new Button();
        HboxCell2(String labelText){
            super();
            label.setText(labelText);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);
            button.setText("Готово");
            button.setOnAction(actionEvent -> {
                this.getChildren().removeAll(label, button);
            });
            this.getChildren().addAll(label, button);
        }
    }
    public ListView<HBoxCell> listv1;
    public ListView<String> listv2;
    public ListView <HboxCell2> listv3;

    public LineChart<Number, Number> ch;
    public NumberAxis x;


    List<Product> mproducts; // используются в нескольких методах

    List<HboxCell2> listForNextInvocation = new ArrayList<>();   // это чтобы каждый раз при вызове "добавить" список дел не стирался. Исп. в двух методах



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        try {
            // Создаем экземпляр по работе с БД
            DbHandler dbHandler = DbHandler.getInstance();
            // Добавляем запись
            // Получаем все записи
            mproducts = dbHandler.getAllProducts();


            List<HBoxCell> list2 = new ArrayList<>();
            List<String> listForT2 = new ArrayList<>();
            for (int i = 0; i < mproducts.size(); i++) {
                list2.add(new HBoxCell(mproducts.get(i).name, this));
                listForT2.add(mproducts.get(i).name);
            }

            ObservableList<HBoxCell> obsProducts = FXCollections.observableList(list2);
            listv1.setItems(obsProducts);
            listv2.setItems(FXCollections.observableList(listForT2));


           List<Product> mgroups = dbHandler.getGroupsProducts();
          Button [] Headings = new Button [mgroups.size()];

            for(int i=0; i<mgroups.size(); i++) {
                final int a = i;
                Headings[i] = new Button(mgroups.get(i).group_of_products);

                Headings[i].setOnAction(actionEvent -> {
                    Button chosenGroup = Headings[a];
                    List<Product> groups = dbHandler.getOneGroup(chosenGroup);
                    List <HBoxCell> list3 = new ArrayList<>();

                    for (int j = 0; j < groups.size(); j++) {
                        list3.add(new HBoxCell(groups.get(j).name, this));
                    }

                    ObservableList<HBoxCell> obsButtons2 = FXCollections.observableList(list3);
                    listv1.setItems(obsButtons2);


                  });
            }


            buttons.getChildren().addAll(Headings);
            buttons.setAlignment(Pos.CENTER);


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void toDoBox(String productName, List<String> datesOfProduction, int [] discounts){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация для мерчандайзера");
        alert.setHeaderText("Необходимо начислить скидки на товар " + productName);
        StringBuilder text = new StringBuilder("");
        List<String> ls = new ArrayList<>();
        for (int i = 0; i<discounts.length; i++) {
            text.append("На партию от " + datesOfProduction.get(i) + " в размере " + discounts[i] + " %\n");
            ls.add("на партию от " + datesOfProduction.get(i) + " в размере " + discounts[i] + " %");
        }
        alert.setContentText(String.valueOf(text));
        alert.show();

        List<HboxCell2> list2 = new ArrayList<>();

            for (int i = 0; i < ls.size(); i++) {
                list2.add(new HboxCell2("Начислить скидку на товар " + productName+ ": " + ls.get(i) + "\n"));
            }

            listForNextInvocation.addAll(list2);
            ObservableList<HboxCell2> obsDiscounts = FXCollections.observableList(listForNextInvocation);

            listv3.setItems(obsDiscounts);
    }


    public void Clicklist(MouseEvent mouseEvent) {
        HBoxCell chosenCell = listv1.getSelectionModel().getSelectedItem();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("info_about_product.fxml"));

                try {
                    Scene scene = new Scene(loader.load(), 320, 200);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setTitle("Информация о товаре: " + chosenCell.label.getText() );
                    stage.setOnShown(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            ((NewController)loader.getController()).initializeTable();
                        }
                    });
                    stage.show();
                }
                catch (IOException e) {
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "Failed to create new Window.", e);
                }

    }

    long z=-1;

    public void Clicklist2(MouseEvent mouseEvent) {
                String chosenCell = listv2.getSelectionModel().getSelectedItem();
                try {

                    DbHandler dbHandler = DbHandler.getInstance();
                    ResultSet statystics = dbHandler.StatysticsOfSelling(chosenCell);


                    ch.setTitle("Статистика продаж");
                    XYChart.Series series1 = new XYChart.Series();
                    series1.getData().removeAll(Collections.singleton(ch.getData().setAll()));
                    series1.setName("продажи товара " + chosenCell);
                    ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();

                    x.setTickUnit(1000 * 60 * 60 * 24);


                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                     Date prefDate = format.parse(statystics.getString ("дата"));
                     final long pd = prefDate.getTime();

                    x.setTickLabelFormatter(new StringConverter<Number>() {
                        @Override
                        public String toString(Number object) {
                            return new SimpleDateFormat("yyyy.MM.dd").format(new Date(object.intValue()*(1000 * 60 * 60 * 24) + pd));
                        }

                        @Override
                        public Number fromString(String string) {
                            return 0;
                        }
                    });

                    z=-1;
                    while (statystics.next()){
                        Date curDate = format.parse(statystics.getString ("дата"));
                        while(curDate.compareTo(prefDate)!=0) {
                            z+=1;
                            datas.add(new XYChart.Data(z, 0));
                            prefDate = new Date(prefDate.getTime() + (1000 * 60 * 60 * 24));
                        }
                        z+=1;

                        datas.add(new XYChart.Data(z, statystics.getInt("count(id)")));

                    }


                    series1.setData(datas);
                    ch.getData().add(series1);
                } catch (SQLException | ParseException e) {
                    e.printStackTrace();
                }

    }

    public void ClickPrediction(ActionEvent actionEvent) {
        try {
            String chosenCell = listv2.getSelectionModel().getSelectedItem();

            DbHandler dbHandler = DbHandler.getInstance();

            int id = dbHandler.IdFromName(chosenCell);

            PythonLauncher.Pyth(id);

            ResultSet prediction;
            prediction = dbHandler.SalesPrediction(chosenCell);

            XYChart.Series series2 = new XYChart.Series();

            ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();



            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date prefDate = format.parse(prediction.getString ("datee"));

            while (prediction.next()){

                z+=1;

                datas.add(new XYChart.Data(z, prediction.getInt("quantity")));
            }

            series2.setData(datas);
            ch.getData().add(series2);
        }catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }


    public void ClickAllProducts(ActionEvent actionEvent) {
        try {
            // Создаем экземпляр по работе с БД
            DbHandler dbHandler = DbHandler.getInstance();
            // Добавляем запись
            // Получаем все записи и выводим их на консоль
            mproducts = dbHandler.getAllProducts();


            List<HBoxCell> list2 = new ArrayList<>();
            for (int i = 0; i < mproducts.size(); i++) {
                list2.add(new HBoxCell(mproducts.get(i).name, this));
            }

            ObservableList<HBoxCell> obsButtons = FXCollections.observableList(list2);
            listv1.setItems(obsButtons);

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public TextField shelfLifetxt;
    public int ClickShelfLife(ActionEvent actionEvent) {
        String shelfLifeStr = shelfLifetxt.getText();
        return Integer.parseInt(shelfLifeStr);
    }

    public void ClickPerishable(ActionEvent actionEvent) {
        try {
            DbHandler dbHandler = DbHandler.getInstance();
            mproducts=dbHandler.getPerishableProducts(ClickShelfLife(actionEvent));
            List<HBoxCell> list2 = new ArrayList<>();
            for (int i = 0; i < mproducts.size(); i++) {
                list2.add(new HBoxCell(mproducts.get(i).name, this));
            }

            ObservableList<HBoxCell> obsButtons = FXCollections.observableList(list2);
            listv1.setItems(obsButtons);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ClickNonPerishable(ActionEvent actionEvent) {
        try {
            DbHandler dbHandler = DbHandler.getInstance();
            mproducts=dbHandler.getNonPerishableProducts(ClickShelfLife(actionEvent));
            List<HBoxCell> list2 = new ArrayList<>();
            for (int i = 0; i < mproducts.size(); i++) {
                list2.add(new HBoxCell(mproducts.get(i).name, this));
            }

            ObservableList<HBoxCell> obsButtons = FXCollections.observableList(list2);
            listv1.setItems(obsButtons);

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TextField hourstxt;
    public TextField minutestxt;
    public void ClickChooseTime(ActionEvent actionEvent1) {
        String hoursStr = hourstxt.getText();
        String minutesStr = minutestxt.getText();


        java.util.Timer timer = new java.util.Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        try {
                            DbHandler dbHandler = DbHandler.getInstance();

                            Alert alrt = new Alert(Alert.AlertType.INFORMATION);
                            alrt.setHeaderText("Завтра истечёт срок годности следующих товаров: ");

                            List<Product> expGoods = dbHandler.expiringGoods();

                            if (expGoods.size()!=0) {
                                StringBuilder text = new StringBuilder("");
                                List<String> ls = new ArrayList<>();
                                for (int i = 0; i < expGoods.size(); i++) {
                                    text.append(expGoods.get(i).name + ". Дата производства: " + expGoods.get(i).group_of_products + ". Осталось единиц: " + expGoods.get(i).shelf_life + "\n");
                                    ls.add("Необходимо утилизировать товар " + expGoods.get(i).name + ". Дата производства: " + expGoods.get(i).group_of_products + ". Осталось единиц: " + expGoods.get(i).shelf_life);
                                }
                                alrt.setContentText(String.valueOf(text));
                                alrt.show();

                                List<HboxCell2> list2 = new ArrayList<>();

                                for (int i = 0; i < ls.size(); i++) {
                                    list2.add(new HboxCell2(ls.get(i)));
                                }

                                listForNextInvocation.addAll(list2);
                                ObservableList<HboxCell2> obsDiscounts = FXCollections.observableList(listForNextInvocation);

                                listv3.setItems(obsDiscounts);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        };
        Date curdate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(curdate);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hoursStr));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minutesStr));
        calendar.set(Calendar.SECOND, 0);
        curdate = calendar.getTime();
        timer.schedule(timerTask, curdate, 86400000);

    }


}
