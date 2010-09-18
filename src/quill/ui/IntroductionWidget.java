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

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.Extend;
import org.freedesktop.cairo.LinearPattern;
import org.freedesktop.cairo.Pattern;
import org.gnome.gdk.EventExpose;
import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Alignment;
import org.gnome.gtk.DrawingArea;
import org.gnome.gtk.Image;
import org.gnome.gtk.Justification;
import org.gnome.gtk.Label;
import org.gnome.gtk.LinkButton;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;

/**
 * Introduction widget. Instead of a splash screen, this starts out being
 * displayed by the PrimaryWindow. Mostly this is here to tell you that F1 is
 * what you need to press for help.
 * 
 * @author Andrew Cowie
 */
class IntroductionWidget extends VBox
{
    private final VBox top;

    IntroductionWidget() {
        super(false, 0);
        final Label title, description, help;
        final Pixbuf pixbuf;
        final Image image;
        Widget bar;
        Label warning;
        Alignment align;
        final LinkButton site;

        try {
            top = this;

            pixbuf = new Pixbuf("share/pixmaps/feather.png", 128, 128, true);
            image = new Image(pixbuf);
            image.setAlignment(Alignment.CENTER, Alignment.BOTTOM);
            top.packStart(image, false, false, 10);

            title = new Label("<span font='DejaVu Serif, 34' color='darkgreen'>Quill </span>"
                    + "<span font='Inconsolata, 36' color='darkblue'>and</span>"
                    + "<span font='Linux Libertine O, 40'> Parchment</span>");
            title.setUseMarkup(true);
            title.setJustify(Justification.CENTER);
            title.setAlignment(Alignment.CENTER, Alignment.TOP);
            top.packStart(title, false, false, 0);

            align = new Alignment(0.5f, 0.5f, 0.0f, 0.0f);
            site = new LinkButton(new URI("http://research.operationaldynamics.com/projects/quill/"));
            align.add(site);
            top.packStart(align, false, false, 0);

            description = new Label(
                    "This is <b>Quill</b>, a <u>W</u>hat <u>Y</u>ou <u>S</u>ee <u>I</u>s <u>W</u>hat <u>Y</u>ou <u>N</u>eed document editor, "
                            + "attempting to give you a clean display of your content and subtle indications of semantic markup."
                            + "\n\n"
                            + "<b>Parchment</b> a simple but powerful rendering "
                            + "back-end which takes these documents and outputs them as PDF files. "
                            + "Different document types have different rendering engines (ie, stylesheets) customized for the purpose.");
            description.setLineWrap(true);
            description.setUseMarkup(true);
            description.setJustify(Justification.CENTER);
            top.packStart(description, false, false, 20);

            help = new Label("Press F1 for help");
            help.setLineWrap(true);
            help.setUseMarkup(true);

            top.packStart(help, true, false, 0);

            bar = new UnderConstruction();
            top.packStart(bar, false, false, 0);

            warning = new Label(
                    "<b>Under Construction</b>\n\n"
                            + "This is experimental software and still under heavy development. "
                            + "It is <i>not</i> suitable for general use. "
                            + "Quill and Parchment <i>will</i> crash, "
                            + "and it <i>will</i> eat your documents, "
                            + "though if possible we attempt to save what you "
                            + "were doing in \"recovery\" files with a <tt>.RESCUED</tt> extension. "
                            + "The on-disk format of manuscripts and chapters is text based, "
                            + "so we encourage you to store your documents in Bazaar or another version control system.\n");
            warning.setLineWrap(true);
            warning.setUseMarkup(true);
            warning.setWidthChars(70);
            warning.setAlignment(Alignment.CENTER, Alignment.TOP);
            warning.setPadding(0, 10);
            warning.setJustify(Justification.CENTER);
            top.packStart(warning, false, false, 0);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new AssertionError();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }
}

class UnderConstruction extends Alignment
{
    private final Alignment align;

    UnderConstruction() {
        super(Alignment.CENTER, Alignment.CENTER, 1.0f, 0.0f);
        final DrawingArea drawing;
        align = this;

        drawing = new DrawingArea();
        drawing.setSizeRequest(-1, 30);

        drawing.connect(new Widget.ExposeEvent() {
            public boolean onExposeEvent(Widget source, EventExpose event) {
                final Context cr;
                final Pattern pattern;

                cr = new Context(event);

                pattern = new LinearPattern(0.0, 0.0, 20.0, 20.0);
                pattern.addColorStopRGB(0.0, 0.0, 0.0, 0.0);
                pattern.addColorStopRGB(0.5, 0.0, 0.0, 0.0);
                pattern.addColorStopRGB(0.5, 255.0 / 255.0, 196.0 / 255.0, 0.0 / 255.0);
                pattern.addColorStopRGB(1.0, 255.0 / 255.0, 196.0 / 255.0, 0.0 / 255.0);
                pattern.addColorStopRGB(1.0, 0.0, 0.0, 0.0);
                pattern.setExtend(Extend.REPEAT);

                cr.setSource(pattern);

                cr.paint();

                return false;
            }
        });

        align.add(drawing);
    }
}
