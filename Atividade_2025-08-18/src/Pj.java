public class Pj extends Person {
    private int quantityEmp;

    public Pj(String name, double income, int quantityEmp){
        super(name, income);
        setQuantityEmp(quantityEmp);
    }

    private void setQuantityEmp(int quantityEmp){
        if(quantityEmp >= 0){
            this.quantityEmp = quantityEmp;
        }else{
            System.err.println("ERROR!");
        }
    }

    private int getQuantityEmp(){
        return quantityEmp;
    }

    @Override
    public double calcTax(){
        double baseIncome = getIncome();
        
        if(quantityEmp > 10) return baseIncome * 0.14;
        return baseIncome * 0.16;
    }

}
