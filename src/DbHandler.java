package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

public class DbHandler {

    // Константа, в которой хранится адрес подключения
    private static final String CON_STR = "jdbc:sqlite:C:/Users/Anastasia/Databaselogistics/dtt.db";

    // Используем шаблон одиночка, чтобы не плодить множество
    // экземпляров класса DbHandler
    private static DbHandler instance = null;

    public static synchronized DbHandler getInstance() throws SQLException {
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }

    // Объект, в котором будет храниться соединение с БД
    private Connection connection;

    private DbHandler() throws SQLException {
        // Регистрируем драйвер, с которым будем работать
        // в нашем случае Sqlite
        DriverManager.registerDriver(new JDBC());
        // Выполняем подключение к базе данных
        this.connection = DriverManager.getConnection(CON_STR);
    }

    public List<Product> getAllProducts() {

        // Statement используется для того, чтобы выполнить sql-запрос
        try (Statement statement = this.connection.createStatement()) {
            // В данный список будем загружать наши продукты, полученные из БД
            List<Product> products = new ArrayList<Product>();
            // В resultSet будет храниться результат нашего запроса,
            // который выполняется командой statement.executeQuery()
            ResultSet resultSet = statement.executeQuery("SELECT id, group_of_products, name, shelf_life, price FROM products");
            // Проходимся по нашему resultSet и заносим данные в products
            while (resultSet.next()) {
                products.add(new Product(resultSet.getInt("id"),
                        resultSet.getString("group_of_products"),
                        resultSet.getString("name"),
                        resultSet.getInt("shelf_life"),
                        resultSet.getDouble("price")));
            }
            // Возвращаем наш список
            return products;

        } catch (SQLException e) {
            e.printStackTrace();
            // Если произошла ошибка - возвращаем пустую коллекцию
            return Collections.emptyList();
        }
    }
    public List<Product> getGroupsProducts() {
        try (Statement statement = this.connection.createStatement()) {
            List<Product> groups = new ArrayList<Product>();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT group_of_products FROM products");
            while (resultSet.next()) {
                groups.add(new Product(0,
                        resultSet.getString("group_of_products"),
                        "",
                        0,
                        0));
            }
            return groups;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Product> getOneGroup(Button chosenGroup){
        try{
            List<Product> groups = new ArrayList<Product>();
            String sql = "SELECT name FROM products where group_of_products = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, chosenGroup.getText());

            ResultSet resultSet = ps.executeQuery();

          while (resultSet.next()) {
              groups.add(new Product(0,
                      "",
                      resultSet.getString("name"),
                      0,
                      0));
          }
          return groups;
      }
      catch (SQLException e) {
          e.printStackTrace();
          return Collections.emptyList();
      }

    }

    public List<InfoAboutProduct> getPartions(String chosenCell) {
        try {
            List<InfoAboutProduct> partions = new ArrayList<InfoAboutProduct>();
            String sql = "Select new_income.дата_производства as партия_от, COALESCE(iquantity-oquantity, iquantity) as остаток_товара\n" +
                    "From (\n" +
                    "Select id, дата_производства, Sum(количество) as iquantity\n" +
                    "From income\n" +
                    "Where дата<= '2021-06-13'\n" +
                    "Group by id, дата_производства) as new_income\n" +
                    "Left Join (\n" +
                    "Select id, дата_производства, Count(дата) as oquantity\n" +
                    "From outcome\n" +
                    "Where дата<= '2021-06-13'\n" +
                    "Group by id, дата_производства) as new_outcome\n" +
                    "On new_outcome.дата_производства=new_income.дата_производства and new_outcome.id=new_income.id\n" +
                    "Join products\n" +
                    "On new_income.id=products.id\n" +
                    "Where julianday('2021-06-13')-julianday(new_income.дата_производства)<=products.shelf_life \n" +
                    "and остаток_товара>0 and products.name = ? ";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, chosenCell.toString());

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                partions.add(new InfoAboutProduct(resultSet.getString("партия_от"),
                        resultSet.getInt("остаток_товара")));
            }
            // Возвращаем наш список
            return partions;

        } catch (SQLException e) {
            e.printStackTrace();
            // Если произошла ошибка - возвращаем пустую коллекцию
            return Collections.emptyList();
        }

    }

