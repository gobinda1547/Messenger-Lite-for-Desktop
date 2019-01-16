
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class MyMessenger extends JFrame {

	public static int messageReceiverPort = 55555;
	public static int fileReceiverPort = 55556;
	public static String parentFolder;

	private static MyMessenger simple;
	private JProgressBar progressBar;
	private JComboBox<String> comboBox;

	public static String lastReceivedMessage;

	private static final long serialVersionUID = 1L;

	private MyMessenger() {
		super();
		setResizable(false);
		parentFolder = System.getProperty("user.home") + "/Documents/MyMessenger/";
		setBounds(getScreenWidgth() / 2 - 810 / 2, 0, 810, 20);
		setAlwaysOnTop(true);
		// setSize(810, 20);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		// setLocationRelativeTo(null);

		DropTargetHandler dropTargetHandler = new DropTargetHandler();

		JPanel upperPanel = new JPanel();
		upperPanel.setBounds(0, 0, 810, 20);

		JButton openTransferFolder = new JButton("open");
		openTransferFolder.setBounds(1, 0, 100, 20);
		openTransferFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File dirToOpen = new File(MyMessenger.parentFolder);
					Desktop.getDesktop().open(dirToOpen);
				} catch (Exception e2) {
					showProgressMesseage("Folder may not exist!", 0);
				}
			}
		});
		upperPanel.setLayout(null);
		upperPanel.add(openTransferFolder);

		comboBox = new JComboBox<>(UserListManger.getUserList());
		comboBox.setBounds(204, 0, 250, 20);
		upperPanel.add(comboBox);

		JButton btnExit = new JButton("send");
		btnExit.setBounds(456, 0, 100, 20);
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String receiver = comboBox.getSelectedItem().toString().split(" ")[0];
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = toolkit.getSystemClipboard();
					String result = (String) clipboard.getData(DataFlavor.stringFlavor);
					// System.out.println("String from Clipboard:" + result);
					new Thread(new MessageSender(receiver, result)).start();
				} catch (Exception e2) {
					showProgressMesseage("Error Sending text!", 0);
				}
			}
		});
		upperPanel.add(btnExit);

		JButton copyButton = new JButton("copy");
		copyButton.setBounds(102, 0, 100, 20);
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					StringSelection stringSelection = new StringSelection(MessageReceiver.lastMessage);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);
					showProgressMesseage("Message Copied!", 100);
				} catch (Exception e2) {
					showProgressMesseage("Couldn't Copy Message", 0);
				}
			}
		});
		upperPanel.add(copyButton);

		JPanel panel = new JPanel();
		panel.setLayout(null);

		panel.add(upperPanel);

		progressBar = new JProgressBar();
		progressBar.setBounds(557, 0, 251, 20);
		upperPanel.add(progressBar);
		progressBar.setStringPainted(true);
		progressBar.setDropTarget(dropTargetHandler);

		setContentPane(panel);

	}

	private int getScreenWidgth() {
		return (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	}

	public static void initialize() {
		if (simple == null) {
			simple = new MyMessenger();
			new Thread(new MessageReceiver()).start();
			new Thread(new FileReceiver()).start();
		}
		simple.setVisible(true);
	}

	public static void showMessageSet(String message) {
		if (simple.isVisible() == false) {
			initialize();
		}
		// simple.textArea.setText(message);
	}

	public static void showProgressMesseage(String message, int value) {
		simple.progressBar.setValue(value);
		simple.progressBar.setString(message);
	}

	public static String getSelectedUserIpAddress() {
		return simple.comboBox.getSelectedItem().toString().split(" ")[0];
	}

	class DropTargetHandler extends DropTarget {

		private static final long serialVersionUID = 1L;

		public synchronized void drop(DropTargetDropEvent evt) {

			try {
				evt.acceptDrop(DnDConstants.ACTION_COPY);

				@SuppressWarnings("unchecked")
				List<File> droppedFiles = (List<File>) evt.getTransferable()
						.getTransferData(DataFlavor.javaFileListFlavor);

				if (droppedFiles.size() == 0) {
					return;
				}
				FileSender.sendFiles(droppedFiles.get(0));

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

}