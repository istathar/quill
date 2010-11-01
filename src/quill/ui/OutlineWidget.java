/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
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

import org.freedesktop.cairo.Antialias;
import org.freedesktop.cairo.Context;
import org.gnome.gdk.EventExpose;
import org.gnome.gtk.Alignment;
import org.gnome.gtk.Button;
import org.gnome.gtk.DrawingArea;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ReliefStyle;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.pango.EllipsizeMode;

import parchment.format.Chapter;
import parchment.format.Metadata;
import quill.textbase.ComponentSegment;
import quill.textbase.Extract;
import quill.textbase.Folio;
import quill.textbase.HeadingSegment;
import quill.textbase.ImageSegment;
import quill.textbase.PreformatSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.TextChain;

import static org.gnome.glib.Glib.markupEscapeText;
import static org.gnome.gtk.SizeGroupMode.HORIZONTAL;

/**
 * A table of contents style interface to navigate the document. This could be
 * a lot fancier, but the tree of buttons conveys the idea.
 * 
 * @author Andrew Cowie
 */
/*
 * TODO This is just a demonstration placeholder. It will need to be made live
 * and reactive to changes to the DataLayer. The code to generate the
 * "compressed lines" is horrendous.
 */
class OutlineWidget extends ScrolledWindow
{
    private final ScrolledWindow scroll;

    private VBox top;

    private Folio folio;

    private SizeGroup group;

    public OutlineWidget() {
        super();
        scroll = this;

        top = new VBox(false, 0);
        scroll.addWithViewport(top);
        scroll.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS);

        folio = null;
    }

    private void buildOutline() {
        Metadata meta;
        Chapter chapter;
        String str;
        Series series;
        HBox box;
        Segment segment;
        int i, j;
        Extract entire;
        StringBuilder buf;
        Button button;
        Label label;
        DrawingArea lines;

        if (folio == null) {
            return;
        }
        meta = folio.getMetadata();

        group = new SizeGroup(HORIZONTAL);

        buf = new StringBuilder();

        buf.append("<span size='xx-large'>");
        buf.append("<span font='Liberation Serif 12'>");
        buf.append('“');
        buf.append("</span>");
        buf.append(meta.getDocumentTitle());
        buf.append("<span font='Liberation Serif 12'>");
        buf.append('”');
        buf.append("</span>");
        buf.append("</span>");

        label = createHeadingLabel(buf.toString());

        box = new HBox(false, 0);
        box.packStart(label, false, false, 3);

        buf.setLength(0);
        buf.append(folio.getManuscript().getBasename());
        buf.append(".parchment");
        label = createFilenameLabel(buf.toString());
        box.packStart(label, true, true, 3);

        top.packStart(box, false, false, 3);

        for (j = 0; j < folio.size(); j++) {
            series = folio.getSeries(j);

            for (i = 0; i < series.size(); i++) {
                segment = series.getSegment(i);

                buf.setLength(0);
                box = new HBox(false, 0);

                if (segment instanceof ComponentSegment) {

                    entire = segment.getEntire();

                    buf.append("<span size='x-large'> ");
                    buf.append(entire.getText());
                    buf.append("</span>");

                    button = new Button();
                    button.setRelief(ReliefStyle.NONE);
                    label = createHeadingLabel(buf.toString());
                    button.add(label);
                    box.packStart(button, false, false, 0);

                    chapter = folio.getChapter(j);
                    str = chapter.getRelative();
                    label = createFilenameLabel(str);
                    label.setAlignment(Alignment.LEFT, Alignment.CENTER);
                    box.packStart(label, true, true, 0);

                } else if (segment instanceof HeadingSegment) {
                    entire = segment.getEntire();

                    buf = new StringBuilder();
                    buf.append("      ");
                    buf.append(entire.getText());

                    button = new Button();
                    button.setRelief(ReliefStyle.NONE);
                    label = createHeadingLabel(buf.toString());
                    button.add(label);
                    box.packStart(button, false, false, 0);

                } else if (segment instanceof ImageSegment) {
                    Image image;

                    image = new Image(images.graphic);
                    image.setAlignment(Alignment.LEFT, Alignment.TOP);
                    image.setPadding(40, 3);

                    box.packStart(image, false, false, 0);
                } else {
                    entire = segment.getEntire();

                    if (segment instanceof PreformatSegment) {
                        lines = new CompressedLines(entire, true);
                    } else {
                        lines = new CompressedLines(entire, false);
                    }
                    box.packStart(lines, false, false, 0);
                }

                top.packStart(box, false, false, 0);
            }
        }

        top.showAll();
    }

    private Label createHeadingLabel(String str) {
        final Label result;

        result = new Label(str);
        result.setUseMarkup(true);
        result.setAlignment(Alignment.LEFT, Alignment.TOP);

        /*
         * Ellipsize the text...
         */

        result.setEllipsize(EllipsizeMode.END);

        /*
         * Without this, the Label will ellipsize, but to the width being
         * driven by the CompressedLines. With this, the headings are slightly
         * wider than that width, and nicely overhand the lines on both sides.
         */

        result.setMaxWidthChars(32);

        group.add(result);

        return result;
    }

    private Label createFilenameLabel(String str) {
        final Label result;

        /*
         * It would be better if this colour was taken from quill.ui.Format,
         * seeing as how we're using the same visual as used for <filename>
         * markup in EditorTextView.
         */
        result = new Label("<span color='darkgreen'><tt>" + markupEscapeText(str) + "</tt></span>");
        result.setUseMarkup(true);

        result.setAlignment(Alignment.LEFT, Alignment.CENTER);

        result.setEllipsize(EllipsizeMode.START);

        return result;
    }

    /**
     * Given a Folio, display it.
     */
    /*
     * FIXME Mockup! We actually need to evaluate the Series(s) against the
     * ones being displayed, and rebuild if/as necessary, and presumably if
     * we're actually showing.
     */
    /*
     * At the moment this is horribly inefficient; we should have something
     * more dynamic that merely updates the Labels rather than wholesale
     * recreates everything.
     */
    void affect(Folio folio) {
        if (this.folio == folio) {
            return;
        }

        this.folio = folio;
    }

    /**
     * Request the Widget actually (re)build itself. This is only called to
     * make a state visible, not on every state update.
     */
    /*
     * Actually, if this whole thing was ExposeEvent driven, then we'd be
     * doing this there and just having people call queueDraw(). But as we
     * have strong Widgets in the composition of this display, we need to
     * reconstruct and repack.
     */
    void refreshDisplay() {
        for (Widget child : top.getChildren()) {
            top.remove(child);
        }

        buildOutline();
    }
}

