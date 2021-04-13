package sample;


public class AddProduct {
    public String dateOfProduction;
    public String quantity;

    public AddProduct(String dateOfProduction, String quantity) {
        this.dateOfProduction = dateOfProduction;
        this.quantity = quantity;
    }

  public void setDateOfProduction(String dateOfProduction){
        this.dateOfProduction = dateOfProduction;
  }

    public void setQuantity(String quantity){
        this.quantity = quantity;
    }

    public String getDateOfProduction() {
        return dateOfProduction;
    }

    public String getQuantity() {
        return quantity;
    }
}
