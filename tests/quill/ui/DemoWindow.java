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
package quill.ui;

import org.gnome.gdk.Event;
import org.gnome.gdk.EventExpose;
import org.gnome.gtk.Adjustment;
import org.gnome.gtk.Entry;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.WrapMode;
import org.gnome.pango.FontDescription;

import quill.textbase.Change;
import quill.textbase.InsertChange;
import quill.textbase.Preformat;
import quill.textbase.Span;
import quill.textbase.StringSpan;
import quill.textbase.TextStack;

public class DemoWindow extends Window
{
    private final Window window;

    private DemoWindow() {
        final FontDescription desc;
        final HBox two;
        final VBox left;
        HBox spread;
        final Entry chapter;
        final ScrolledWindow scroll;
        final PreviewWidget preview;

        window = this;
        window.setMaximize(true);
        desc = new FontDescription("Deja Vu Serif, 11");
        window.modifyFont(desc);

        /*
         * LHS
         */
        left = new VBox(false, 3);

        spread = new HBox(false, 3);
        chapter = new Entry("Chapter 1");
        chapter.modifyFont(desc);
        spread.packStart(chapter);
        spread.packEnd(new Label("Chapter"));

        left.packStart(spread, false, false, 0);

        int i;

        for (i = 1; i < 5; i++) {
            Entry entry;
            EditorTextView editor;
            ScrolledWindow wide;
            TextStack lorem, prog;

            spread = new HBox(false, 3);
            if (i == 1) {
                entry = new Entry("In the beginning");
            } else {
                entry = new Entry("Title " + i);
            }
            entry.modifyFont(desc);
            spread.packStart(entry);
            spread.packEnd(new Label("Section"));

            left.packStart(spread, false, false, 0);

            /*
             * some paras
             */

            editor = new ParagraphEditorTextView();
            editor.modifyFont(desc);

            lorem = demoLoremIpsum();
            editor.loadText(lorem);

            left.packStart(editor, false, false, 0);

            /*
             * some program listing
             */

            editor = new PreformatEditorTextView();
            editor.setWrapMode(WrapMode.NONE);

            prog = demoProgramListing();
            editor.loadText(prog);

            wide = new ScrolledWindow();
            wide.setPolicy(PolicyType.AUTOMATIC, PolicyType.NEVER);
            wide.add(editor);

            left.packStart(wide, false, false, 0);

            /*
             * more paras
             */

            editor = new ParagraphEditorTextView();

            lorem = demoLoremIpsum();
            editor.loadText(lorem);

            left.packStart(editor, false, false, 0);

        }

        scroll = new ScrolledWindow();
        scroll.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS);
        scroll.addWithViewport(left);

        scroll.connect(new ExposeEvent() {
            public boolean onExposeEvent(Widget source, EventExpose event) {
                Adjustment adj;

                adj = scroll.getVAdjustment();
                System.out.format("%4.0f + %3.0f = %4.0f to %4.0f\n", adj.getValue(), adj.getPageSize(),
                        adj.getValue() + adj.getPageSize(), adj.getUpper());
                return false;
            }
        });
        /*
         * RHS
         */

        preview = new PreviewWidget();

        /*
         * Setup window
         */

        two = new HBox(true, 6);
        two.packStart(scroll, true, true, 0);
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

    private TextStack demoLoremIpsum() {
        final TextStack result;
        final Span span;
        final Change change;

        result = new TextStack();

        span = new StringSpan(textview.LoremIpsum.text, null); // change

        change = new InsertChange(0, span);
        result.apply(change);

        return result;
    }

    private TextStack demoProgramListing() {
        final TextStack result;
        final Span span;
        final Change change;

        result = new TextStack();

        span = new StringSpan("public class Hello {\n"
                + "    public static void main(String[] args) {\n" + "        Gtk.init(args);\n"
                + "        Gtk.main();\n" + "    }\n" + "}", Preformat.NORMAL); // change

        change = new InsertChange(0, span);
        result.apply(change);

        return result;
    }
}
