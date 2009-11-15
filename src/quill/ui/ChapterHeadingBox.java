/*
 * ChapterHeadingBox.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.gtk.TextBuffer;

import quill.textbase.Segment;

import static quill.client.Quill.ui;

public class ChapterHeadingBox extends HeadingBox
{
    public ChapterHeadingBox(Segment segment) {
        super(segment);
        final EditorTextView editor;
        label.setLabel("Chapter");

        editor = this.getEditor();
        editor.buffer.connect(new TextBuffer.Changed() {
            public void onChanged(TextBuffer source) {
                ui.primary.updateTitle();
            }
        });
    }
}
