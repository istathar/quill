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

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.Matrix;
import org.gnome.gdk.EventExpose;
import org.gnome.gtk.Alignment;
import org.gnome.gtk.Allocation;
import org.gnome.gtk.AttachOptions;
import org.gnome.gtk.Button;
import org.gnome.gtk.ButtonBoxStyle;
import org.gnome.gtk.CellRendererPixbuf;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.ComboBox;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnPixbuf;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.DrawingArea;
import org.gnome.gtk.Entry;
import org.gnome.gtk.HBox;
import org.gnome.gtk.HButtonBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.Stock;
import org.gnome.gtk.Table;
import org.gnome.gtk.TextComboBox;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;

import parchment.format.Stylesheet;
import parchment.render.RenderEngine;
import quill.client.ApplicationException;
import quill.textbase.Folio;

import static org.gnome.gtk.Alignment.CENTER;
import static org.gnome.gtk.Alignment.LEFT;
import static org.gnome.gtk.Alignment.RIGHT;
import static org.gnome.gtk.Alignment.TOP;

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

    private RendererPicker rendererList;

    private TextComboBox paperList;

    private MarginsDisplay page;

    private Button ok, revert;

    /**
     * SizeGroup to keep the subheading Labels aligned.
     */
    private final SizeGroup size;

    StylesheetEditorWidget(PrimaryWindow primary) {
        super(false, 0);
        top = this;

        size = new SizeGroup(SizeGroupMode.HORIZONTAL);

        setupHeading();
        setupRenderSelector();
        setupPaperSelector();
        setupMarginPreview();
        setupActionButtons();
    }

    private void setupHeading() {
        final Label heading;

        heading = new Label();
        heading.setUseMarkup(true);
        heading.setLabel("<span size='xx-large'>Stylesheet</span>");
        heading.setAlignment(LEFT, TOP);

        top.packStart(heading, false, false, 6);
    }

    private void setupRenderSelector() {
        final Label heading;

        heading = new Label("<b>Render Engine</b>");
        heading.setUseMarkup(true);
        heading.setAlignment(LEFT, CENTER);
        top.packStart(heading, false, false, 6);

        rendererList = new RendererPicker(size);
        top.packStart(rendererList, false, false, 0);
    }

    private void setupPaperSelector() {
        final HBox box;
        final Label heading, label;

        heading = new Label("<b>Paper</b>");
        heading.setUseMarkup(true);
        heading.setAlignment(LEFT, CENTER);
        top.packStart(heading, false, false, 6);

        label = new Label("Size:");

        /*
         * TODO, replace this with a better source of sizes? Remember that
         * we're deliberately not showing every paper size under the sun, just
         * a couple obvious ones. There needs to be a java-gnome PaperSize
         * constant for it...
         */

        paperList = new TextComboBox();
        paperList.appendText("A4");
        paperList.appendText("Letter");
        paperList.setActive(0);

        box = new KeyValueBox(size, label, paperList, false);
        top.packStart(box, false, false, 0);
    }

    private void setupMarginPreview() {
        final HBox box;
        final Label label;
        final Table table;
        Widget widget;
        final Alignment align;

        label = new Label("Margins:");

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

        page = new MarginsDisplay(style);

        page.setSizeRequest(130, 180);
        table.attach(page, 1, 2, 1, 2, AttachOptions.SHRINK, AttachOptions.SHRINK, 0, 0);

        /*
         * Ensure the whole thing floats in the center of the pane
         */

        align = new Alignment(CENTER, CENTER, 0.0f, 0.0f);
        align.add(table);

        box = new KeyValueBox(size, label, align, true);
        label.setAlignment(RIGHT, TOP);
        label.setPadding(0, 10);
        top.packStart(box, false, false, 6);
    }

    private static Alignment positionMarginEntry(final Entry entry, final float horizontal,
            final float vertical) {
        final Alignment align;
        final HBox box;
        final Label label;

        entry.setWidthChars(6);
        entry.setAlignment(RIGHT);
        box = new HBox(false, 0);
        box.packStart(entry, false, false, 3);
        label = new Label("mm");
        box.packStart(label, false, false, 3);

        align = new Alignment(horizontal, vertical, 0.0f, 0.0f);
        align.setPadding(10, 10, 10, 10);
        align.add(box);

        return align;
    }

    private void setupActionButtons() {
        final HButtonBox box;

        box = new HButtonBox();
        box.setLayout(ButtonBoxStyle.END);
        box.setSpacing(6);

        revert = new Button(Stock.REVERT_TO_SAVED);
        box.packStart(revert, false, false, 0);

        ok = new Button(Stock.OK);
        ok.setCanDefault(true);
        ok.grabFocus();
        box.packStart(ok, false, false, 0);

        top.packEnd(box, false, false, 6);
    }

    void affect(Folio folio) {
        String str, text;

        style = folio.getStylesheet();

        // FIXME
        str = style.getRendererClass();

        str = style.getMarginTop();
        topMargin.setText(str);

        str = style.getMarginLeft();
        leftMargin.setText(str);

        str = style.getMarginRight();
        rightMargin.setText(str);

        str = style.getMarginBottom();
        bottomMargin.setText(str);

        page.setStyle(style);
    }

    public void grabDefault() {
        ok.grabFocus();
        ok.grabDefault();
    }
}

class KeyValueBox extends HBox
{
    /**
     * @param expand
     *            Whether or not to give extra space to value Widget
     */
    KeyValueBox(SizeGroup size, Label label, Widget value, boolean expand) {
        super(false, 0);

        super.packStart(label, false, false, 3);
        label.setAlignment(RIGHT, CENTER);
        size.add(label);

        super.packStart(value, expand, expand, 3);
    }
}

class RendererPicker extends VBox
{
    private final VBox top;

