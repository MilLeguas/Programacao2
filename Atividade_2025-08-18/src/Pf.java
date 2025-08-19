public class Pf extends Person{
    private double healthExp;


    public Pf (String name, double income, double healthExp){
        super(name, income);
        this.healthExp = healthExp; 
    }

    private void setHealthExp(double healthExp){
        if(healthExp >= 0){
            this.healthExp = healthExp;
        }
        else{
            System.out.println("ERROR!");
        }
    }

    private double getHealthExp(){
        return healthExp;
    }

    @Override
    public double calcTax(){
        double baseIncome = getIncome();
        double tax = 0;

        if(baseIncome < 20000){
            tax = baseIncome * 0.15;
        }else{
            tax = baseIncome * 0.25;
        }

        tax -= healthExp * 0.5;
        
        if(tax < 0) return 0.0;
        
        return tax;
    }
}
