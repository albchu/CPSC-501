public class ClassCConcrete extends ClassD implements InterfaceA
{

    public ClassCConcrete(){ }
    public ClassCConcrete(int a,int b){ val3= new ClassA(a); val4 = new ClassA(b); }

    public void run() { }

    public String toString() { return "ClassC"; }

    public void func0(int a,boolean c) throws Exception {}

    public void func1(int a,double b,boolean c, String s) throws Exception
    {
    }
    
    private int func2private(String s)throws Exception, ArithmeticException , IllegalMonitorStateException 
    {
    return 1;
    }
    public int func2(String s)throws Exception, ArithmeticException , IllegalMonitorStateException 
    {
	return 1;
    }

   

    private ClassA val2 = new ClassA(100);
    private ClassA val3;
    private ClassA val4;
}
