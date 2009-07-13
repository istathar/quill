/*
 * SectionHeadingBox.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.gtk.Editable;
import org.gnome.gtk.Entry;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;

import quill.textbase.DeleteTextualChange;
import quill.textbase.Extract;
import quill.textbase.FullTextualChange;
import quill.textbase.Segment;
import quill.textbase.Span;
import quill.textbase.StringSpan;
import quill.textbase.TextChain;
import quill.textbase.TextualChange;

import static quill.client.Quill.data;

class HeadingBox extends HBox
{
    private HBox box;

    protected HeadingEditorTextView title;

    protected Label label;

    private TextChain chain;

    public HeadingBox(Segment segment) {
        super(false, 0);

        setupBox(segment);
        hookupChangeHandler();
    }

    private void setupBox(Segment segment) {
        box = this;

        title = new HeadingEditorTextView(segment);
        box.packStart(title, true, true, 0);

        label = new Label();
        label.setWidthChars(20);
        box.packEnd(label, false, false, 0);
    }

    /*
     * This is ad-hoc while we persist at having an Entry here as the Widget
     * for entering title te
     */
    private void hookupChangeHandler() {
        Entry junk;

        junk = new Entry();

        junk.connect(new Entry.Changed() {
            public void onChanged(Editable source) {
                TextualChange change;
                Extract entire;
                Span span;
                String str;

                entire = chain.extractAll();
                str = ((Entry) source).getText();

                if (str.length() == 0) {
                    change = new DeleteTextualChange(chain, 0, entire);
                } else {
                    span = new StringSpan(str, null);
                    change = new FullTextualChange(chain, 0, entire, span);
                }
                data.apply(change);
            }
        });
    }
}
