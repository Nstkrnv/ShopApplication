package sample;

public class InfoAboutProduct {
    public String dateOfProduction;
    public int quantity;

    public InfoAboutProduct(String dateOfProduction, int quantity) {
        this.dateOfProduction = dateOfProduction;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDateOfProduction() {
        return dateOfProduction;
    }

    @Override
    public String toString() {
        return String.format("Дата производства: %s | количество: %s ",
                this.dateOfProduction, this.quantity);
    }
}
