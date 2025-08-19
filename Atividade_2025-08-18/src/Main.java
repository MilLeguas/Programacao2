import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in).useLocale(Locale.US);
        List<Person> persons = new ArrayList<>();
        char letter;
        String name; 
        double income = 0;
        int quantityEmp;
        double healthExp;
        int i;
        int count;
        System.out.print("Enter the number of tax payers: ");
        count = sc.nextInt();


        for(i = 1; i <= count; i++){
            sc.nextLine();
            System.out.printf("Tax Payer #%d data:\n", i);
            System.out.print("Individual or Company: ");
            letter = sc.nextLine().charAt(0);
            if(letter != 'c' && letter != 'i') {
                System.out.printf("ERROR!\n");
                sc.close();
                return;
            }
            System.out.print("Name: ");
            name = sc.nextLine();
            System.out.print("Anual Income: ");
            income = sc.nextDouble();
            
            if(letter == 'i'){
                System.out.print("Health expenditures: ");
                healthExp = sc.nextDouble();
                
                persons.add(new Pf(name, income, healthExp));
            }else if (letter == 'c'){
                System.out.print("Number of employees ");
                quantityEmp = sc.nextInt();
                
                persons.add(new Pj(name, income, quantityEmp));
            }
            else{
                System.out.println("ERROR!");
            }
        }

        System.out.println("\nTaxes Paid:");
        double sumTax = 0;
        for(Person person : persons){
            System.out.print(person);
            sumTax += person.calcTax();
        }

        System.out.printf("\nTOTAL TAXES: $ %.2f\n", sumTax);

        
        sc.close();
    }
}
