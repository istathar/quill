/*
 * CaptionEditorTextView.java, from the Quill and Parchment document editor.
 *
 * Copyright © 2009 Operational Dynamics Consulting Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License, version
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

import org.gnome.gtk.Justification;
import org.gnome.pango.FontDescription;
import org.gnome.pango.Style;

import quill.textbase.Segment;

/**
 * Text to be used as the explanation (not to mention body, on disk) of an
 * image or figure.
 * 
 * @author Andrew Cowie
 */
class CaptionEditorTextView extends EditorTextView
{
    private static FontDescription desc;

    static {
        desc = fonts.serif.copy();
        desc.setStyle(Style.ITALIC);
    }

    CaptionEditorTextView(Segment segment) {
        super(segment);

        view.modifyFont(desc);
        view.setMarginLeft(40);
        view.setMarginRight(40);
        view.setPaddingAboveParagraph(2);
        view.setPaddingBelowParagraph(4);
        view.setJustify(Justification.CENTER);
    }

    protected boolean isTabAllowed() {
        return false;
    }

    protected boolean isSpellChecked() {
        return false;
    }

    protected boolean isEnterAllowed() {
        return false;
    }
}
