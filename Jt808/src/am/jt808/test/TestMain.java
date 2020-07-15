package am.jt808.test;

import java.util.Scanner;

public class TestMain 
{
	
	public static void main(String[] args) throws Exception 
	{
		System.out.println("��ʼ����");
		Scanner s = new Scanner(System.in);  
		
		System.out.println("������exit�˳�����");
		while (true) 
		{
			String lin = s.nextLine();
			if (lin.equals("exit"))  
			{
				break;
			}
			System.out.println(">>>" + lin);
		}  
	} 

}
