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
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.Label;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.Stock;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Since Enchant can get a bit out of control with the suggestions it offers,
 * limit the displayed menu to 10 suggestions, thereby preventing the Menu
 * from wrapping top/bottom.
 * 
 * @author Andrew Cowie
 */
class SuggestionsContextMenu extends ContextMenu
{
    private final SpellChecker dict;

    private MenuItem.Activate picked;

    private SuggestionsContextMenu.WordSelected handler;

    SuggestionsContextMenu(ComponentEditorWidget parent) {
        super(parent);
        dict = primary.getDictionary();

        picked = new MenuItem.Activate() {
            public void onActivate(MenuItem source) {
                final WordMenuItem local;
                final String suggestion;

                local = (WordMenuItem) source;
                suggestion = local.getWord();

                handler.onWordSelected(suggestion, true);
            }
        };

        hookupBehaviourHandlers();
    }

    private void hookupBehaviourHandlers() {}

    void populateSuggestions(final String word) {
        final String[] suggestions;
        MenuItem item;
        Label label;
        int i;

        /*
         * Add a MenuItem for each suggestion provided by Enchant. Reuse the
         * same signal handler for each one; calls the WordSelected handler
         * passed in by the EditorTextView if it is Activated.
         */

        suggestions = dict.suggest(word);

        if (suggestions == null) {
            label = new Label("<i>" + _("no suggestions") + "</i>");
            label.setUseMarkup(true);
            label.setAlignment(Alignment.LEFT, Alignment.CENTER);

            item = new MenuItem();
            item.add(label);

            menu.append(item);

        } else {
            for (i = 0; i < suggestions.length; i++) {
                if (i > 10) {
                    break;
                }

                label = new Label("<b>" + suggestions[i] + "</b>");
                label.setUseMarkup(true);
                label.setAlignment(Alignment.LEFT, Alignment.CENTER);

                item = new WordMenuItem(suggestions[i]);
                item.add(label);

                item.connect(picked);

                menu.append(item);
            }
        }

        /*
         * Now, since we allow asking for suggestions even on correctly
         * spelled words [which is a prelude to completion operations], we
         * need to NOT offer to add the word to a custom dictionary if the
         * word is not already unknown.
         */
        if (!dict.check(word)) {
            item = new SeparatorMenuItem();
            menu.append(item);

            item = new ImageMenuItem(new Image(Stock.ADD, IconSize.MENU), _("Add to document word list"));
            menu.append(item);

            item.connect(new MenuItem.Activate() {
                public void onActivate(MenuItem source) {
                    menu.hide();
                    dict.addToDocument(word);

                    handler.onWordSelected(word, false);
                }
            });

            item = new MenuItem(_("Add to personal dictionary"));
            menu.append(item);

            item.connect(new MenuItem.Activate() {
                public void onActivate(MenuItem source) {
                    menu.hide();
                    dict.addToSystem(word);

                    handler.onWordSelected(word, false);
                }
            });

            if (!dict.isSystemValid()) {
                item.setSensitive(false);
            }
        }

        menu.showAll();
    }

    /**
     * Allow the parent EditorTextView to react to a selection being chosen
     */
    void connect(SuggestionsContextMenu.WordSelected handler) {
        this.handler = handler;
    }

    /**
     * Return the word chosen by the user (if any) to the EditorTextView
     * asking for suggestions.
     */
    static interface WordSelected
    {
        /**
         * @param replacement
         *            Indicates whether the word being returned is different
         *            from the word that was queried.
         */
        void onWordSelected(String word, boolean replacement);
    }

    /**
     * Wrapper class to let us get at the word that was passed in as a
     * suggestion.
     * 
     * @author Andrew Cowie
     */
    private static class WordMenuItem extends MenuItem
    {
        private String word;

        private WordMenuItem(final String suggestion) {
            this.word = suggestion;
        }

        private String getWord() {
            return word;
        }
    }
}
