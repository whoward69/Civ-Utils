package me.civ5.modutils.ui.action;

import javax.swing.AbstractAction;
import javax.swing.Action;

import me.civ5.modutils.ui.ModFrame;

public abstract class ModAction extends AbstractAction {
	protected ModFrame owner;
	
	public ModAction(ModFrame owner, String name) {
		this(owner, name, null);
	}
	
	public ModAction(ModFrame owner, String name, String tooltip) {
		super(name);
		this.owner = owner;

		if (tooltip != null) {
			putValue(SHORT_DESCRIPTION, tooltip);
		}
	}
	
	public void setName(String name) {
		putValue(Action.NAME, name);
	}
}