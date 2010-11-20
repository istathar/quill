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

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.freedesktop.enchant.Enchant;
import org.gnome.gdk.EventFocus;
import org.gnome.gdk.EventKey;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.WindowTypeHint;
import org.gnome.gtk.Allocation;
import org.gnome.gtk.Button;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.SelectionMode;
import org.gnome.gtk.Statusbar;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreeModel;
import org.gnome.gtk.TreeModelFilter;
import org.gnome.gtk.TreeModelSort;
import org.gnome.gtk.TreePath;
import org.gnome.gtk.TreeSelection;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;

import static org.freedesktop.bindings.Internationalization._;
import static org.freedesktop.bindings.Internationalization.translateCountryName;
import static org.freedesktop.bindings.Internationalization.translateLanguageName;
import static org.gnome.gtk.Alignment.CENTER;
import static org.gnome.gtk.Alignment.LEFT;

class LanguageSelectionButton extends Button
{
    private final Button button;

    private DictionarySelectionWindow window;

    private String code;

    LanguageSelectionButton(PrimaryWindow primary) {
        super("xx_YY");
        final int width;

        button = this;
        window = new DictionarySelectionWindow(this);
        window.setTransientFor(primary);

        /*
         * Set the size of the button to be the width of the widest display
         * string. This is what ComboBox does by itself, but here we have to
         * create a similar effect by hand.
         */

        width = window.getWidestDisplay();
        button.setSizeRequest(width, -1);
        button.setAlignment(LEFT, CENTER);

        button.connect(new Button.Clicked() {
            public void onClicked(Button source) {
                final Allocation alloc;
                final org.gnome.gdk.Window underlying;
                int x, y;

                window.setCode(code);

                underlying = button.getWindow();
                x = underlying.getOriginX();
                y = underlying.getOriginY();

                alloc = button.getAllocation();
                x += alloc.getX();
                y += alloc.getY();

                window.move(x, y);
                window.show();
            }
        });

        window.connect(new Widget.FocusOutEvent() {
            public boolean onFocusOutEvent(Widget source, EventFocus event) {
                window.hide();
                return false;
            }
        });
    }

    private LanguageSelectionButton.Changed handler;

    interface Changed
    {
        void onChanged(LanguageSelectionButton source);
    }

    void connect(LanguageSelectionButton.Changed handler) {
        this.handler = handler;
    }

    /**
     * Callback from DictionarySelectionWindow
     */
    void setLanguage(String code, String display) {
        button.setLabel(display);

        if (code.equals(this.code)) {
            return;
        }
        this.code = code;

        handler.onChanged(this);
    }

    String getCode() {
        return code;
    }

    /**
     * Entry point from MetadataEditorWidget
     */
    void setCode(String tag) {
        final String display;

        if (tag.equals(this.code)) {
            return;
        }
        this.code = tag;

        window.setCode(tag);
        display = window.getDisplayForSelected();

        button.setLabel(display);
    }
}

/**
 * Popup a window to allow the user to select which (of the installed)
 * dictionaries they wish the document language to be.
 * 
 * @author Andrew Cowie
 */
class DictionarySelectionWindow extends Window
{
    private final DictionarySelectionWindow window;

    private final LanguageSelectionButton enclosing;

    private VBox top;

    private TreeView view;

    private Statusbar status;

    private ListStore store;

    private TreeModelFilter filtered;

    private TreeModelSort sorted;

    private DataColumnString tagColumn;

    private DataColumnString displayColumn;

    private TreeSelection selection;

    private String code;

    DictionarySelectionWindow(LanguageSelectionButton button) {
        super();
        window = this;
        enclosing = button;

        setupWindow();
        buildModel();
        setupView();
        setupStatusbar();

        populateModel();

        hookupKeyboardSignals();
        hookupSelectionSignals();

        initialPresentation();
    }

    private void setupWindow() {
        window.setDecorated(false);
        window.setTitle("Languages");
        window.setTypeHint(WindowTypeHint.DIALOG);
        window.setDefaultSize(-1, 400);

        top = new VBox(false, 0);
        window.add(top);
    }

    /*
     * We aren't actually present()ing the window here; but we need to call
     * realize() to cause the allocation cycle to run otherwise the
     * LanguageSelctionButton ends up zero width. More importatnly, if we
     * show() the window at this point then under Compiz we get a ghost window
     * temporarily appearing on the screen.
     */
    private void initialPresentation() {
        top.showAll();
        window.realize();
        window.hide();
    }

