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
import org.gnome.gtk.Alignment;
import org.gnome.gtk.Allocation;
import org.gnome.gtk.Button;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.SelectionMode;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreePath;
import org.gnome.gtk.TreeSelection;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.WindowType;

import static quill.client.Quill.ui;

class SuggestionsPopupWindow extends Window
{
    private Window window;

    private VBox top;

    private TreeView view;

    private ListStore model;

    private DataColumnString columnMarkup;

    private DataColumnString columnWord;

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
        final Button button;

        if (word == null) {
            row = model.appendRow();
            model.setValue(row, columnMarkup, "<i>no suggestions</i>");
            return;
        }

        suggestions = ui.dict.suggest(word);

        for (String suggestion : suggestions) {
            row = model.appendRow();
            model.setValue(row, columnMarkup, "<b>" + suggestion + "</b>");
            model.setValue(row, columnWord, suggestion);
        }

        button = new Button("Add");
        button.setAlignment(Alignment.LEFT, Alignment.CENTER);
        top.packEnd(button, false, false, 0);
    }

    private void setupWindow() {
        window.setDecorated(false);
        window.setBorderWidth(1);
        window.setTransientFor(ui.primary); // ?
        window.setResizable(false);
        window.setTypeHint(WindowTypeHint.UTILITY);

        top = new VBox(false, 0);

        window.add(top);
    }

    private void setupListView() {
        final TreeSelection selection;
        final TreeViewColumn vertical;
        final CellRendererText renderer;
        final ScrolledWindow scroll;

        model = new ListStore(new DataColumn[] {
                columnWord = new DataColumnString(), columnMarkup = new DataColumnString(),
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
        renderer.setMarkup(columnMarkup);

        /*
         * Add scrollbars for when there are more words than available space.
         */

        scroll = new ScrolledWindow();
        scroll.setPolicy(PolicyType.NEVER, PolicyType.AUTOMATIC);
        scroll.add(view);
        top.packStart(scroll, true, true, 0);

        view.connect(new TreeView.RowActivated() {
            public void onRowActivated(TreeView source, TreePath path, TreeViewColumn vertical) {
                TreeIter row;
                String word;

                row = model.getIter(path);
                word = model.getValue(row, columnWord);

                window.hide();
                handler.onWordSelected(word);
            }
        });
    }

    /**
     * The maximum height we want our popup to be.
     */
    private static final int HEIGHT = 200;

    void presentAt(final int x, final int y, final int h) {
        final Allocation alloc;
        final int H;

        window.setSizeRequest(150, HEIGHT);
        window.showAll();

        window.present();

        window.grabFocus();

        /*
         * Get the height of the parent window. Right now this is available on
         * the UserInterface global.
         */

        alloc = ui.primary.getAllocation();

        H = alloc.getHeight();

        /*
         * Figure out if there is sufficient room for the popup below, as we
         * would prefer. The plus 3 is sufficient to continue to show the red
         * squiggle.
         */

        if (y + HEIGHT < H) {
            window.move(x - 2, y + h + 3);
        } else {
            window.move(x - 2, y - HEIGHT);
        }
    }

    private SuggestionsPopupWindow.WordSelected handler;

    /**
     * Allow the parent EditorTextView to react to a selection being chosen
     */
    void connect(SuggestionsPopupWindow.WordSelected handler) {
        this.handler = handler;
    }

    static interface WordSelected
    {
        void onWordSelected(String word);
    }
}
