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

import org.gnome.gdk.Event;
import org.gnome.gdk.EventFocus;
import org.gnome.gdk.EventKey;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.WindowTypeHint;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.SelectionMode;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreePath;
import org.gnome.gtk.TreeSelection;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.WindowType;

import static quill.client.Quill.ui;

class SuggestionsPopupWindow extends Window
{
    private Window window;

    private TreeView view;

    private ListStore model;

    private DataColumnString columnWords;

    SuggestionsPopupWindow() {
        super(WindowType.TOPLEVEL);
        window = this;

        setupWindow();
        setupListView();

        hookupBehaviourHandlers();
    }

    private void hookupBehaviourHandlers() {
        window.connect(new DeleteEvent() {
            public boolean onDeleteEvent(Widget source, Event event) {
                window.hide();
                return false;
            }
        });

        window.connect(new Widget.KeyPressEvent() {
            public boolean onKeyPressEvent(Widget source, EventKey event) {
                final Keyval key;

                key = event.getKeyval();

                if (key == Keyval.Escape) {
                    window.hide();
                    return true;
                }
                return false;
            }
        });

        /*
         * Lost focus? Then close!
         */

        window.connect(new Widget.FocusOutEvent() {
            public boolean onFocusOutEvent(Widget source, EventFocus event) {
                window.hide();
                return false;
            }
        });

        /*
         * Horizontal cursor movement? Then close!
         */

        window.connect(new Widget.KeyPressEvent() {
            public boolean onKeyPressEvent(Widget source, EventKey event) {
                final Keyval key;

                key = event.getKeyval();

                if ((key == Keyval.Left) || (key == Keyval.Right)) {
                    window.hide();
                    return true;
                }
                return false;
            }
        });

    }

    void populateSuggestions(String word) {
        final String[] suggestions;
        TreeIter row;

        if (word == null) {
            row = model.appendRow();
            model.setValue(row, columnWords, "<i>no suggestions</i>");
            return;
        }

        suggestions = ui.dict.suggest(word);

        for (String suggestion : suggestions) {
            row = model.appendRow();
            model.setValue(row, columnWords, suggestion);
        }
    }

    private void setupWindow() {
        window.setDecorated(false);
        window.setBorderWidth(1);
        window.setTransientFor(ui.primary); // ?
        window.setResizable(false);
        window.setTypeHint(WindowTypeHint.UTILITY);
    }

    private void setupListView() {
        final TreeSelection selection;
        final TreeViewColumn vertical;
        final CellRendererText renderer;

        model = new ListStore(new DataColumn[] {
            columnWords = new DataColumnString(),
        });

        view = new TreeView(model);
        view.setRulesHint(false);
        view.setEnableSearch(false);
        view.setReorderable(false);
        view.setHeadersVisible(false);

        selection = view.getSelection();
        selection.setMode(SelectionMode.SINGLE);

        vertical = view.appendColumn();

        renderer = new CellRendererText(vertical);
        renderer.setMarkup(columnWords);

        window.add(view);

        view.connect(new TreeView.RowActivated() {
            public void onRowActivated(TreeView source, TreePath path, TreeViewColumn vertical) {
                TreeIter row;
                String word;

                row = model.getIter(path);
                word = model.getValue(row, columnWords);

                window.hide();
                handler.onRowActivated(word);
            }
        });
    }

    void presentAt(int x, int y) {
        window.showAll();

        window.present();
        window.move(x, y);
        window.grabFocus();
    }

    private SuggestionsPopupWindow.RowActivated handler;

    /**
     * Allow the parent EditorTextView to react to a selection being chosen
     */
    void connect(SuggestionsPopupWindow.RowActivated handler) {
        this.handler = handler;
    }

    static interface RowActivated
    {
        void onRowActivated(String word);
    }
}
