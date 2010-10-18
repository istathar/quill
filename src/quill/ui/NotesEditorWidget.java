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

import org.gnome.gtk.Adjustment;
import org.gnome.gtk.HBox;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextView;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Viewport;
import org.gnome.gtk.Widget;
import org.gnome.gtk.WrapMode;

class NotesEditorWidget extends ScrolledWindow
{
    private ScrolledWindow scroll;

    private Adjustment adj;

    private VBox box;

    /**
     * What is the top level UI holding this document?
     */
    private PrimaryWindow primary;

    NotesEditorWidget(PrimaryWindow primary) {
        super();
        scroll = this;
        this.primary = primary;

        setupScrolling();
        mockupSeveralNotes();
    }

    private void setupScrolling() {
        final Viewport port;
        box = new VBox(false, 3);

        scroll.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS);
        scroll.addWithViewport(box);
    }

    private void mockupSeveralNotes() {
        Widget widget;

        widget = createFakeEndnote(
                "42",
                "From \"A Victory For Democracy\", an episode of the popular television series Yes Prime Minister. This passage taken from the book version [2], page 165.");
        box.packStart(widget, false, false, 0);
        widget = createFakeEndnote(
                "43",
                "Typesetting text (the fake latin verse) known as \"Lorum Ipsum\" created by an online generator program available at [3]");
        box.packStart(widget, false, false, 0);
        widget = createFakeEndnote("44", "");
        box.packStart(widget, false, false, 0);
    }

    /*
     * TODO change to single-line PropertyEditorTextView?
     */
    /*
     * TODO change body to a NormalEditorTextView? Constrained how?
     */
    private static Widget createFakeEndnote(String left, String right) {
        final HBox hbox;
        final VBox vbox;
        final TextView ref, body;
        final TextBuffer one, two;

        hbox = new HBox(false, 0);

        one = new TextBuffer();
        one.setText(left);
        ref = new TextView(one);
        ref.setAcceptsTab(false);
        ref.setWrapMode(WrapMode.NONE);
        ref.setMarginLeft(10);
        vbox = new VBox(false, 0);
        vbox.packStart(ref, false, false, 0);
        hbox.packStart(vbox, false, false, 10);

        two = new TextBuffer();
        two.setText(right);
        body = new TextView(two);
        body.setWrapMode(WrapMode.WORD);

        hbox.packStart(body, true, true, 0);
        return hbox;
    }

    void refreshDisplay() {}
}
