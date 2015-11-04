package me.civ5.modbuilder.ui.control.select;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.bool.PromotionBooleanItem;
import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class PromotionMultiSelectItem extends ReferenceMultiSelectItem implements ListSelectionListener, ChangeListener {
	private static PromotionMultiSelectItem instance = null;
	
	private static List<PromotionBooleanItem> items = new ArrayList<PromotionBooleanItem>();
	
	public static PromotionMultiSelectItem getInstance(String id, ModDb modDb) {
		if (instance == null) {
			instance = new PromotionMultiSelectItem(id, modDb);
			
			for (PromotionBooleanItem item : items) {
				instance.registerPromotionItem(item);
			}
		}

		return instance;
	}
	
	public static PromotionBooleanItem getBooleanItem(String id, String key) {
		PromotionBooleanItem item = new PromotionBooleanItem(id, key);
		
		if (instance != null) {
			instance.registerPromotionItem(item);
		}

		items.add(item);
		
		return item;
	}
	
	private PromotionMultiSelectItem(String id, ModDb modDb) {
		super(id, "notag", "Unit_FreePromotions", "PromotionType", modDb, "UnitPromotions", "Type", "Description", "InstaHeal != 1 AND NOT (Type LIKE '%_1' OR Type LIKE '%_2' OR Type LIKE '%_3')");
		
		list.addListSelectionListener(this);
	}
	
	protected void registerPromotionItem(PromotionBooleanItem item) {
		((JCheckBox) item.getControl()).addChangeListener(this);
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		String combatClass = XpathHelper.getString(row, "./CombatClass");
		
		if (combatClass.equals("UNITCOMBAT_SIEGE")) {
			list.selectKey("PROMOTION_CITY_SIEGE");
			list.selectKey("PROMOTION_SIGHT_PENALTY");
		} else if (combatClass.equals("UNITCOMBAT_SUBMARINE")) {
			list.selectKey("PROMOTION_SILENT_HUNTER");
		}

		super.buildXmlImpl(gamedata, row, type);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		for (int index = e.getFirstIndex(); index <= e.getLastIndex(); ++index) {
			SelectEntry entry = (SelectEntry) list.getModel().getElementAt(index);
			
			for (PromotionBooleanItem item : items) {
				if (item.getTag().equals(entry.getKey())) {
					item.setSelected(list.isSelectedIndex(index));
					break;
				}
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JCheckBox) {
			JCheckBox c = (JCheckBox) e.getSource();
			
			for (PromotionBooleanItem item : items) {
				if (item.getControl().equals(c)) {
					if (item.isSelected()) {
						list.selectKey(item.getTag());
					} else {
						list.deselectKey(item.getTag());
					}
					
					break;
				}
			}
		}
	}
}
