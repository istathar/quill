/*
 * Format.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.ui;

import markerpen.textbase.Common;
import markerpen.textbase.Markup;

import org.gnome.gtk.TextTag;
import org.gnome.pango.Style;
import org.gnome.pango.Weight;

/**
 * Format TextTags that can be applied to a TextBuffer which represent the
 * capabilities of the Markdown markup language.
 * 
 * The order that tags are created is very significant, as they are implicitly
 * added to a TextTagTable on creation, and their "priority" is, unless
 * otherwise altered, that order (last added highest priority).
 */
class Format
{
    static final TextTag italics;

    static final TextTag bold;

    static final TextTag filename;

    static final TextTag classname;

    static final TextTag function;

    static final TextTag hidden;

    static {
        filename = new TextTag();
        filename.setFamily("DejaVu Sans Mono");
        filename.setForeground("darkgreen");
        filename.setStyle(Style.ITALIC);

        classname = new TextTag();
        classname.setFamily("DejaVu Sans Mono");
        classname.setForeground("blue");

        function = new TextTag();
        function.setFamily("DejaVu Sans Mono");

        italics = new TextTag();
        italics.setStyle(Style.ITALIC);

        bold = new TextTag();
        bold.setWeight(Weight.BOLD);

        hidden = new TextTag();
        hidden.setInvisible(true);
    }

    /**
     * Keep this around as a reminder of what we'll do if we return multiple
     * tags covering block and inline format.
     * 
     * @deprecated
     */
    static TextTag[] tagsForMarkup(Markup markup) {
        final TextTag[] tags;

        if (markup == null) {
            return null;
        }

        tags = new TextTag[1];

        for (int i = 0; i < 1; i++) {
            tags[i] = tagForMarkup(markup);
        }

        return tags;
    }

    static TextTag tagForMarkup(Markup m) {
        if (m == null) {
            return null;
        }
        if (m instanceof Common) {
            if (m == Common.ITALICS) {
                return italics;
            } else if (m == Common.BOLD) {
                return bold;
            } else if (m == Common.FILENAME) {
                return filename;
            } else if (m == Common.CLASSNAME) {
                return classname;
            } else if (m == Common.FUNCTION) {
                return function;
            }
        }
        // else TODO

        throw new IllegalArgumentException("\n" + "Translation of " + m + " not yet implemented");
    }
}