 /*   public int countPartions(String productName){
        try{
        String sql = "Select count(дата_производства) as количество_различных_партий\n" +
                "from(\n" +
                "Select new_income.id, new_income.дата_производства, COALESCE(iquantity-oquantity, iquantity) as остаток_товара\n" +
                "From (\n" +
                "Select id, дата_производства, Sum(количество) as iquantity\n" +
                "From income\n" +
                "Where дата<= '2021-01-13'\n" +
                "Group by id, дата_производства) as new_income\n" +
                "Left Join (\n" +
                "Select id, дата_производства, Count(дата) as oquantity\n" +
                "From outcome\n" +
                "Where дата<= '2021-01-13'\n" +
                "Group by id, дата_производства) as new_outcome\n" +
                "On new_outcome.дата_производства=new_income.дата_производства and new_outcome.id=new_income.id\n" +
                "Join products\n" +
                "On new_income.id=products.id\n" +
                "Where julianday('2021-01-13')-julianday(new_income.дата_производства)<=products.shelf_life \n" +
                "and остаток_товара>0 and products.name = ? ) as a";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, productName);
            ResultSet resultSet = ps.executeQuery();
            List<Integer> quantityOfPartionsList = new ArrayList<Integer>();
            int quantityOfPartions = 0;
            while (resultSet.next()){
              quantityOfPartionsList.add(resultSet.getInt("количество_различных_партий"));
              quantityOfPartions = quantityOfPartionsList.get(0);
            }
        return quantityOfPartions;
        }
        catch (SQLException e) {
            e.printStackTrace();
            int i = 0;
            return  i;
        }
    }*/

