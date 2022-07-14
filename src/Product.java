package sample;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class Product {
        // Поля класса
        public int id;

        public String group_of_products;

        public String name;

        public int shelf_life;

        public double price;

        // Конструктор
        public Product(int id, String group_of_products, String name, int shelf_life, double price) {
            this.id = id;
            this.group_of_products = group_of_products;
            this.name = name;
            this.shelf_life = shelf_life;
            this.price = price;
        }



    // Выводим информацию по продукту
        @Override
        public String toString() {
            return String.format("ID: %s | Товарная группа: %s | Наименование: %s | Срок годности: %s | Цена: %s",
                    this.id, this.group_of_products, this.name, this.shelf_life, this.price);
        }
    }
