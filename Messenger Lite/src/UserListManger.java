import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

public class UserListManger {

	private static final String userListFileLocation = "c:/userList.txt";

	public static String[] getUserList() {

		try {

			// System.out.println(userListFileLocation);
			File file = new File(userListFileLocation);
			if (file.exists() == false) {
				Formatter formatter = new Formatter(file);
				formatter.close();
			}

			ArrayList<String> userList = new ArrayList<>();
			Scanner scanner = new Scanner(new File(userListFileLocation));
			while (scanner.hasNextLine()) {
				userList.add(scanner.nextLine());
			}
			scanner.close();
			String[] vals = new String[userList.size()];
			for (int i = 0; i < vals.length; i++) {
				vals[i] = userList.get(i);
			}
			// System.out.println(vals.length);
			return vals;
		} catch (Exception e) {
			MyMessenger.showProgressMesseage("c:/userList.txt problem!", 0);
		}

		return null;

	}

}
