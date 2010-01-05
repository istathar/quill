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

import org.gnome.gtk.Alignment;
import org.gnome.gtk.Allocation;
import org.gnome.gtk.Button;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.Label;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.Requisition;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.Stock;

import static quill.client.Quill.ui;

class SuggestionsPopupMenu extends Menu
{
    private Menu menu;

    SuggestionsPopupMenu() {
        super();
        menu = this;

        hookupBehaviourHandlers();
    }

    private void hookupBehaviourHandlers() {}

    void populateSuggestions(String word) {
        final String[] suggestions;
        MenuItem item;
        Label label;
        final Button button;

        if (word == null) {
            item = new MenuItem();

            label = new Label("<i>no suggestions</i>");
            label.setUseMarkup(true);

            item.add(label);
            menu.append(item);
            return;
        }

        suggestions = ui.dict.suggest(word);

        for (String suggestion : suggestions) {
            label = new Label("<b>" + suggestion + "</b>");
            label.setUseMarkup(true);
            label.setAlignment(Alignment.LEFT, Alignment.CENTER);

            item = new MenuItem();
            item.add(label);

            menu.append(item);
        }

        item = new SeparatorMenuItem();
        menu.append(item);
        item = new ImageMenuItem(Stock.ADD);
        menu.append(item);

        item.connect(new MenuItem.Activate() {

            public void onActivate(MenuItem source) {
                // menu.hide();

                handler.onWordSelected("");
            }
        });
        menu.showAll();
    }

    /**
     * The maximum height we want our popup to be.
     */
    private static final int HEIGHT = 200;

    /**
     * Present the suggestions menu at x, y (and supplying current line's
     * y-range [height] as R).
     */
    void presentAt(final int x, final int y, final int R) {
        final Allocation alloc;
        final int h, H;

        final Requisition req;

        /*
         * Get the height of the parent window. Right now this is available on
         * the UserInterface global.
         */

        alloc = ui.primary.getAllocation();
        H = alloc.getHeight();

        req = menu.getRequisition();
        h = req.getHeight();

        /*
         * Figure out if there is sufficient room for the popup below, as we
         * would prefer. The plus 3 is sufficient to continue to show the red
         * squiggle. The minus 24 moves the popup back so that the suggestions
         * are aligned with the word start.
         */

        if (y + R + h < H) {
            menu.popup(x - 24, y + R + 3);
        } else {
            menu.popup(x - 24, y - h);
        }
    }

    private SuggestionsPopupMenu.WordSelected handler;

    /**
     * Allow the parent EditorTextView to react to a selection being chosen
     */
    void connect(SuggestionsPopupMenu.WordSelected handler) {
        this.handler = handler;
    }

    static interface WordSelected
    {
        void onWordSelected(String word);
    }
}
