/*
 * DemoWindow.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.ui;

import org.gnome.gdk.Event;
import org.gnome.gtk.Entry;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextView;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.WrapMode;
import org.gnome.pango.FontDescription;

import static textview.LoremIpsum.text;

public class DemoWindow extends Window
{
    private final Window window;

    private DemoWindow() {
        final HBox two;
        final VBox left;
        HBox spread;
        final ScrolledWindow editor;
        final PreviewWidget preview;

        window = this;
        window.setMaximize(true);

        /*
         * LHS
         */
        left = new VBox(false, 3);

        spread = new HBox(false, 3);
        spread.packStart(new Entry("Chapter 1"));
        spread.packEnd(new Label("Chapter"));

        left.packStart(spread, false, false, 0);

        int i;

        for (i = 1; i < 5; i++) {
            TextView view;
            TextBuffer buffer;
            FontDescription desc;

            spread = new HBox(false, 3);
            spread.packStart(new Entry("Title " + i));
            spread.packEnd(new Label("Section"));

            left.packStart(spread, false, false, 0);

            view = new TextView();
            view.setWrapMode(WrapMode.WORD);
            view.setPaddingBelowParagraph(10);
            desc = new FontDescription("Deja Vu Sans, 11");
            view.modifyFont(desc);
            view.setBorderWidth(2);

            view.setLeftMargin(3);

            buffer = view.getBuffer();
            buffer.setText(text);

            left.packStart(view, false, false, 0);
        }

        editor = new ScrolledWindow();
        editor.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS);
        editor.addWithViewport(left);

        /*
         * RHS
         */

        preview = new PreviewWidget(text);

        /*
         * Setup window
         */

        two = new HBox(true, 6);
        two.packStart(editor, true, true, 0);
        two.packStart(preview, true, true, 0);

        window.add(two);

        window.setTitle("Mockup");
        window.setDefaultSize(800, 600);

        window.showAll();

        window.connect(new Window.DeleteEvent() {
            public boolean onDeleteEvent(Widget source, Event event) {
                Gtk.mainQuit();
                return false;
            }
        });
    }

    public static void main(String[] args) {
        Gtk.init(args);

        new DemoWindow();

        Gtk.main();
    }
}
