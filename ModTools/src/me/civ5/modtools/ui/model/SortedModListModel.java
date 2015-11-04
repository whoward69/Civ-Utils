package me.civ5.modtools.ui.model;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class SortedModListModel extends OrderedModListModel implements ModListModel {
	// This can only work if we ensure that we cannot drag-and-drop within a JList backed by this model!
	private Set<Object> delegate = new TreeSet<Object>();

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public int getSize() {
		return delegate.size();
	}

	@Override
	public Object getElementAt(int index) {
		Iterator<Object> itr = delegate.iterator();
		for (int i = index; i > 0; --i) {
			itr.next();
		}
		
		return itr.next();
	}

	@Override
	public void addElement(Object element) {
		delegate.add(element);

		int index = 0;
		Iterator<Object> itr = delegate.iterator();
		while (!itr.next().equals(element)) {
			++index;
		}
	    fireIntervalAdded(this, index, index);
	}

	@Override
	public void addElement(int index, Object element) {
		this.addElement(element);
	}

	@Override
	public Object removeElement(int index) {
		Object rv = getElementAt(index);
		delegate.remove(rv);
		fireIntervalRemoved(this, index, index);
		return rv;
	}
}