    int getWidestDisplay() {
        final Allocation alloc;
        final int width;

        alloc = window.getAllocation();
        width = alloc.getWidth();

        return width;
    }

    private void buildModel() {
        tagColumn = new DataColumnString();
        displayColumn = new DataColumnString();

        store = new ListStore(new DataColumn[] {
                tagColumn,
                displayColumn
        });
        filtered = new TreeModelFilter(store, null);
        sorted = new TreeModelSort(filtered);
    }

    private void setupView() {
        final TreeViewColumn vertical;
        final CellRendererText renderer;
        final ScrolledWindow scroll;

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

        scroll = new ScrolledWindow();
        scroll.add(view);
        scroll.setPolicy(PolicyType.NEVER, PolicyType.AUTOMATIC);
        top.packStart(scroll, true, true, 0);
    }

    private void setupStatusbar() {
        status = new Statusbar();
        status.setHasResizeGrip(false);
        status.setBorderWidth(0);

        top.packStart(status, false, false, 0);
    }

    private void populateModel() {
        final String[] list;
        int i;

        list = Enchant.listDictionaries();

        for (i = 0; i < list.length; i++) {
            insertTagIntoModel(list[i]);
        }
    }

    /**
     * This is called during normal population of the popup, of course, but is
     * also available to be called if the document specifies a language we
     * don't know about.
     */
    private void insertTagIntoModel(String tag) {
        final LanguageTagTranslationTable table;
        final TreeIter row;
        final String languageCode;
        final String languageName;
        final String countryCode, countryName, displayName;

        table = LanguageTagTranslationTable.getInstance();

        /*
         * Parse [sic] the language tags to pull out ISO 639 language and ISO
         * 3166 country codes.
         */

        if (tag.length() == 2) {
            languageCode = tag;
            countryCode = null;
        } else if (tag.length() == 5) {
            languageCode = tag.substring(0, 2);
            countryCode = tag.substring(3, 5);
        } else {
            /*
             * There's nothing wrong with an Enchant lang_tag being longer
             * than "fr_CA", but how do we handle it?
             */
            languageCode = null;
            countryCode = null;
        }

        languageName = table.getLanguageName(languageCode);
        countryName = table.getCountryName(countryCode);

        if (languageName == null) {
            /*
             * Uncommon but possible case where the document language is
             * something we have no understanding of.
             */
            displayName = _("Unknown");
        } else if (countryName == null) {
            displayName = translateLanguageName(languageName);
        } else {
            displayName = translateLanguageName(languageName) + " (" + translateCountryName(countryName)
                    + ")";
        }

        row = store.appendRow();
        store.setValue(row, tagColumn, tag);
        store.setValue(row, displayColumn, displayName);
    }

    private void hookupKeyboardSignals() {
        window.connect(new Widget.KeyPressEvent() {

            public boolean onKeyPressEvent(Widget source, EventKey event) {
                Keyval key;

                key = event.getKeyval();
                if (key == Keyval.Escape) {
                    window.hide();
                    return true;
                }

                return false;
            }
        });
    }

    private void hookupSelectionSignals() {
        selection = view.getSelection();
        selection.setMode(SelectionMode.BROWSE);
        selection.unselectAll();

        view.connect(new TreeView.RowActivated() {
            public void onRowActivated(TreeView source, TreePath path, TreeViewColumn vertical) {
                final TreeIter row;
                final String code, display;

                window.hide();

                row = sorted.getIter(path);

                code = sorted.getValue(row, tagColumn);
                display = sorted.getValue(row, displayColumn);

                enclosing.setLanguage(code, display);
            }
        });

        selection.connect(new TreeSelection.Changed() {
            public void onChanged(TreeSelection source) {
                final TreeIter row;
                final String tag;

                row = source.getSelected();
                tag = sorted.getValue(row, tagColumn);

                setTagOnStatusbar(tag);
            }
        });
    }

    private void setTagOnStatusbar(String tag) {
        status.setMessage(" " + tag);
    }

    /**
     * Called on initial document load.
     */
    void setCode(String code) {
        TreeIter row;
        final TreePath path;

        this.code = code;

        this.setTagOnStatusbar(code);
        row = findTag(sorted, code);

        if (row == null) {
            this.insertTagIntoModel(code);
            return;
        }

        /*
         * Setting the selection is not enough. We also need to set the
         * "cursor" which is the dotted marking of the current keyboard focus.
         */

        path = sorted.getPath(row);
        view.setCursor(path, null, false);
    }

