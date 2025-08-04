import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter product data:");
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Price: ");
        double price = sc.nextDouble();
        System.out.print("Quantity: ");
        int quantity = sc.nextInt();

        Product product = new Product(name, price, quantity);
        
        System.out.println("Product data: " + product.toString());

        System.out.print("Enter the number of products to be added in stock: ");
        product.AddProduct(sc.nextInt());
        System.out.println("Updated data: " + product.toString());
        
        System.out.print("Enter the number of products to be removed from stock: ");
        product.RemoveProduct(sc.nextInt());

        System.out.println("Updated data: " + product.toString());

        sc.close();
    }
}
