public abstract class Person {
    private String name;
    private double income;

    public Person (String name, double income){
        setName(name);
        setIncome(income);
    }

    protected String getName(){
        return this.name;
    }

    protected Double getIncome(){
        return this.income;
    }

    protected void setName(String name){
        this.name = name;
    }

    protected void setIncome(double income){
        if(income >= 0){
            this.income = income;
        }else{
            System.out.println("ERROR");
        }
    }

    public abstract double calcTax ();

    public String toString(){
        return String.format("%s: $ %.2f\n", name, calcTax());
    }
}
