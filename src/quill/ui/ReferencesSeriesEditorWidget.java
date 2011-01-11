/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010-2011 Operational Dynamics Consulting, Pty Ltd
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

import java.util.List;

import org.gnome.gtk.Label;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;

import quill.textbase.EndnoteSegment;
import quill.textbase.ReferenceSegment;
import quill.textbase.Segment;

import static org.gnome.gtk.Alignment.LEFT;
import static org.gnome.gtk.Alignment.TOP;
import static org.gnome.gtk.SizeGroupMode.HORIZONTAL;

/**
 * Series editor for editing notes or references.
 * 
 * @author Andrew Cowie
 */
public class ReferencesSeriesEditorWidget extends SeriesEditorWidget
{
    private SizeGroup group;

    ReferencesSeriesEditorWidget(PrimaryWindow primary) {
        super(primary);
        setupLabels();
        addHeading("Endnotes");
        addHeading("References");
    }

    private void setupLabels() {
        group = new SizeGroup(HORIZONTAL);
    }

    private void addHeading(String title) {
        final Label heading;
        final VBox top;

        heading = new Label();
        heading.setUseMarkup(true);
        heading.setLineWrap(true);
        heading.setLabel("<span size='xx-large'>" + title + "</span>");
        heading.setAlignment(LEFT, TOP);

        top = super.getTop();
        top.packStart(heading, false, false, 6);
    }

    protected Widget createEditorForSegment(int index, Segment segment) {
        final PrimaryWindow primary;
        final Widget result;
        final EditorTextView editor;
        final ReferenceListitemBox listitem;
        final List<EditorTextView> editors;

        primary = super.getPrimary();

        if (segment instanceof EndnoteSegment) {
            listitem = new ReferenceListitemBox(this, segment);

            editor = listitem.getEditor();
            result = listitem;
        } else if (segment instanceof ReferenceSegment) {
            listitem = new ReferenceListitemBox(this, segment);

            editor = listitem.getEditor();
            result = listitem;
        } else {
            // skip!
            editor = null;
            result = null;
        }

        editors = super.getEditors();
        editors.add(index, editor);

        return result;
    }
}
