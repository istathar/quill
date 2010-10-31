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

import org.gnome.gdk.EventFocus;
import org.gnome.gtk.Entry;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;

import parchment.format.Metadata;
import quill.textbase.Folio;

import static org.freedesktop.bindings.Internationalization._;
import static org.gnome.glib.Glib.markupEscapeText;
import static org.gnome.gtk.Alignment.CENTER;
import static org.gnome.gtk.Alignment.LEFT;
import static org.gnome.gtk.Alignment.TOP;

/**
 * UI for presenting and editing the active Metadata details.
 * 
 * @author Andrew Cowie
 */
// cloned from StylesheetEditorWidget
class MetadataEditorWidget extends VBox
{
    /**
     * Reference to self
     */
    private final VBox top;

    /**
     * The current details
     */
    private Metadata meta;

    private Entry documentTitle, authorName;

    private LanguageSelectionButton documentLang;

    private Label enchantCode;

    /**
     * SizeGroup to keep the subheading Labels aligned.
     */
    private final SizeGroup group;

    /**
     * Reference to the enclosing document Window.
     */
    private final PrimaryWindow primary;

    private Folio folio;

    /**
     * Are we in the midst of loading a new document? If so, ignore events.
     */
    private boolean loading;

    MetadataEditorWidget(PrimaryWindow primary) {
        super(false, 0);
        top = this;

        this.primary = primary;
        this.group = new SizeGroup(SizeGroupMode.HORIZONTAL);
        this.loading = false;

        setupHeading();
        setupDocumentProperties();
    }

    private void setupHeading() {
        final Label heading;

        heading = new Label();
        heading.setUseMarkup(true);
        heading.setLabel("<span size='xx-large'>" + _("Metadata") + "</span>");
        heading.setAlignment(LEFT, TOP);

        top.packStart(heading, false, false, 6);
    }

    private void setupDocumentProperties() {
        Label heading, label, suffix;
        HBox box;
        VBox pair;

        heading = new Label("<b>" + _("Document") + "</b>");
        heading.setUseMarkup(true);
        heading.setAlignment(LEFT, CENTER);
        top.packStart(heading, false, false, 6);

        label = new Label(_("Title") + ":");

        documentTitle = new Entry();
        documentTitle.setWidthChars(50);
        box = new KeyValueBox(group, label, documentTitle, false);
        top.packStart(box, false, false, 0);
        documentTitle.connect(new Entry.Activate() {
            public void onActivate(Entry source) {
                final String value;
                final Metadata replacement;

                if (loading) {
                    return;
                }

                value = source.getText();

                replacement = meta.changeDocumentTitle(value);
                propegateMetadataChange(replacement);
            }
        });
        documentTitle.connect(new Widget.FocusOutEvent() {
            public boolean onFocusOutEvent(Widget source, EventFocus event) {
                String value, existing;

                existing = meta.getDocumentTitle();
                value = documentTitle.getText();

                if (!existing.equals(value)) {
                    documentTitle.activate();
                }

                return false;
            }
        });

        pair = new VBox(false, 0);
        label = new Label(_("Language") + ":");

        documentLang = new LanguageSelectionButton(primary);
        suffix = new Label("<i>(" + _("for spell checking") + ")</i>");
        suffix.setUseMarkup(true);
        box = new KeyValueBox(group, label, documentLang, suffix);
        pair.packStart(box, false, false, 0);
        documentLang.connect(new LanguageSelectionButton.Changed() {
            public void onChanged(LanguageSelectionButton source) {
                final String value;
                final Metadata replacement;

                value = source.getCode();

                replacement = meta.changeDocumentLanguage(value);
                propegateMetadataChange(replacement);

                enchantCode.setLabel("<tt>" + markupEscapeText(value) + "</tt>");
            }
        });

        label = new Label(_("Dictionary") + ":");
        enchantCode = new Label("<tt>xx_YY</tt>");
        enchantCode.setUseMarkup(true);
        enchantCode.setAlignment(LEFT, CENTER);
        enchantCode.setPadding(4, 0);

        box = new KeyValueBox(group, label, enchantCode, false);
        pair.packStart(box, false, false, 0);
        top.packStart(pair, false, false, 6);

        label = new Label(_("Author") + ":");

        authorName = new Entry();
        authorName.setWidthChars(50);
        box = new KeyValueBox(group, label, authorName, false);
        top.packStart(box, false, false, 6);
        authorName.connect(new Entry.Activate() {
            public void onActivate(Entry source) {
                final String value;
                final Metadata replacement;

                if (loading) {
                    return;
                }

                value = source.getText();

                replacement = meta.changeAuthorName(value);
                propegateMetadataChange(replacement);
            }
        });
        authorName.connect(new Widget.FocusOutEvent() {
            public boolean onFocusOutEvent(Widget source, EventFocus event) {
                String value, existing;

                existing = meta.getDocumentTitle();
                value = authorName.getText();

                if (!existing.equals(value)) {
                    authorName.activate();
                }

                return false;
            }
        });
    }

    void initializeMetadata(Folio folio) {
        loading = true;
        this.affect(folio);
        loading = false;
    }

    void affect(Folio folio) {
        final Metadata meta;
        String str;

        this.folio = folio;

        meta = folio.getMetadata();
        if (meta == this.meta) {
            return;
        }
        this.meta = meta;

        str = meta.getDocumentTitle();
        documentTitle.setText(str);

        str = meta.getDocumentLanguage();
        documentLang.setCode(str);
        enchantCode.setLabel("<tt>" + markupEscapeText(str) + "</tt>");

        str = meta.getAuthorName();
        authorName.setText(str);
    }

    /**
     * Create a new Folio, propegate it, and update the preview.
     */
    private void propegateMetadataChange(Metadata meta) {
        final Folio replacement;

        replacement = folio.update(meta);
        primary.apply(replacement);
        primary.forceRefresh();
        primary.forceRecheck();
    }

    public void grabDefault() {
        documentTitle.grabFocus();
        documentTitle.selectRegion(0, 0);
        documentTitle.setPosition(0);
    }
}
