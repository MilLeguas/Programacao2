import java.util.Locale;

public class Product {
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName(){
        return this.name;
    }
    
    public double getPrice(){
        return this.price;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public double TotalValueinStock(){
        return this.quantity * this.price;
    }
    
    public void AddProduct(int quantity){
        this.quantity += quantity;
    }

    public void RemoveProduct(int quantity){
        if(quantity > this.quantity){
            System.out.println("Não foi possível remover");
            return;
} 
        this.quantity -= quantity;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s, $ %.2f, %d units, Total: $ %.2f\n",
        this.name, this.price, this.quantity, TotalValueinStock());
    }
}