
import java.io.*; 
import java.util.*; 
import java.net.*; 
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

 
public class Server 
{ 

	
	static Vector<ClientThreads> ar = new Vector<>(); 
	static HashMap<String, ArrayList<String>> grouplist = new HashMap<String, ArrayList<String>>();
 	static HashMap<String, ArrayList<String>> dirFiles = new HashMap<String, ArrayList<String>>();

	
	static int i = 0; 

	public static void main(String[] args) throws IOException 
	{ 
	
		ServerSocket ss = new ServerSocket(1999); 
		System.out.println("==================================================================");

		System.out.println("Server Running at:"+ss);

		System.out.println("===================================================================");
		Socket s; 
		while (true) 
		{ 
			System.out.println("Accepting Requests ............");
			
			s = ss.accept(); 

			System.out.println("New Clent Request : " + s); 
			
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
			
			System.out.println("New thread handler for client..."); 

			ClientThreads mtch = new ClientThreads(s,"client " + i, dis, dos); 

			Thread t = new Thread(mtch); 
			
			System.out.println("Addition of client to active list"); 

			
			ar.add(mtch); 

			t.start(); 
			 
			i++; 

		} 
	} 
} 


class ClientThreads implements Runnable 
{ 
	Scanner scn = new Scanner(System.in); 
	private String name; 
	final DataInputStream dis; 
	final DataOutputStream dos; 
	String username;
	Socket s; 
	boolean isloggedin; 
	
