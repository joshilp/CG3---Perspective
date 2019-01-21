package client;

import javafx.stage.*;
import java.util.*;
import javafx.application.Application;
import windowing.Window361;
import windowing.drawable.Drawable;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		Parameters p = getParameters();
		String arg;

		Map<String, String> namedParams = p.getNamed();
		List<String> unnamedParams = p.getUnnamed();
		List<String> rawParams = p.getRaw();

		String paramStr = "Named Parameters: " + namedParams + "\n" + "Unnamed Parameters: " + unnamedParams + "\n"
				+ "Raw Parameters: " + rawParams;

		System.out.println(paramStr);

		if (rawParams.isEmpty()) {
//			System.out.println("EMPTY!!!!!!!!!");
			arg = null;
		} else {
			arg = rawParams.get(0);
			arg = arg.toString();
		}

		Window361 window = new Window361(primaryStage);
		Drawable drawable = window.getDrawable();
		Client client = new Client(drawable, arg);

		window.setPageTurner(client);
		client.nextPage();

		primaryStage.show();
	}

}
