package my.rest;

import java.io.IOException;
//import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SrvltMyRest
 */
@WebServlet("/SrvltMyRest")
public class SrvltMyRest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String USER_AGENT = "Mozilla/5.0";
	private final String CHAR_SET = "UTF-8";
	private final String TYPE_CONT = "text/plain";
	private final String OPENHAB_URL = "http://10.32.0.251:8080";
	private final String COMMAND_ON = "ON";
	private final String COMMAND_OFF ="OFF";
	private final String STATE_OPEN = "OPEN";
	private final String STATE_CLOSED = "CLOSED";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String itemName [] = {"m1p00","m1p01","m1p02","m1p03","m1p04","m1p05","m1p06","m1p07","m1p08","m1p09","m1p10","m1p11","m1p12","m1p13"};
		char itemType [] = {'1',  '1',  '1',  '1'  ,'1',  '1',  '1',  '2',  '2',  '2',  '2', '2', '2',  '2'};
		char paramType = '1';
		String paramName ="";
		String setCommand = "";
		String fileName= "D:\\mega0.ini";
		int j;
		int readChar;
		boolean errorFlag = true;

		String str_pt = request.getParameter("pt");
		String str_m = request.getParameter("m");
		
//		String str_v = request.getParameter("v");
//		String str_dir = request.getParameter("dir");
//		String response = "";
         System.out.println(System.getProperty("user.dir")
		
		
		if (str_pt == "" || str_pt == null) 
			{System.out.println("Value Pointer Code not defind");
			return;
			}
		
		byte value_pt = 0;
			try {
				value_pt = Byte.parseByte(str_pt);
				}
				catch (Exception ex)
				{
				System.out.println("Value Pointer Code : " + value_pt);	
				}
			
		if (value_pt < 0 || value_pt > 14)
			{System.out.println("Value Pointer out of interval");
			return;
			}
		try (FileInputStream filePointId = new FileInputStream(fileName)){
			
			for(int i=0; i <= value_pt; i++){
				paramName = "";
				paramType = 'n';
				readChar = filePointId.read();
				if(readChar != -1)
					{paramType = (char)readChar;}
				else
				{
System.out.println("End Of File");
					errorFlag = false;
					break;
				}
			j=0;
			do {
				if(j<16)
					{readChar = filePointId.read();
					if(readChar == -1)
	{System.out.println("End Of File"); errorFlag = false; break;}
					else
					{
						if (readChar != ';')
							{paramName = paramName + (char)readChar;
							j=j+1;}
						else
		//good exit
							break;
					}
					}
					else
					{
	System.out.println("Value Items Name Too Long>16 = " + paramName);
						errorFlag = false;
						break;
					}
					} while (true);
			
					
			}
			
			
//	System.out.println("Value Param for controll = " + paramType + " " + paramName + " " +errorFlag);
			
							if((paramType == '1'||paramType == '2')&&(paramName != "")&&errorFlag)
							{
								itemName [value_pt] = paramName;
								itemType [value_pt] = paramType;
//	System.out.println("Value Items Name defind OK = " + itemType [value_pt] + " " + itemName[value_pt]);
							}
							else
	{System.out.println("Value Items Name set default = " + itemType [value_pt] + " " + itemName[value_pt]);}
		}
		catch (FileNotFoundException e){System.out.println("File "+fileName + "not found.");}
		catch (IOException e){System.out.println("Input/output error.");}	
	
		switch (itemType [value_pt]){
				case '1':
				setCommand = STATE_CLOSED;
				if (isValueUp(str_m))setCommand = STATE_OPEN;
				try {sendCommandPUT(itemName [value_pt],setCommand);}
				catch ( Exception ex )
	{System.out.println("sendGet Exception");}			
				
				break;
				case '2':
				setCommand = COMMAND_OFF;
				if (isValueUp(str_m))setCommand = COMMAND_ON;
				try {sendCommandGET(itemName [value_pt],setCommand);}
				catch ( Exception ex )
	{System.out.println("sendGet Exception");}			
				
				break;
				default:
		}
	}

//con.connect();	

/*-------------------------------------------------------------------------------*/
	protected boolean isValueUp(String str_m)
	{
		if (str_m == "" || str_m == null) 
		{
//			System.out.println("Set UpDown Code not defind. SetItem ->up");
			return true;
		}
		else
		{
//			System.out.println("Set UpDown Code defind. SetItem ->down");	
			return false;
		}
	}

/*-------------------------------------------------------------------------------*/
	protected void sendCommandPUT(String itemName, String Command) throws Exception
	{
		byte[] postData = Command.getBytes("UTF-8");
		String urlString = OPENHAB_URL+"/rest/items/"+itemName+"/state";
		
    System.out.println("\nTesting - Send Http PUT request --> " + Command);
	System.out.println("\nSending 'PUT' request to URL : " + urlString);	

		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", TYPE_CONT);
		con.setRequestProperty("charset", CHAR_SET);
		con.setRequestMethod("PUT");			

		con.setDoOutput(true);
		DataOutputStream httpout = new DataOutputStream(con.getOutputStream());
		httpout.write(postData);
		httpout.flush();
		httpout.close();

		int responseCode = con.getResponseCode();
System.out.println("Response Code : " + responseCode);
System.out.println(readStreamToString(con.getInputStream(),CHAR_SET));
	
	if(con != null){con.disconnect();}
	}
/*-------------------------------------------------------------------------------*/	
	protected void sendCommandGET(String itemName, String Command) throws Exception
	{
		String urlString = OPENHAB_URL+"/CMD?"+itemName+"="+Command;
		
   System.out.println("\nTesting - Send Http GET request --> " + Command);
   System.out.println("\nSending 'GET' request to URL : " + urlString);	

		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", TYPE_CONT);
		con.setRequestProperty("charset", CHAR_SET);
		con.setRequestMethod("GET");	
		int responseCode = con.getResponseCode();
		
	System.out.println("Response Code : " + responseCode);
	System.out.println(readStreamToString(con.getInputStream(),CHAR_SET));		
		if(con != null){con.disconnect();}
	}

/*-------------------------------------------------------------------------------*/
private String readStreamToString(InputStream in,String encoding)throws IOException {
	StringBuffer response = new StringBuffer();
	InputStreamReader reader = new InputStreamReader(in,encoding);
	int httpChar;
	while ((httpChar = reader.read()) != -1) {response.append((char)httpChar);}
	reader.close();
return response.toString();
}
}