    private TreeIter findTag(TreeModel model, String code) {
        final TreeIter row;
        String tag;

        row = model.getIterFirst();
        do {
            tag = model.getValue(row, tagColumn);
            if (code.equals(tag)) {
                return row;
            }
        } while (row.iterNext());

        return null;
    }

    /**
     * Called on initial document load.
     */
    String getDisplayForSelected() {
        final TreeIter row;
        final String display;

        row = findTag(store, code);

        if (row != null) {
            display = store.getValue(row, displayColumn);
            return display;
        } else {
            /*
             * If the language is unknown, we need to say so. But this should
             * never occur!
             */
            return _("Unknown language");
        }
    }
}

/**
 * Build a table of language code to standardized ("official") language names
 * and country code to country names.
 * 
 * <p>
 * The existence of the class is a shame; ideally we could do this work inside
 * java-gnome seeing as how the only reason to look up the ISO names is to use
 * them as <code>msgid</code>s in translations.
 * 
 * @author Andrew Cowie
 */
class LanguageTagTranslationTable
{
    private static WeakReference<LanguageTagTranslationTable> ref;

    static {
        ref = new WeakReference<LanguageTagTranslationTable>(null);
    }

    /**
     * Get the singleton instance of the translation table, reloading it if
     * necessary.
     */
    static LanguageTagTranslationTable getInstance() {
        LanguageTagTranslationTable table;

        table = ref.get();
        if (table == null) {
            table = new LanguageTagTranslationTable();
            ref = new WeakReference<LanguageTagTranslationTable>(table);
        }
        return table;
    }

    Map<String, String> languages;

    Map<String, String> countries;

    private LanguageTagTranslationTable() {
        // 185 languages
        languages = new HashMap<String, String>(190, 1.0f);

        // 246 countries
        countries = new HashMap<String, String>(255, 1.0f);

        try {
            loadLanguageNames();
            loadCountryNames();
        } catch (Exception e) {
            /*
             * Handle the file not being present...
             */
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void loadLanguageNames() throws ValidityException, ParsingException, IOException {
        final File source;
        final Builder parser;
        final Document doc;
        final Element root;
        final Elements children;
        String local;
        final int I;
        int i;
        Element child;
        Attribute attr;
        String code, name;

        source = new File("/usr/share/xml/iso-codes/iso_639.xml");
        parser = new Builder();
        doc = parser.build(source);

        root = doc.getRootElement();

        /*
         * Sanity check
         */

        local = root.getLocalName();
        if (!local.equals("iso_639_entries")) {
            throw new IllegalStateException();
        }

        /*
         * Iterate through list. For any entry with a _1 code, we store its
         * name (there are a fair number of languages that don't have a two
         * character code; some day we may need to support that by changing
         * the parser [sic] above).
         */

        children = root.getChildElements("iso_639_entry");
        I = children.size();
        for (i = 0; i < I; i++) {
            child = children.get(i);

            attr = child.getAttribute("iso_639_1_code");
            if (attr == null) {
                continue;
            }
            code = attr.getValue();

            attr = child.getAttribute("name");
            name = attr.getValue();

            languages.put(code, name);
        }
    }

    String getLanguageName(String code) {
        return languages.get(code);
    }

    private void loadCountryNames() throws ValidityException, ParsingException, IOException {
        final File source;
        final Builder parser;
        final Document doc;
        final Element root;
        final Elements children;
        String local;
        final int I;
        int i;
        Element child;
        Attribute attr;
        String code, name;

        source = new File("/usr/share/xml/iso-codes/iso_3166.xml");
        parser = new Builder();
        doc = parser.build(source);

        root = doc.getRootElement();

        /*
         * Sanity check
         */

        local = root.getLocalName();
        if (!local.equals("iso_3166_entries")) {
            throw new IllegalStateException();
        }

        /*
         * Iterate through list. For any entry with an alpha_2 code, we store
         * its name.
         */

        children = root.getChildElements("iso_3166_entry");
        I = children.size();
        for (i = 0; i < I; i++) {
            child = children.get(i);

            attr = child.getAttribute("alpha_2_code");
            if (attr == null) {
                continue;
            }
            code = attr.getValue();

            attr = child.getAttribute("name");
            name = attr.getValue();

            countries.put(code, name);
        }
    }

    String getCountryName(String code) {
        return countries.get(code);
    }
}