	public static void listFilesForFolder(File folder)
            throws IOException {

        if(folder.isDirectory()) {

            ArrayList<String> fileNames = new ArrayList<String>();

            for (final File fileEntry : folder.listFiles()) {
               // System.out.println(fileEntry.toString());
                if (fileEntry.isDirectory()) {
                //  System.out.println(fileEntry.toString());
                    listFilesForFolder(fileEntry);
                } else {
                    String fileName = (fileEntry.getPath()).toString();
                    fileNames.add(fileEntry.getPath());
                }
            }
            Server.dirFiles.put(folder.getName(), fileNames);
        }
    }
	public ClientThreads(Socket s, String name, 
							DataInputStream dis, DataOutputStream dos) { 
		this.dis = dis; 
		this.dos = dos; 
		this.name = name; 
		this.username="";
		this.s = s; 
		this.isloggedin=true; 
	} 

 
	@Override
	public void run() { 

		String received; 
		while (true) 
		{ 
			try
			{ 
				//handling for share messgae needed
				// for proper working of share msg use received = this.dis.readUTF(); 
				received = dis.readUTF(); 
				//System.out.println(received);
				
				if(received.equals("logout")){ 
					this.isloggedin=false; 
					System.out.println(this.username+" exited Successfully");
					//this.s.close(); 
					break; 
				} 
				StringTokenizer st = new StringTokenizer(received, "#"); 
				String command_type = st.nextToken(); 
				String attribute="";
				String File_Size="";
				if(command_type.equals("share_msg"))
				{
					attribute = st.nextToken();
					while(st.hasMoreElements())
					File_Size=" "+st.nextToken();
				}
				else
				{
				attribute = st.nextToken();
				File_Size = st.nextToken();
				}


				switch(command_type)
				{
					case "create_user":
										
										this.username=attribute;
										this.name=attribute;
										File foldername = new File(System.getProperty("user.dir")+"/"+attribute);
										if (!foldername.exists()) {
											if (foldername.mkdir()) {
												this.dos.writeUTF("User is created!");
											} else {
												this.dos.writeUTF("Failed to create User!");
											}
										}
										this.dos.writeUTF("User created successfully"); 
										
										break;
					case "upload":
					
				
									this.dos.writeUTF("File Upload in progress");
									
									File file = new File(System.getProperty("user.dir")+"/"+attribute);//user/filename
									String myFile=file.getAbsolutePath();
									DataInputStream dis = new DataInputStream((this.s).getInputStream());
									FileOutputStream fos = new FileOutputStream(myFile);
									byte[] buffer = new byte[4096];
									
									int filesize = Integer.parseInt(File_Size); 
									int read = 0;
									int totalRead = 0;
									int remaining = filesize;
									while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
										totalRead += read;
										remaining -= read;
										System.out.println("read " + totalRead + " bytes.");
										fos.write(buffer, 0, read);
									}
									
									fos.close();
									//dis.close();
									
									break;

						case "upload_udp":
									this.dos.writeUTF("File Upload in progress");
									
								
									DatagramSocket dsoc = new DatagramSocket(9000);
									file = new File(System.getProperty("user.dir")+"/"+attribute);
									 myFile=file.getAbsolutePath();
									//DataInputStream dis = new DataInputStream((this.s).getInputStream());
									 fos = new FileOutputStream(myFile);
									byte[] bufferr = new byte[1024];
									
									 filesize = Integer.parseInt(File_Size); // Send file size in separate msg
									 read = 0;
									 totalRead = 0;
									 remaining = filesize;
									 try{
									while(true) {
										DatagramPacket dp = new DatagramPacket(bufferr, bufferr.length);
										dsoc.receive(dp);
										byte[] buffer1 = dp.getData();  
										
										fos.write(buffer1, 0, buffer1.length);
										buffer1 = new byte[4096];
									}}
									catch(Exception e)
									{e.printStackTrace();}
									
									fos.close();
									break;

					case "create_folder":
									foldername = new File(System.getProperty("user.dir")+"/"+File_Size+"/"+attribute);
									if (!foldername.exists()) {
										if (foldername.mkdir()) {
											this.dos.writeUTF("Directory is created!");
										} else {
											this.dos.writeUTF("Failed to create directory!");
										}
									}

									break;

					case "move_file":
									try{
										
									File afile = new File(System.getProperty("user.dir")+"/"+attribute);
									System.out.println(System.getProperty("user.dir")+"/"+attribute);
									System.out.println(System.getProperty("user.dir")+"/"+File_Size+"/temp.pdf");
									if(afile.renameTo(new File(System.getProperty("user.dir")+"/"+File_Size)))
									{
										this.dos.writeUTF("File is moved successful!");
									}else{
										this.dos.writeUTF("File is failed to move!");
									}
										
									}catch(Exception e){
										e.printStackTrace();
									}
   									 
									break;

					case "create_group":
									ArrayList<String> itemsList = (Server.grouplist).get(attribute);

									
									if(itemsList == null) {
										itemsList = new ArrayList<String>();
										itemsList.add(File_Size);
										(Server.grouplist).put(attribute, itemsList);
									} else {
										
										if(!itemsList.contains(File_Size)) itemsList.add(File_Size);
										(Server.grouplist).put(attribute, itemsList);
									}
									foldername = new File(System.getProperty("user.dir")+"/"+attribute);
									if (!foldername.exists()) {
										if (foldername.mkdir()) {
											System.out.println("Directory is created!");
										} 
									}

									this.dos.writeUTF("Group created successfully");

									break;

					case "list_groups":
									String mesg="";
									// for (Map.Entry<String,ArrayList<String>> entry : Server.grouplist.entrySet())  
            						// 	mesg+= entry.getKey()+"#";
									
									listFilesForFolder(new File(System.getProperty("user.dir")+"/"+attribute));

									this.dos.writeUTF(Integer.toString(Server.dirFiles.size()));
									System.out.println("hello");
									for(Map.Entry<String, ArrayList<String>> folder_n : Server.dirFiles.entrySet())
									{
										mesg=folder_n.getKey() + "-" + folder_n.getValue();
										this.dos.writeUTF(mesg);

									}
									//this.dos.writeUTF(mesg);

									break;	
					case "leave_group":

									for (Map.Entry<String,ArrayList<String>> entry : Server.grouplist.entrySet()) 
									{
										entry.getValue().remove(File_Size);
									}
									System.out.println(System.getProperty("user.dir")+"/"+attribute+"/"+File_Size);
									File index = new File(System.getProperty("user.dir")+"/"+attribute+"/"+File_Size);
									if (index.exists())
									{
										String[]entries = index.list();
										for(String s: entries){
											File currentFile = new File(index.getPath(),s);
											currentFile.delete();
										}
									}
									index.delete();
									mesg="Successful leaving group";
									this.dos.writeUTF(mesg);
									break;

					case "join_group":
									ArrayList<String> list=new ArrayList<String>();
									list.add(File_Size);

									// itemsList = (Server.grouplist).get(attribute);

									
									// if(itemsList == null) {
									// 	itemsList = new ArrayList<String>();
									// 	itemsList.add(File_Size);
									// 	(Server.grouplist).put(attribute, itemsList);
									// } else {
										
									// 	if(!itemsList.contains(File_Size)) itemsList.add(File_Size);
									// 	(Server.grouplist).put(attribute, itemsList);
									// }

									//Server.grouplist.put(attribute,list);
									foldername = new File(System.getProperty("user.dir")+"/"+attribute+"/"+File_Size);
									if (!foldername.exists()) {
										if (foldername.mkdir()) {
											System.out.println("Directory is created!");
										} 
									}

									this.dos.writeUTF("Group joining successfully");

									break;
					case "share_msg":

										String groupname=attribute;
					
										for(Map.Entry<String, ArrayList<String>> folder_n : Server.grouplist.entrySet())
										{
											
											if(folder_n.getKey().equals(groupname));
											{
												
												ArrayList<String> value = folder_n.getValue();
    											for(String aString : value)
												{
													for (ClientThreads mc : Server.ar)  
													{ 
														
														
															if (mc.name.equals(aString) && mc.isloggedin==true)  
															{ 
													

																mc.dos.writeUTF(this.name+" : "+File_Size); 
																
															} 
													}
												} 
											
											}
									 	}
										
									
									break;

				    case "get_file":
								file = new File(System.getProperty("user.dir")+"/"+attribute);
								mesg="";
								mesg=Integer.toString((int)file.length());
								this.dos.writeUTF(mesg);
									String file_tf=file.getAbsolutePath();
									FileInputStream fis = new FileInputStream(file_tf);
									byte[] buffer_read = new byte[8192];
									
									while (fis.read(buffer_read) > 0) {
										this.dos.write(buffer_read);
										this.dos.flush();
									}
									
									fis.close();
									break;
				default:
									System.out.println("Not find command");
									break;
				}
				//break;
				
				
				
				} catch (IOException e) 
				{ 
				
					e.printStackTrace(); 
				} 
			
		} 
		try
		{ 
			
			this.dis.close(); 
			this.dos.close(); 
			
		}catch(IOException e){ 
			e.printStackTrace(); 
		} 
	} 
} 

