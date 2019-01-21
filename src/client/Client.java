package client;

import client.interpreter.SimpInterpreter;
import client.interpreter.RendererTrio;

import geometry.Point2D;

import line.LineRenderer;
import polygon.FilledPolygonRenderer;
import polygon.PolygonRenderer;
import polygon.ColorPolygonRenderer;
import polygon.WireFramePolygonRenderer;
import windowing.PageTurner;
import windowing.drawable.ColoredDrawable;
import windowing.drawable.Drawable;
import windowing.drawable.InvertedYDrawable;
import windowing.drawable.TranslatingDrawable;
import windowing.drawable.ZBufferingDrawable;
import windowing.graphics.Dimensions;

public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int ARGB_GREEN = 0xff_00_ff_40;

	private static final int NUM_PAGES = 10;
	private static final int NUM_PANELS = 1; // Assignment 2
	private static final Dimensions PANEL_SIZE = new Dimensions(650, 650);

	private final Drawable drawable;
	private int pageNumber = 0;

	private boolean hasArgument;
	private String filename;

	private Drawable image;
	private Drawable[] panels;
	private Drawable fullPanel; // Assignment 2

	private LineRenderer lineRenderers[];
	private PolygonRenderer polygonRenderer;
	private PolygonRenderer wireframeRenderer;
	private PolygonRenderer rainbowRenderer;
	private RendererTrio renderers;

	public Client(Drawable drawable, String arg) {

		if (arg != null) {
//			System.out.println("has argument!!");
//			hasArgument = true;
			hasArgument = checkArg(arg);
		} else {
//			System.out.println("no argument!!");
			hasArgument = false;
		}

		filename = arg;
//		System.out.println("filename: " + filename);

		this.drawable = drawable;
		createDrawables();
		createRenderers();
	}

	public void createDrawables() {
		image = new InvertedYDrawable(drawable);
		image = new TranslatingDrawable(image, point(0, 0), dimensions(750, 750));
		image = new ColoredDrawable(image, ARGB_WHITE);

		fullPanel = new TranslatingDrawable(image, point(50, 50), dimensions(650, 650)); // Assignment 2
		fullPanel = new ZBufferingDrawable(fullPanel);

		createPanels();
	}

	public void createPanels() {
		panels = new Drawable[NUM_PANELS];

		for (int index = 0; index < NUM_PANELS; index++) {
			panels[index] = new TranslatingDrawable(image, point(50, 50), PANEL_SIZE);
		}
	}

	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}

	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}

	private void createRenderers() {
		rainbowRenderer = ColorPolygonRenderer.make();
		wireframeRenderer = WireFramePolygonRenderer.make();
		polygonRenderer = FilledPolygonRenderer.make();
	}

	private void defaultPage() {
		image.clear();
		fullPanel.fill(ARGB_GREEN, Double.MAX_VALUE);
	}

	// Assignment 3
	@Override
	public void nextPage() {
		if (hasArgument) {
//			System.out.println("calling argumentNextPage()");
			argumentNextPage();
			hasArgument = false;
			pageNumber = (pageNumber + 1) % NUM_PAGES;
		} else {
//			System.out.println("calling noArgumentNextPage()");
			noArgumentNextPage();
		}
	}

	private void argumentNextPage() {
		image.clear();
		fullPanel.clear();
		pageNumber = getPageNumber(filename);
		SimpInterpreter interpreter = new SimpInterpreter(filename + ".simp", fullPanel, renderers);
		System.out.println("running the interpreter with " + filename + ".simp");
		interpreter.interpret();
	}

	public void noArgumentNextPage() {
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;

		image.clear();
		fullPanel.clear();

		String filename = getPageLetter(pageNumber);
		
		System.out.println("   -" +filename);
		SimpInterpreter interpreter = new SimpInterpreter(filename + ".simp", fullPanel, renderers);
		interpreter.interpret();
	}
	
	
	private String getPageLetter(int pageNumber)
	{
		String filename = null;

		switch (pageNumber) {
		case 1:	filename = "pageA";	break;
		case 2:	filename = "pageB";	break;
		case 3:	filename = "pageC";	break;
		case 4:	filename = "pageD";	break;
		case 5:	filename = "pageE";	break;
		case 6:	filename = "pageF";	break;
		case 7:	filename = "pageG";	break;
		case 8:	filename = "pageH";	break;
		case 9:	filename = "pageI";	break;
		case 0:	filename = "pageJ";	break;

		default: defaultPage();
		return filename;
		}
		return filename;
	}

	private int getPageNumber(String filename)
	{
		int page = 0;
		
		switch (filename) {
		case "pageA": page = 1;	break;
		case "pageB": page = 2;	break;
		case "pageC": page = 3;	break;
		case "pageD": page = 4;	break;
		case "pageE": page = 5;	break;
		case "pageF": page = 6;	break;
		case "pageG": page = 7;	break;
		case "pageH": page = 8;	break;
		case "pageI": page = 9;	break;
		case "pageJ": page = 0;	break;

		default: page = 0;
		return page;
		}
		return page;
	}
	
	private boolean checkArg(String arg)
	{
		boolean page;
		
		switch (arg) {
		case "pageA": page = true;	break;
		case "pageB": page = true;	break;
		case "pageC": page = true;	break;
		case "pageD": page = true;	break;
		case "pageE": page = true;	break;
		case "pageF": page = true;	break;
		case "pageG": page = true;	break;
		case "pageH": page = true;	break;
		case "pageI": page = true;	break;
		case "pageJ": page = true;	break;

		default: page = false;
		return page;
		}
		return page;
	}
}
