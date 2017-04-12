package FMCA;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Family {
	private Product[] elements;
	private int[][] po;
	
	public Family(Product[] elements, int[][] po)
	{
		this.elements=elements;
		this.po=po;
	}
	public Family()
	{
		this.elements=null;
		this.po=null;
	}
	public Product[] getProducts()
	{
		return elements;
	}
	
	public int[][] getPartialOrder()
	{
		return po;
	}
	
	public static String[] readFile(String currentdir, String filename){
		Path p=Paths.get(currentdir, filename);
		Charset charset = Charset.forName("ISO-8859-1");
		List<String> lines = null;
		try {
			lines = Files.readAllLines(p, charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] arr = lines.toArray(new String[lines.size()]);
		boolean end=false;
		int i=0;
		while (!end)
		{
			String[] s=arr[i].split("{");
			Scanner sc=new Scanner(arr[i]).useDelimiter("{");
			i+=1;
			if (arr[i].equals("end"))
				end=true;
		}
		
		return arr;
	}
	
	public static void main(String[] args){
		String[] t=Family.readFile(System.getProperty("user.dir"),"test.txt");
		System.out.println(t.toString());
	}
}
