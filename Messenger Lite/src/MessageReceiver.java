import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageReceiver implements Runnable {

	public static String lastMessage = "";

	public MessageReceiver() {
		// System.out.println("message receiver waiting....");
	}

	@Override
	public void run() {

		while (true) {
			try {
				ServerSocket serverSocket = new ServerSocket(MyMessenger.messageReceiverPort);
				Socket socket = serverSocket.accept();
				DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
				lastMessage = dataInputStream.readUTF();
				serverSocket.close();
				MyMessenger.showProgressMesseage("Message Received!", 100);
				return;
			} catch (Exception e) {
				MyMessenger.showProgressMesseage("Message Received Error!", 0);
			}
		}

	}

}
