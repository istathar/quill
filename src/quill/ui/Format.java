/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
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

import org.gnome.gtk.TextTag;
import org.gnome.pango.FontDescription;
import org.gnome.pango.Style;
import org.gnome.pango.Underline;
import org.gnome.pango.Weight;

import quill.textbase.Common;
import quill.textbase.Markup;
import quill.textbase.Preformat;
import quill.textbase.Special;

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

    static final TextTag code;

    static final TextTag application;

    static final TextTag command;

    static final TextTag highlight;

    static final TextTag publication;

    static final TextTag keyboard;

    static final TextTag proper;

    static final TextTag hidden;

    static final TextTag userinput;

    static final TextTag spelling;

    static {
        FontDescription desc;
        double size;

        filename = new TextTag();
        filename.setFontDescription(fonts.mono);
        filename.setForeground("darkgreen");
        filename.setStyle(Style.ITALIC);

        classname = new TextTag();
        classname.setFontDescription(fonts.sans);
        classname.setForeground("blue");

        function = new TextTag();
        function.setFontDescription(fonts.mono);
        function.setForeground("darkblue");

        code = new TextTag();
        code.setFontDescription(fonts.mono);

        application = new TextTag();
        application.setFontDescription(fonts.sans);
        application.setWeight(Weight.BOLD);
        application.setForeground("#444444");

        command = new TextTag();
        command.setFontDescription(fonts.mono);
        command.setWeight(Weight.BOLD);
        command.setForeground("#444444");

        highlight = new TextTag();
        highlight.setBackground("yellow");

        italics = new TextTag();
        italics.setStyle(Style.ITALIC);

        bold = new TextTag();
        bold.setWeight(Weight.BOLD);

        hidden = new TextTag();
        hidden.setInvisible(true);

        userinput = new TextTag();
        userinput.setWeight(Weight.BOLD);

        spelling = new TextTag();
        spelling.setUnderline(Underline.ERROR);

        publication = new TextTag();
        publication.setStyle(Style.ITALIC);

        keyboard = new TextTag();
        keyboard.setFontDescription(fonts.mono);
        keyboard.setWeight(Weight.BOLD);

        proper = new TextTag();

        /*
         * This is a hack; clearly we need something better to display.
         */
        desc = new FontDescription("Linux Libertine O C");

        proper.setFontDescription(desc);
        proper.setWeight(Weight.BOLD);
        size = fonts.serif.getSize();
        proper.setSize(size);
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
            } else if (m == Common.TYPE) {
                return classname;
            } else if (m == Common.FUNCTION) {
                return function;
            } else if (m == Common.APPLICATION) {
                return application;
            } else if (m == Common.COMMAND) {
                return command;
            } else if (m == Common.HIGHLIGHT) {
                return highlight;
            } else if (m == Common.PUBLICATION) {
                return publication;
            } else if (m == Common.KEYBOARD) {
                return keyboard;
            } else if (m == Common.ACRONYM) {
                return proper;
            } else if (m == Common.LITERAL) {
                return code;
            }
        } else if (m instanceof Preformat) {
            if (m == Preformat.USERINPUT) {
                return userinput;
            }
        } else if (m instanceof Special) {
            if (m == Special.NOTE) {
                return null;
            } else if (m == Special.CITE) {
                return null;
            }
        }
        // else TODO

        throw new IllegalArgumentException("\n" + "Translation of " + m + " not yet implemented");
    }
}
