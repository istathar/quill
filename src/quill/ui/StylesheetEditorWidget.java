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

    /**
     * an instance of a renderer based on Style.
     */
    private RenderEngine engine;

    private MilimetreEntry topMargin, leftMargin, rightMargin, bottomMargin;

    private Entry serifFont, sansFont, monoFont, headingFont;

    private MilimetreEntry serifSize, sansSize, monoSize, headingSize;

    private RendererPicker rendererList;

    private TextComboBox paperList;

    private Label paperWidth, paperHeight;

    private MarginsDisplay page;

    private Button ok, revert;

    /**
     * SizeGroup to keep the subheading Labels aligned.
     */
    private final SizeGroup group;

    /**
     * Reference to the enclosing document Window.
     */
    private final PrimaryWindow primary;

    StylesheetEditorWidget(PrimaryWindow primary) {
        super(false, 0);
        top = this;

        this.primary = primary;
        this.group = new SizeGroup(SizeGroupMode.HORIZONTAL);

        setupHeading();
        setupRenderSelector();
        setupPaperSelector();
        setupFontPreview();
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

        rendererList = new RendererPicker(this, group);
        top.packStart(rendererList, false, false, 0);
    }

    private void setupPaperSelector() {
        final HBox sides;
        final VBox left;
        Label heading, label;
        final Table table;
        HBox box;

        sides = new HBox(false, 0);
        left = new VBox(false, 3);

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

        paperWidth = new Label("000.0 mm");
        paperHeight = new Label("000.0 mm");

        box = new KeyValueBox(group, label, paperList, false);
        left.packStart(box, false, false, 0);
        sides.packStart(left, false, false, 0);

        paperList.connect(new ComboBox.Changed() {
            public void onChanged(ComboBox source) {
                final String str;
                final Stylesheet replacement;

                str = paperList.getActiveText();
                replacement = style.createWithPaperSize(str);
                affect(replacement);
            }
        });

        /*
         * On the right, include an illustration of the page size, showing
         * dimensions.
         */

        page = new MarginsDisplay();
        page.setSizeRequest(130, 180);

        table = new Table(2, 2, false);
        table.attach(page, 0, 1, 0, 1, AttachOptions.SHRINK, AttachOptions.SHRINK, 0, 0);

        paperHeight.setAlignment(LEFT, CENTER);
        table.attach(paperHeight, 1, 2, 0, 1);

        paperWidth.setAlignment(CENTER, TOP);
        table.attach(paperWidth, 0, 1, 1, 2);

        /*
         * Ensure the whole thing floats in the center of the pane
         */

        sides.packStart(table, true, false, 0);

        /*
         * Now, the margins
         */

        heading = new Label("<b>Margins</b>");
        heading.setUseMarkup(true);
        heading.setAlignment(LEFT, CENTER);
        left.packStart(heading, false, false, 6);

        label = new Label("Top:");
        topMargin = new MilimetreEntry();
        box = new KeyValueBox(group, label, topMargin, false);
        left.packStart(box, false, false, 0);
        topMargin.connect(new MilimetreEntry.Changed() {
            public void onChanged(String value) {
                final Stylesheet replacement;

                replacement = style.changeMarginTop(value);
                affect(replacement);
            }
        });

        label = new Label("Left:");
        leftMargin = new MilimetreEntry();
        box = new KeyValueBox(group, label, leftMargin, false);
        left.packStart(box, false, false, 0);
        leftMargin.connect(new MilimetreEntry.Changed() {
            public void onChanged(String value) {
                final Stylesheet replacement;

                replacement = style.changeMarginLeft(value);
                affect(replacement);
            }
        });

        label = new Label("Right:");
        rightMargin = new MilimetreEntry();
        box = new KeyValueBox(group, label, rightMargin, false);
        left.packStart(box, false, false, 0);
        rightMargin.connect(new MilimetreEntry.Changed() {
            public void onChanged(String value) {
                final Stylesheet replacement;

                replacement = style.changeMarginRight(value);
                affect(replacement);
            }
        });

        label = new Label("Bottom:");
        bottomMargin = new MilimetreEntry();
        box = new KeyValueBox(group, label, bottomMargin, false);
        left.packStart(box, false, false, 0);
        bottomMargin.connect(new MilimetreEntry.Changed() {
            public void onChanged(String value) {
                final Stylesheet replacement;

                replacement = style.changeMarginBottom(value);
                affect(replacement);
            }
        });

        top.packStart(sides, false, false, 6);
    }

    private void setupFontPreview() {
        final HBox sides;
        final VBox left;
        HBox box;
        final Label heading;
        Label label;

        heading = new Label("<b>Fonts</b>");
        heading.setUseMarkup(true);
        heading.setAlignment(LEFT, CENTER);
        top.packStart(heading, false, false, 6);

        sides = new HBox(false, 0);
        left = new VBox(false, 3);

        label = new Label("Serif:");
        serifFont = new Entry();
        serifSize = new MilimetreEntry();
        box = new KeyValueBox(group, label, serifFont, false);
        box.packStart(serifSize, false, false, 0);
        left.packStart(box, false, false, 0);
        serifSize.connect(new MilimetreEntry.Changed() {
            public void onChanged(String value) {
                final Stylesheet replacement;

                replacement = style.changeSizeSerif(value);
                affect(replacement);
            }
        });

        label = new Label("Sans:");
        sansFont = new Entry();
        sansSize = new MilimetreEntry();
        box = new KeyValueBox(group, label, sansFont, false);
        box.packStart(sansSize, false, false, 0);
        left.packStart(box, false, false, 0);
        sansSize.connect(new MilimetreEntry.Changed() {
            public void onChanged(String value) {
                final Stylesheet replacement;

                replacement = style.changeSizeSans(value);
                affect(replacement);
            }
        });

        label = new Label("Mono:");
        monoFont = new Entry();
        monoSize = new MilimetreEntry();
        box = new KeyValueBox(group, label, monoFont, false);
        box.packStart(monoSize, false, false, 0);
        left.packStart(box, false, false, 0);
        monoSize.connect(new MilimetreEntry.Changed() {
            public void onChanged(String value) {
                final Stylesheet replacement;

                replacement = style.changeSizeMono(value);
                affect(replacement);
            }
        });

        label = new Label("Heading:");
        headingFont = new Entry();
        headingSize = new MilimetreEntry();
        box = new KeyValueBox(group, label, headingFont, false);
        box.packStart(headingSize, false, false, 0);
        left.packStart(box, false, false, 0);
        headingSize.connect(new MilimetreEntry.Changed() {
            public void onChanged(String value) {
                final Stylesheet replacement;

                replacement = style.changeSizeHeading(value);
                affect(replacement);
            }
        });

        sides.packStart(left, true, true, 0);
        top.packStart(sides, false, false, 6);
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

        ok.connect(new Button.Clicked() {
            public void onClicked(Button source) {
                final Folio replacement;

                replacement = folio.update(style);
                primary.apply(replacement);
                primary.switchToEditor();
                primary.switchToPreview();
            }
        });
    }

    private Folio folio;

    void affect(Folio folio) {
        final Stylesheet style;

        this.folio = folio;

        style = folio.getStylesheet();
        affect(style);
    }

    void affect(Stylesheet style) {
        String str;
        double width, height;

        if (style == this.style) {
            return;
        }
        this.style = style;

        try {
            engine = RenderEngine.createRenderer(style);
        } catch (ApplicationException ae) {
            throw new Error(ae);
        }

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

        str = style.getFontSerif();
        serifFont.setText(str);
        str = style.getSizeSerif();
        serifSize.setText(str);

        str = style.getFontSans();
        sansFont.setText(str);
        str = style.getSizeSans();
        sansSize.setText(str);

        str = style.getFontMono();
        monoFont.setText(str);
        str = style.getSizeMono();
        monoSize.setText(str);

        str = style.getFontHeading();
        headingFont.setText(str);
        str = style.getSizeHeading();
        headingSize.setText(str);

        page.setStyle(engine);
        page.queueDraw();

        width = engine.getPageWidth();
        height = engine.getPageHeight();
        paperWidth.setLabel(convertPageSize(width) + " mm");
        paperHeight.setLabel(convertPageSize(height) + " mm");
    }

    private static String convertPageSize(double points) {
        final double mm;
        final String trim;

        mm = points / 72.0 * 25.4;
        trim = MilimetreEntry.constrainDecimal(mm);
        return trim;
    }

    public void grabDefault() {
        ok.grabFocus();
        ok.grabDefault();
    }

    /**
     * Read the current values from all UI elements, compose a new Stylesheet,
     * and apply.
     */
    void processFields() {
        final String rendererClass, paperSize, marginTop, marginLeft, marginRight, marginBottom, fontSerif, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono, sizeHeading;
        final Stylesheet replacement;

        rendererClass = rendererList.getSelectedRenderer();

        paperSize = paperList.getActiveText();

        marginTop = topMargin.getText();
        marginLeft = leftMargin.getText();
        marginRight = rightMargin.getText();
        marginBottom = bottomMargin.getText();

        fontSerif = serifFont.getText();
        fontSans = sansFont.getText();
        fontMono = monoFont.getText();
        fontHeading = headingFont.getText();

        sizeSerif = serifSize.getText();
        sizeSans = sansSize.getText();
        sizeMono = monoSize.getText();
        sizeHeading = headingSize.getText();

        replacement = new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, marginRight,
                marginBottom, fontSerif, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono,
                sizeHeading);

        affect(replacement);
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

    KeyValueBox(SizeGroup size, Label label, Widget value, Widget suffix) {
        this(size, label, value, false);
        super.packStart(suffix, false, false, 3);
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

    RendererPicker(final StylesheetEditorWidget parent, final SizeGroup size) {
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
                renderer.setLabel("<tt>" + str + "</tt>");

                if (combo.getHasFocus()) {
                    parent.processFields();
                }
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

    /**
     * Access the Java class name that has been selected by this Widget.
     */
    String getSelectedRenderer() {
        final TreeIter row;
        final String str;

        row = combo.getActiveIter();
        str = model.getValue(row, classColumn);

        return str;
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

        model.setValue(row, classColumn, typeName);
    }
}

class MarginsDisplay extends DrawingArea
{
    private final DrawingArea drawing;

    private RenderEngine engine;

    MarginsDisplay() {
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
    void setStyle(RenderEngine engine) {
        this.engine = engine;
    }

    private static final double BUMP = 50.0;

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

        scaleWidth = pixelWidth / (pageWidth + (2.0 * BUMP) + 10.0);
        scaleHeight = pixelHeight / (pageHeight + (2.0 * BUMP) + 10.0);

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

        cr.rectangle(0.0, 0.0, pageWidth, pageHeight);
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

        cr.setLineWidth(2.0);

        /*
         * Horizontal
         */

        cr.moveTo(0.0, pageHeight + BUMP);
        cr.lineRelative(pageWidth, 0.0);
        cr.stroke();

        // left
        cr.moveTo(0.0, pageHeight + BUMP);
        cr.lineRelative(25.0, -15.0);
        cr.lineRelative(0.0, 30.0);
        cr.closePath();
        cr.fill();

        cr.moveTo(0.0, pageHeight + BUMP - 20.0);
        cr.lineRelative(0.0, 40.0);
        cr.stroke();

        // right
        cr.moveTo(pageWidth, pageHeight + BUMP);
        cr.lineRelative(-25.0, -15.0);
        cr.lineRelative(0.0, 30.0);
        cr.closePath();
        cr.fill();

        cr.moveTo(pageWidth, pageHeight + BUMP - 20.0);
        cr.lineRelative(0.0, 40.0);
        cr.stroke();

        /*
         * Vertical
         */

        cr.moveTo(pageWidth + BUMP, 0.0);
        cr.lineRelative(0.0, pageHeight);
        cr.stroke();

        // top
        cr.moveTo(pageWidth + BUMP, 0.0);
        cr.lineRelative(-15.0, 25.0);
        cr.lineRelative(30.0, 0.0);
        cr.closePath();
        cr.fill();

        cr.moveTo(pageWidth + BUMP - 20.0, 0.0);
        cr.lineRelative(40.0, 0.0);
        cr.stroke();

        // bottom
        cr.moveTo(pageWidth + BUMP, pageHeight);
        cr.lineRelative(-15.0, -25.0);
        cr.lineRelative(30.0, 0.0);
        cr.closePath();
        cr.fill();

        cr.moveTo(pageWidth + BUMP - 20.0, pageHeight);
        cr.lineRelative(40.0, 0.0);
        cr.stroke();
    }
}
