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

import org.gnome.gdk.Color;
import org.gnome.gtk.Alignment;
import org.gnome.gtk.AttachOptions;
import org.gnome.gtk.DrawingArea;
import org.gnome.gtk.Entry;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.StateType;
import org.gnome.gtk.Table;
import org.gnome.gtk.TextComboBoxEntry;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;

import parchment.format.Stylesheet;
import quill.textbase.Folio;

import static org.gnome.gtk.Alignment.CENTER;
import static org.gnome.gtk.Alignment.LEFT;
import static org.gnome.gtk.Alignment.RIGHT;

/**
 * UI for presenting and editing the active Stylesheet
 * 
 * @author Andrew Cowie
 */
class StylesheetEditorWidget extends VBox
{
    /**
     * Reference to self
     */
    private final VBox top;

    /**
     * The current Stylesheet
     */
    private Stylesheet style;

    private Entry topMargin, leftMargin, rightMargin, bottomMargin;

    private TextComboBoxEntry renderer;

    /**
     * SizeGroup to keep the subheading Labels aligned.
     */
    private final SizeGroup size;

    StylesheetEditorWidget(PrimaryWindow primary) {
        super(false, 0);
        top = this;
        size = new SizeGroup(SizeGroupMode.HORIZONTAL);

        setupRenderSelector();
        setupMarginPreview();
    }

    private void setupRenderSelector() {
        final HBox box;
        final Label heading, label;
        final Entry entry;

        box = new HBox(false, 0);

        heading = new Label("<b>Render Engine</b>");
        heading.setUseMarkup(true);
        heading.setAlignment(LEFT, CENTER);
        top.packStart(heading, false, false, 6);

        label = new Label("Class:");
        box.packStart(label, false, false, 3);
        size.add(label);

        renderer = new TextComboBoxEntry();
        renderer.appendText("parchment.render.ReportRenderEngine");
        renderer.setActive(0);
        entry = (Entry) renderer.getChild();
        entry.setWidthChars(50);
        entry.selectRegion(0, 0);
        entry.setPosition(0);
        box.packStart(renderer, false, false, 3);

        top.packStart(box, false, false, 0);
    }

    private void setupMarginPreview() {
        final Label heading;
        final Table table;
        final Widget page;
        Widget widget;
        final Alignment align;

        heading = new Label("<b>Margins</b>");
        heading.setUseMarkup(true);
        heading.setAlignment(LEFT, CENTER);
        top.packStart(heading, false, false, 6);

        /*
         * Surround a representation of a page with Entries for the margin
         * values.
         */

        table = new Table(3, 3, false);

        topMargin = new Entry();
        widget = positionMarginEntry(topMargin, CENTER, CENTER);
        table.attach(widget, 1, 2, 0, 1, AttachOptions.SHRINK, AttachOptions.SHRINK, 0, 0);

        leftMargin = new Entry();
        widget = positionMarginEntry(leftMargin, RIGHT, CENTER);
        table.attach(widget, 0, 1, 1, 2, AttachOptions.SHRINK, AttachOptions.SHRINK, 0, 0);

        rightMargin = new Entry();
        widget = positionMarginEntry(rightMargin, LEFT, CENTER);
        table.attach(widget, 2, 3, 1, 2, AttachOptions.SHRINK, AttachOptions.SHRINK, 0, 0);

        bottomMargin = new Entry();
        widget = positionMarginEntry(bottomMargin, CENTER, CENTER);
        table.attach(widget, 1, 2, 2, 3, AttachOptions.SHRINK, AttachOptions.SHRINK, 0, 0);

        // PLACEHOLDER
        page = new DrawingArea();
        page.setSizeRequest(215, 297);
        page.modifyBackground(StateType.NORMAL, Color.BLUE);
        table.attach(page, 1, 2, 1, 2, AttachOptions.SHRINK, AttachOptions.SHRINK, 0, 0);

        /*
         * Ensure the whole thing floats in the center of the pane
         */

        align = new Alignment(CENTER, CENTER, 0.0f, 0.0f);
        align.add(table);
        top.packStart(align, false, false, 6);
    }

    private static Alignment positionMarginEntry(final Entry entry, final float horizontal,
            final float vertical) {
        final Alignment align;
        final HBox box;
        final Label label;

        entry.setWidthChars(6);
        box = new HBox(false, 0);
        box.packStart(entry, false, false, 3);
        label = new Label("mm");
        box.packStart(label, false, false, 3);

        align = new Alignment(horizontal, vertical, 0.0f, 0.0f);
        align.setPadding(10, 10, 10, 10);
        align.add(box);

        return align;
    }

    void affect(Folio folio) {
        String str, text;

        style = folio.getStylesheet();

        // FIXME
        str = style.getRendererClass();
        text = renderer.getActiveText();
        if (!str.equals(text)) {
            renderer.appendText(str);
        }

        str = style.getMarginTop();
        topMargin.setText(str);

        str = style.getMarginLeft();
        leftMargin.setText(str);

        str = style.getMarginRight();
        rightMargin.setText(str);

        str = style.getMarginBottom();
        bottomMargin.setText(str);
    }
}