    private final DataColumnString nameColumn;

    private final DataColumnPixbuf defaultColumn;

    private final DataColumnString classColumn;

    private final ListStore model;

    private final ComboBox combo;

    private final Label renderer;

    RendererPicker(SizeGroup size) {
        super(false, 0);
        HBox box;
        Label label;
        CellRendererText text;
        CellRendererPixbuf image;
        top = this;

        label = new Label("Renderer:");

        model = new ListStore(new DataColumn[] {
                nameColumn = new DataColumnString(),
                defaultColumn = new DataColumnPixbuf(),
                classColumn = new DataColumnString()
        });

        combo = new ComboBox(model);
        text = new CellRendererText(combo);
        text.setMarkup(nameColumn);

        image = new CellRendererPixbuf(combo);
        image.setPixbuf(defaultColumn);
        image.setAlignment(Alignment.LEFT, Alignment.CENTER);

        combo.setSizeRequest(450, -1);

        /*
         * FIXME drive this based on some list of registered renderers!
         */

        populate("Manuscript", "Technical reports, conference papers, book manuscripts", true,
                "parchment.render.ReportRenderEngine");
        populate("Paperback Novel", "A printed novel, tradeback size", false, "FIXME");
        populate("School paper", "University paper or School term report", false, "FIXME");

        box = new KeyValueBox(size, label, combo, false);
        top.packStart(box, true, true, 0);

        combo.connect(new ComboBox.Changed() {
            public void onChanged(ComboBox source) {
                final TreeIter row;
                final String str;

                row = source.getActiveIter();

                str = model.getValue(row, classColumn);
                renderer.setLabel(str);
            }
        });

        /*
         * Now the display of the actual Java Class
         */

        label = new Label("Class:");

        renderer = new Label("package.Class");
        renderer.setAlignment(LEFT, CENTER);
        renderer.setUseMarkup(true);
        renderer.setPadding(4, 0);

        box = new KeyValueBox(size, label, renderer, false);
        top.packStart(box, false, false, 0);

        combo.setActive(0);
    }

    private void populate(String rendererName, String rendererDescription, boolean isDefault,
            String typeName) {
        final TreeIter row;

        row = model.appendRow();
        model.setValue(row, nameColumn, "<b>" + rendererName + "</b>" + (isDefault ? "  (default)" : "")
                + "\n<span size='small'><i>" + rendererDescription + "</i></span>");

        if (isDefault) {
            model.setValue(row, defaultColumn, null);
        } else {
            model.setValue(row, defaultColumn, null);
        }

        model.setValue(row, classColumn, "<tt>" + typeName + "</tt>");
    }
}

class MarginsDisplay extends DrawingArea
{
    private final DrawingArea drawing;

    private RenderEngine engine;

    MarginsDisplay(Stylesheet style) {
        drawing = this;

        drawing.connect(new Widget.ExposeEvent() {
            public boolean onExposeEvent(Widget source, EventExpose event) {
                final Context cr;

                cr = new Context(event);

                scaleOutput(cr, engine);
                drawPageOutline(cr, engine);
                drawPageDimensions(cr, engine);

                return true;
            }
        });
    }

    /*
     * FIXME We should not be creating a new RenderEngine! And, once again
     * this is NOT the place to be doing the valdiation trap.
     */
    void setStyle(Stylesheet style) {
        try {
            engine = RenderEngine.createRenderer(style);
        } catch (ApplicationException ae) {
            throw new Error(ae);
        }
    }

    private void scaleOutput(Context cr, final RenderEngine engine) {
        final Allocation rect;
        final Matrix matrix;
        final double scaleWidth, scaleHeight, scaleFactor;
        final double pageWidth, pageHeight;
        final double pixelWidth, pixelHeight;

        rect = this.getAllocation();

        pixelWidth = rect.getWidth();
        pixelHeight = rect.getHeight();

        pageWidth = engine.getPageWidth();
        pageHeight = engine.getPageHeight();

        scaleWidth = pixelWidth / (pageWidth + 10.0);
        scaleHeight = pixelHeight / (pageHeight + 10.0);

        if (scaleWidth > scaleHeight) {
            scaleFactor = scaleHeight;
        } else {
            scaleFactor = scaleWidth;
        }

        matrix = new Matrix();
        matrix.scale(scaleFactor, scaleFactor);

        if (scaleWidth > scaleHeight) {
            matrix.translate(((pixelWidth / scaleFactor) - pageWidth) / 2.0, 0.0);
        } else {
            matrix.translate(0.0, ((pixelHeight / scaleFactor) - pageHeight) / 2.0);
        }

        /*
         * Bump the image off of the top left corner.
         */
        matrix.translate(0.5, 1.5);
        cr.transform(matrix);
    }

    /*
     * This code is almost identical to that in PreviewWidget
     */
    private void drawPageOutline(Context cr, RenderEngine engine) {
        final double pageWidth, pageHeight;

        pageWidth = engine.getPageWidth();
        pageHeight = engine.getPageHeight();

        cr.rectangle(0, 0, pageWidth, pageHeight);
        cr.setSource(1.0, 1.0, 1.0);
        cr.fillPreserve();
        cr.setSource(0.0, 0.0, 0.0);
        cr.setLineWidth(1.0);
        cr.stroke();
    }

    private void drawPageDimensions(Context cr, RenderEngine engine) {
        final double pageWidth, pageHeight;

        pageWidth = engine.getPageWidth();
        pageHeight = engine.getPageHeight();

        cr.moveTo(0.0, 25.0);
        cr.lineTo(pageWidth, 25.0);
        cr.stroke();

        cr.moveTo(25.0, 0.0);
        cr.lineTo(25.0, pageHeight);
        cr.stroke();
    }
}