    public List<String> getDatesOfProduction(String productName){ //метод для получения списка дат пр-ва партий конкретного товара(для начисления скидок)
        try {
            String sql = "Select дата_производства\n" +
                    "from(\n" +
                    "Select new_income.дата_производства as дата_производства, COALESCE(iquantity-oquantity, iquantity) as остаток_товара\n" +
                    "From (\n" +
                    "Select id, дата_производства, Sum(количество) as iquantity\n" +
                    "From income\n" +
                    "Where дата<= '2021-06-13'\n" +
                    "Group by id, дата_производства) as new_income\n" +
                    "Left Join (\n" +
                    "Select id, дата_производства, Count(дата) as oquantity\n" +
                    "From outcome\n" +
                    "Where дата<= '2021-06-13'\n" +
                    "Group by id, дата_производства) as new_outcome\n" +
                    "On new_outcome.дата_производства=new_income.дата_производства and new_outcome.id=new_income.id\n" +
                    "Join products\n" +
                    "On new_income.id=products.id\n" +
                    "Where julianday('2021-06-13')-julianday(new_income.дата_производства)<=products.shelf_life \n" +
                    "and остаток_товара>0 and products.name = ?)\n"+
                    "order by дата_производства desc";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, productName);
            ResultSet resultSet = ps.executeQuery();
            List<String> datesOfProduction = new ArrayList<>();
            while (resultSet.next()){
                datesOfProduction.add(resultSet.getString("дата_производства"));
            }

            return datesOfProduction;
        }catch (SQLException e) {
            e.printStackTrace();
            // Если произошла ошибка - возвращаем пустую коллекцию
            return Collections.emptyList();
        }

    }

    public List<Product> getPerishableProducts(int shelfLife){
        try{
            List<Product> perishables = new ArrayList<Product>();
            String sql = "SELECT name\n" +
                    "  FROM products\n" +
                    "  where shelf_life<=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, String.valueOf(shelfLife));

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                perishables.add(new Product(0,
                        "",
                        resultSet.getString("name"),
                        0,
                        0));
            }
            return perishables;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

    public List<Product> getNonPerishableProducts(int shelfLife){
        try{
            List<Product> perishables = new ArrayList<Product>();
            String sql = "SELECT name\n" +
                    "  FROM products\n" +
                    "  where shelf_life > ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, String.valueOf(shelfLife));

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                perishables.add(new Product(0,
                        "",
                        resultSet.getString("name"),
                        0,
                        0));
            }
            return perishables;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

    public List<Product> expiringGoods(){
        try (Statement statement = this.connection.createStatement()) {
            List<Product> expGoods = new ArrayList<Product>();
            ResultSet resultSet = statement.executeQuery("Select products.name as наименование, new_income.дата_производства as партия_от, COALESCE(iquantity-oquantity, iquantity) as остаток_товара\n" +
                    " From (\n" +
                    "  Select id, дата_производства, Sum(количество) as iquantity\n" +
                    "  From income\n" +
                    "  Group by id, дата_производства) as new_income\n" +
                    "  Left Join (\n" +
                    "  Select id, дата_производства, Count(дата) as oquantity\n" +
                    "  From outcome\n" +
                    "  Group by id, дата_производства) as new_outcome\n" +
                    "  On new_outcome.дата_производства=new_income.дата_производства and new_outcome.id=new_income.id\n" +
                    "  Join products\n" +
                    "  On new_income.id=products.id\n" +
                    "  Where products.shelf_life - (julianday(date('now'))-julianday(new_income.дата_производства)) = 1\n" +
                    "  and остаток_товара>0");
            while (resultSet.next()) {
                expGoods.add(new Product(0,
                        resultSet.getString("партия_от"),
                        resultSet.getString("наименование"),
                        resultSet.getInt("остаток_товара"),
                        0));
            }
            return expGoods;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void updateDiscount(int discount, String productName, String dateOfProduction){
        try (PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE income\n" +
                        "   SET скидка = ?\n" +
                        " WHERE \n" +
                        "       id = (Select distinct id From products where name = ?) AND \n" +
                        "       дата_производства = ?"
        )) {
            statement.setInt(1, discount);
            statement.setString(2, productName);
            statement.setString(3, dateOfProduction);

            // Выполняем запрос
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(e.toString());
            alert.show();

        }
    }


    // Добавление партии в БД
    public void addingProduct(AddProduct product, String productName) {
        // Создадим подготовленное выражение, чтобы избежать SQL-инъекций
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO income('дата', 'id', 'количество', 'дата_производства', 'скидка')\n" +
                        "                        VALUES(date('now'), (Select distinct id From products where name = ?), ?, ?, 0)"
        )) {
            statement.setObject(1, productName);
            statement.setObject(2, product.quantity);
            statement.setObject(3, product.dateOfProduction);

            // Выполняем запрос
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(e.toString());
            alert.show();

        }
    }

    // Удаление продукта по id
    public void deleteProduct(int id) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM Products WHERE id = ?")) {
            statement.setObject(1, id);
            // Выполняем запрос
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet StatysticsOfSelling(String productName){
        try {
            String sql = "SELECT дата,\n" +
                    "      count(id)\n" +
                    "  FROM outcome\n" +
                    "  where id = (Select distinct id From products where name = ?)\n" +
                    "  Group by дата" +
                    "   order by дата";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, productName);
            ResultSet resultSet = ps.executeQuery();


            return resultSet;
        }catch (SQLException e) {
            e.printStackTrace();
            // Если произошла ошибка - возвращаем пустую коллекцию
            return null;
        }
    }

    public ResultSet SalesPrediction (String productName){
        try {
            String sql = "SELECT datee,\n" +
                    "      quantity\n" +
                    "  FROM prediction\n" +
                    "  where id = (Select distinct id From products where name = ?)\n" +
                    "   order by datee";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, productName);
            ResultSet resultSet = ps.executeQuery();


            return resultSet;
        }catch (SQLException e) {
            e.printStackTrace();
            // Если произошла ошибка - возвращаем пустую коллекцию
            return null;
        }
    }
    public int IdFromName (String productName){
        try {
            String sql = "SELECT id\n" +
                    "  FROM products\n" +
                    "   where name = ?\n";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, productName);
            ResultSet resultSet = ps.executeQuery();
            List<Integer> ids = new ArrayList<>();
            int id = 0;
            while (resultSet.next()){
                ids.add(resultSet.getInt("id"));
                id = ids.get(0);
            }


            return id;
        }catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

}