class CompressedLines extends DrawingArea
{
    private int[] dots;

    private int num;

    private boolean pre;

    private static final int WIDTH = 200;

    private static final int SPACING = 4;

    CompressedLines(Extract entire, boolean program) {
        super();
        final DrawingArea self;
        final TextChain chain;
        Extract[] paras;
        String line;
        int j, i, words, width;

        self = this;

        chain = new TextChain(entire);
        paras = chain.extractParagraphs();
        dots = new int[paras.length];
        j = 0;

        for (Extract extract : paras) {
            if (extract == null) {
                continue;
            }
            line = extract.getText();
            i = -1;
            words = 0;

            do {
                i++;
                words++;
            } while ((i = line.indexOf(' ', i)) != -1);

            dots[j++] = words;
        }

        num = 1;
        width = 0;
        for (i = 0; i < dots.length; i++) {
            width += dots[i] + 2;
            if (width > WIDTH) {
                num++;
                width = 0;
            }
        }

        pre = program;

        self.setSizeRequest(40 + WIDTH, SPACING * num);

        this.connect(new Widget.ExposeEvent() {
            public boolean onExposeEvent(Widget source, EventExpose event) {
                final Context cr;
                int i, j, width, rem;

                cr = new Context(event);

                if (pre) {
                    cr.setSource(0.7, 0.7, 0.7);
                } else {
                    cr.setSource(0.5, 0.5, 0.5);
                }

                cr.setLineWidth(1.0);
                cr.setAntialias(Antialias.NONE);

                cr.moveTo(40, 0);
                width = 0;
                j = 0;

                for (i = 0; i < dots.length; i++) {
                    if (width + dots[i] > WIDTH) {
                        rem = WIDTH - width;
                        cr.lineRelative(rem, 0);
                        j++;
                        cr.moveTo(40, SPACING * j);
                        cr.lineRelative(dots[i] - rem, 0);
                        width = dots[i] - rem + 2;
                    } else {
                        cr.lineRelative(dots[i], 0);
                        width += dots[i] + 2;
                    }
                    cr.moveRelative(2, 0);
                }

                cr.stroke();

                return true;
            }
        });
    }
}
