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
import org.gnome.gtk.Justification;
import org.gnome.gtk.Label;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextView;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Viewport;
import org.gnome.gtk.Widget;
import org.gnome.gtk.WrapMode;

import static org.gnome.gtk.Alignment.LEFT;
import static org.gnome.gtk.Alignment.TOP;
import static org.gnome.gtk.SizeGroupMode.HORIZONTAL;

class NotesEditorWidget extends ScrolledWindow
{
    private ScrolledWindow scroll;

    private Adjustment adj;

    private VBox top;

    private SizeGroup group;

    /**
     * What is the top level UI holding this document?
     */
    private PrimaryWindow primary;

    NotesEditorWidget(PrimaryWindow primary) {
        super();
        scroll = this;
        this.primary = primary;

        setupScrolling();
        addHeading("Endnotes");
        mockupSeveralNotes();
        addHeading("References");
        mockupSeveralReferences();
    }

    private void setupScrolling() {
        final Viewport port;
        top = new VBox(false, 3);

        scroll.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS);
        scroll.addWithViewport(top);

        group = new SizeGroup(HORIZONTAL);
    }

    private void addHeading(String title) {
        final Label heading;

        heading = new Label();
        heading.setUseMarkup(true);
        heading.setLineWrap(true);
        heading.setLabel("<span size='xx-large'>" + title + "</span>");
        heading.setAlignment(LEFT, TOP);

        top.packStart(heading, false, false, 6);
    }

    private void mockupSeveralNotes() {
        Widget widget;

        widget = createFakeEndnote(
                "42",
                "From \"A Victory For Democracy\", an episode of the popular television series Yes Prime Minister. This passage taken from the book version [2], page 165.");
        top.packStart(widget, false, false, 0);
        widget = createFakeEndnote("43", "");
        top.packStart(widget, false, false, 0);
    }

    private void mockupSeveralReferences() {
        Widget widget;

        widget = createFakeEndnote(
                "[1]",
                "Andrew Cowie, \"Surviving Change\" in Proceedings of the 12th Australian Systems Administrators Conference (Brisbane: SAGE-AU, 2004), pages 23-40.");
        top.packStart(widget, false, false, 0);
        widget = createFakeEndnote(
                "[2]",
                "Antony Jay and Jonathan Lynn, The Complete Yes Prime Minister (London: BBC Books, 1989). ISBN 0563207736.");
        top.packStart(widget, false, false, 0);
        widget = createFakeEndnote(
                "[Lorem]",
                "Generator of typesetting text (the fake latin verse known as \"Lorum Ipsum\") available at http://www.lipsum.com");
        top.packStart(widget, false, false, 0);
    }

    /*
     * TODO change to single-line PropertyEditorTextView?
     */
    /*
     * TODO change body to a NormalEditorTextView? Constrained how?
     */
    private Widget createFakeEndnote(String left, String right) {
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
        ref.setJustify(Justification.RIGHT);
        vbox = new VBox(false, 0);
        vbox.packStart(ref, false, false, 0);
        hbox.packStart(vbox, false, false, 10);
        group.add(ref);

        two = new TextBuffer();
        two.setText(right);
        body = new TextView(two);
        body.setWrapMode(WrapMode.WORD);

        hbox.packStart(body, true, true, 0);
        return hbox;
    }

    void refreshDisplay() {}
}
