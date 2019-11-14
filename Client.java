// Java implementation for multithreaded chat client 
// Save file as Client.java 

import java.io.*; 
import java.net.*; 
import java.util.Scanner;
import java.util.*; 

public class Client 
{ 
	final static int ServerPort = 1999; 
	 static String username;
	public static void main(String args[]) throws UnknownHostException, IOException 
	{ 
		String user_command;
		String command_type; 
		
		InetAddress ip = InetAddress.getByName("localhost"); 
		
		
			
		Socket s = new Socket(ip, ServerPort); 

	
		do
		{
			// Socket s = new Socket(ip, ServerPort); 
			System.out.println("=======================");
			System.out.println("Menu");
			System.out.println("=======================");
			System.out.println("Account Creation");
			System.out.println("File Management");
			System.out.println("Group Management");
			System.out.println("=======================");
			System.out.println("             ");
			System.out.print("Command:");
			
			Scanner scn = new Scanner(System.in);
			user_command=scn.nextLine();
			
			String str[]=user_command.split("\\s+");
			command_type=str[0];
			
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

			switch (command_type) { 
        	case "create_user": 
								
								Thread messge_send_thread = new Thread(new Runnable() 
								{ 
									@Override
									public void run() { 
										

											
											String msg ="create_user#"+str[1]+"#abcd";//scn.nextLine();
											username=str[1]; 
										
											
											try { 
												
												dos.writeUTF(msg); 
											} catch (IOException e) { 
												e.printStackTrace(); 
											} 
										
									} 
								}); 
								
								
								Thread messge_read_thread = new Thread(new Runnable() 
								{ 
									@Override
									public void run() { 

										
											try { 
												
												String msg = dis.readUTF(); 
												System.out.println(msg); 
											} catch (IOException e) { 

												e.printStackTrace(); 
											} 
										} 
								
								}); 

								messge_send_thread.start(); 
								messge_read_thread.start();

								break; 
        	case "upload": 
			
							try { 
									String file_name=str[1];
									File file = new File(file_name);
									String msg ="upload#"+username+"/"+str[1]+"#"+file.length();
									
									
									dos.writeUTF(msg); 
									String recvmsg=dis.readUTF();
									System.out.println(recvmsg);
									
									FileInputStream fis = new FileInputStream(file);
									byte[] buffer = new byte[8192];
									
									while (fis.read(buffer) > 0) {
										dos.write(buffer);
										dos.flush();
									}
									
									fis.close();
									//dos.close();
									
									
								}
							catch (IOException e)
							{ 
								e.printStackTrace(); 
							}
			
            				break; 

			case "upload_udp":

									String file_name=str[1];
									File file1 = new File(file_name);
									String mesg ="upload_udp#"+username+"/"+str[1]+"#"+file1.length();
									
									
									dos.writeUTF(mesg); 
									String recvmsg1=dis.readUTF();
									System.out.println(recvmsg1);

									
									DatagramSocket dsoc = new DatagramSocket();
									FileInputStream fis = new FileInputStream(file1);
									byte[] bufferr = new byte[1024];
									
									while (fis.read(bufferr) > 0) {
											
										dsoc.send(new DatagramPacket(bufferr, bufferr.length, ip, 9000));
									
									}
									
									fis.close();
							break;
			case "create_folder":
							String msg="create_folder#"+str[1]+"#"+username;
							dos.writeUTF(msg);
							String recvmsg=dis.readUTF();
							System.out.println(recvmsg);
							break;

			case "move_file":
							msg="move_file#"+str[1]+"#"+str[2];
							dos.writeUTF(msg);
							recvmsg=dis.readUTF();
							System.out.println(recvmsg);
							break;

			case "create_group":
							msg="create_group#"+str[1]+"#"+username;
							dos.writeUTF(msg);
							recvmsg=dis.readUTF();
							System.out.println(recvmsg);

							break;

			case "list_groups":
							msg="list_groups#"+str[1]+"#abcd";
							dos.writeUTF(msg);
							recvmsg=dis.readUTF();
							int sz=Integer.parseInt(recvmsg);
							while(sz>0)
							{
								recvmsg=dis.readUTF();
								System.out.println(recvmsg);
								sz=sz-1;
							}
							// System.out.println(recvmsg);
							// StringTokenizer st = new StringTokenizer(recvmsg, "#");
							// for(int i=0;i<st.countTokens();i++) 
							// System.out.println(st.nextToken()); 
							

							break;

			case "leave_group":
							msg="leave_group#"+str[1]+"#"+username;
							dos.writeUTF(msg);
							recvmsg=dis.readUTF();
							
							break;

			case "join_group":
							msg="join_group#"+str[1]+"#"+username;
							dos.writeUTF(msg);
							recvmsg=dis.readUTF();
							System.out.println(recvmsg);

							break;

			case "share_msg":
							
							
								String comm=user_command;
								messge_send_thread = new Thread(new Runnable() 
								{ 
									@Override
									public void run() { 
										

											String msg_share=str[2];
											
										
											String msg="share_msg#"+str[1]+"#"+msg_share;//scn.nextLine();
												
											try { 
											
												dos.writeUTF(msg); 
											} catch (IOException e) { 
												e.printStackTrace(); 
											} 
										
									} 
								}); 
								messge_read_thread = new Thread(new Runnable() 
								{ 
									@Override
									public void run() { 

										
											try { 
												
												String msg = dis.readUTF(); 
												System.out.println(msg); 
											} catch (IOException e) { 

												e.printStackTrace(); 
											} 
										} 
								
								}); 
							messge_send_thread.start();
							//messge_send_thread.setDaemon(true);
							messge_read_thread.start();
							//messge_read_thread.setDaemon(true);
							break;



			case "get_file":

							msg="get_file#"+str[1]+"#"+username;
							dos.writeUTF(msg);
							String file_sz=dis.readUTF();
							System.out.println(file_sz);

							File file = new File(System.getProperty("user.dir")+"/download.pdf");
									String myFile=file.getAbsolutePath();
									DataInputStream dis1 = new DataInputStream((s).getInputStream());
									FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")+"/download.pdf");
									byte[] buffer = new byte[4096];
									
									int filesize = Integer.parseInt(file_sz); 
									int read = 0;
									int totalRead = 0;
									int remaining = filesize;
									while((read = dis1.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
										totalRead += read;
										remaining -= read;
										System.out.println("read " + totalRead + " bytes.");
										fos.write(buffer, 0, read);
									}
									
									fos.close();
							break;
				case "exit":
									msg ="logout";
									dos.writeUTF(msg);
									System.out.println("Logging out system");
									System.exit(0);
							break;
				default:
						System.out.println("Enter Correct Command !!!!!!!!!!!!!!");
						break;
			
			
			}
			//System.out.println(command_type.equals("exit"));
		}while(!command_type.equals("exit"));

	} 
} 

