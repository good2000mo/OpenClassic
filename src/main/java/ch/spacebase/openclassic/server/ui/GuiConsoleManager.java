package ch.spacebase.openclassic.server.ui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import ch.spacebase.openclassic.api.OpenClassic;

public class GuiConsoleManager implements ConsoleManager {

	private ServerFrame frame = new ServerFrame();
	private FileHandler fileHandler;

	public GuiConsoleManager() {
		try {
			this.fileHandler = new FileHandler("server.log");
		} catch(IOException e) {
			OpenClassic.getLogger().severe("Failed to create log file handler!");
			e.printStackTrace();
		}

		ConsoleHandler chandler = new ConsoleHandler();
		chandler.setFormatter(new DateOutputFormatter(new SimpleDateFormat("HH:mm:ss")));

		if(this.fileHandler != null) {
			this.fileHandler.setFormatter(new DateOutputFormatter(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")));
		}

		Logger logger = Logger.getLogger("");

		for (Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}

		logger.addHandler(chandler);
		logger.addHandler(new WindowHandler(new DateOutputFormatter(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"))));

		if(this.fileHandler != null) {
			logger.addHandler(this.fileHandler);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new ServerShutdownHandler()));
	}

	public void setup() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		frame.setLocation((gd.getDisplayMode().getWidth() - frame.getWidth()) / 2, (gd.getDisplayMode().getHeight() - frame.getHeight()) / 2);
		frame.setVisible(true);
	}

	public void stop() {
		this.fileHandler.flush();
		this.fileHandler.close();
		this.frame.setVisible(false);
	}

	public String formatOutput(String message) {
		message = message.replaceAll("&", "&amp;");
		message = message.replaceAll("<", "&lt;");
		message = message.replaceAll(">", "&gt;");
		
		message = message.replaceAll("&amp;0", "<font color='#000000'>");
		message = message.replaceAll("&amp;1", "<font color='#0000AA'>");
		message = message.replaceAll("&amp;2", "<font color='#00AA00'>");
		message = message.replaceAll("&amp;3", "<font color='#00AAAA'>");
		message = message.replaceAll("&amp;4", "<font color='#AA0000'>");
		message = message.replaceAll("&amp;5", "<font color='#AA00AA'>");
		message = message.replaceAll("&amp;6", "<font color='#FFAA00'>");
		message = message.replaceAll("&amp;7", "<font color='#AAAAAA'>");
		message = message.replaceAll("&amp;8", "<font color='#555555'>");
		message = message.replaceAll("&amp;9", "<font color='#5555FF'>");
		message = message.replaceAll("&amp;a", "<font color='#55FF55'>");
		message = message.replaceAll("&amp;b", "<font color='#55FFFF'>");
		message = message.replaceAll("&amp;c", "<font color='#FF5555'>");
		message = message.replaceAll("&amp;d", "<font color='#FF55FF'>");
		message = message.replaceAll("&amp;e", "<font color='#FFFF55'>");
		message = message.replaceAll("&amp;f", "<font color='#000000'>");
		
		int colorCount = message.split("&amp;[0-9a-f]").length - 1;
		for (int i = 0; i < colorCount; i++)
			message += "</font>";
		/* int index = 0;
		while ((index = message.indexOf('&', index + 1)) != -1) {
			String code = message.substring(index+1, index+2);
			if(!code.matches("[0-9a-f]")) continue;
			
			int color = -1;
			try {
				color = Integer.parseInt(code, 16);
			} catch (NumberFormatException nfe) {
			}

			if (color != -1) {
				String replace = "";
				switch (color) {
				case 0:
					replace = "<font color=000000>";
					break;
				case 1:
					replace = "<font color=0000bf>";
					break;
				case 2:
					replace = "<font color=00bf00>";
					break;
				case 3:
					replace = "<font color=00bfbf>";
					break;
				case 4:
					replace = "<font color=bf0000>";
					break;
				case 5:
					replace = "<font color=bf00bf>";
					break;
				case 6:
					replace = "<font color=bfbf00>";
					break;
				case 7:
					replace = "<font color=bfbfbf>";
					break;
				case 8:
					replace = "<font color=404040>";
					break;
				case 9:
					replace = "<font color=4040ff>";
					break;
				case 10:
					replace = "<font color=40ff40>";
					break;
				case 11:
					replace = "<font color=40ffff>";
					break;
				case 12:
					replace = "<font color=ff4040>";
					break;
				case 13:
					replace = "<font color=ff40ff>";
					break;
				case 14:
					replace = "<font color=ffff40>";
					break;
				case 15:
					replace = "<font color=ffffff>";
					break;
				case -1:
					break;
				}
				message = message.replaceFirst(message.substring(index, index+2), replace);
			} else {
				message = message.replaceFirst(message.substring(index, index), "");
			}
		} */

		return message;
	}

	public ServerFrame getFrame() {
		return this.frame;
	}

	private class WindowHandler extends Handler {
		public WindowHandler(Formatter formatter) {
			this.setFormatter(formatter);

			LogManager manager = LogManager.getLogManager();
			String className = this.getClass().getName();
			String level = manager.getProperty(className + ".level");

			this.setLevel(level != null ? Level.parse(level) : Level.INFO);
		}

		public synchronized void publish(LogRecord record) {
			String message = null;

			if (!this.isLoggable(record))
				return;
			try {
				message = this.getFormatter().format(record);
			} catch (Exception e) {
				reportError(null, e, ErrorManager.FORMAT_FAILURE);
			}

			final String msg = message;
			try {
		        SwingUtilities.invokeLater(new Runnable() {
		        	@Override
		        	public void run() {
						try {
							((HTMLEditorKit) frame.log.getEditorKit()).insertHTML((HTMLDocument) frame.log.getDocument(), frame.log.getDocument().getEndPosition().getOffset()-1, msg, 1, 0, null);
						} catch (Exception e) {
							e.printStackTrace();
							System.err.println("Error appending text to console output: " + msg);
						}
		        	}
			    });
				
		        SwingUtilities.invokeLater(new Runnable() {
		        	@Override
		        	public void run() {
		        			frame.log.setCaretPosition(frame.log.getDocument().getLength());
		        	}
		        });
			} catch (Exception ex) {
				this.reportError(null, ex, ErrorManager.WRITE_FAILURE);
			}

		}

		public void close() {
		}

		public void flush() {
		}
	}

	private class DateOutputFormatter extends Formatter {
		private final SimpleDateFormat date;

		public DateOutputFormatter(SimpleDateFormat date) {
			this.date = date;
		}

		@Override
		public String format(LogRecord record) {
			StringBuilder builder = new StringBuilder();

			builder.append(date.format(record.getMillis()));
			builder.append(" [");
			builder.append(record.getLevel().getLocalizedName().toUpperCase());
			builder.append("] ");
			builder.append(formatOutput(formatMessage(record)));
			builder.append('\n');

			if (record.getThrown() != null) {
				StringWriter writer = new StringWriter();
				record.getThrown().printStackTrace(new PrintWriter(writer));
				builder.append(writer.toString());
			}

			return builder.toString();
		}
	}

	private class ServerShutdownHandler implements Runnable {
		@Override
		public void run() {
			OpenClassic.getGame().shutdown();
		}
	}

}
