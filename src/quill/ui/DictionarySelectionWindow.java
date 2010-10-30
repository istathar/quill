/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.ui;

import org.freedesktop.enchant.Enchant;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.Entry;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.SelectionMode;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreeModelFilter;
import org.gnome.gtk.TreeModelSort;
import org.gnome.gtk.TreePath;
import org.gnome.gtk.TreeSelection;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Window;

class DictionarySelectionWindow extends Window
{
    private final VBox top;

    private Entry search;

    private TreeView view;

    private ListStore store;

    private TreeModelFilter filtered;

    private TreeModelSort sorted;

    private DataColumnString tagColumn;

    private DataColumnString displayColumn;

    DictionarySelectionWindow() {
        super();

        top = new VBox(false, 0);
        super.add(top);

        buildModel();
        setupSearch();
        setupView();

        populateModel();

        hookupSelectionSignals();
    }

    private void buildModel() {
        tagColumn = new DataColumnString();
        displayColumn = new DataColumnString();

        store = new ListStore(new DataColumn[] {
                tagColumn, displayColumn
        });
        filtered = new TreeModelFilter(store, null);
        sorted = new TreeModelSort(filtered);
    }

    private void setupSearch() {
        search = new Entry();

        top.packStart(search, false, false, 0);
    }

    private void setupView() {
        final TreeViewColumn vertical;
        final CellRendererText renderer;

        view = new TreeView(sorted);
        view.setRulesHint(false);
        view.setEnableSearch(false);
        view.setReorderable(false);
        view.setHeadersVisible(false);

        vertical = view.appendColumn();
        vertical.setReorderable(false);
        vertical.setResizable(false);
        vertical.setSortColumn(displayColumn);
        vertical.emitClicked();

        renderer = new CellRendererText(vertical);
        renderer.setText(displayColumn);

        top.packStart(view, false, false, 0);
    }

    private void populateModel() {
        final String[] list;
        int i;
        TreeIter row;

        list = Enchant.listDictionaries();

        for (i = 0; i < list.length; i++) {
            row = store.appendRow();
            store.setValue(row, tagColumn, list[i]);
            store.setValue(row, displayColumn, list[i]); // FIXME
        }
    }

    private void hookupSelectionSignals() {
        final TreeSelection selection;

        selection = view.getSelection();
        selection.setMode(SelectionMode.SINGLE);

        view.connect(new TreeView.RowActivated() {
            public void onRowActivated(TreeView source, TreePath path, TreeViewColumn vertical) {
                final TreeIter row;

                row = sorted.getIter(path);

                // TODO
            }
        });
    }
}
