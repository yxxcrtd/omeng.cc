package open.wechat.util;

public class Test {
public static void main(String[] args) {
	String money="0.88";
	float sessionmoney = Float.parseFloat(money);
	String finalmoney = String.format("%.2f", sessionmoney);
	finalmoney = finalmoney.replace(".", "");
	int intMoney = Integer.parseInt(finalmoney);
	System.out.println(intMoney);
	
	float mo=Float.parseFloat(finalmoney)/100;
	
	System.out.println(mo);
}
}
