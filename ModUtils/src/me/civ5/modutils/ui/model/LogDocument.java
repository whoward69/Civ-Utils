package me.civ5.modutils.ui.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogMessage;
import me.civ5.modutils.log.ModReporter;

public class LogDocument extends DefaultStyledDocument implements ModReporter {
	private List<LogMessage> messages = new ArrayList<LogMessage>();
	private int level = LogMessage.INFO;
	
	private boolean lastMessageBlank = false;
	
	@Override
	public int getLevel() {
		return level;
	}
	
	@Override
	public void setLevel(int level) {
		int oldLevel = this.level;
		this.level = level;
		
		if (oldLevel != level) {
			// Synchronise this in case the user is changing the filter level when a new message arrives from the worker
			synchronized (messages) {
				try {
					remove(0, getLength());
					lastMessageBlank = false;

					for (LogMessage msg : messages) {
						append(msg);
					}
				} catch (BadLocationException e) {
				}
			}
		}
	}

	@Override
	public void log(Exception e) {
		log(new LogError(e.getMessage()));
	}

	@Override
	public void log(LogMessage msg) {
		// Synchronise this in case a new message arrives from the worker while the user is changing the filter level
		synchronized (messages) {
			messages.add(msg);
			append(msg);
		}
	}
	
	private void append(LogMessage msg) {
		if (msg.getText(level) != null) {
			try {
				if (!msg.isBlank()) {
					SimpleAttributeSet attrs = new SimpleAttributeSet();
					
					if (lastMessageBlank) {
						insertString(getLength(), "\n", attrs);
					}
				
					if (msg.isError()) {
						attrs.addAttribute(StyleConstants.Foreground, Color.RED);
						attrs.addAttribute(StyleConstants.Bold, true);
					} else if (msg.isWarn()) {
						attrs.addAttribute(StyleConstants.Foreground, Color.MAGENTA);
					} else if (msg.isDebug()) {
						attrs.addAttribute(StyleConstants.Foreground, Color.GRAY);
					} else {
						attrs.addAttribute(StyleConstants.Foreground, Color.BLACK);
					}
					
					insertString(getLength(), msg.getText() + "\n", attrs);
				}
				
				lastMessageBlank = msg.isBlank();
			} catch (BadLocationException e) {}
		}

	}
}