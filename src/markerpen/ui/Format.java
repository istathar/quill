/*
 * EditorWindow.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.ui;

import java.util.ArrayList;

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
public class Format
{
    public static final TextTag italics;

    public static final TextTag bold;

    public static final TextTag mono;

    public static final TextTag[] tags;

    public static final TextTag hidden;

    static {
        ArrayList<TextTag> list;

        list = new ArrayList<TextTag>(4);

        mono = new TextTag();
        mono.setFamily("Mono");
        list.add(mono);

        italics = new TextTag();
        italics.setFamily("Serif");
        italics.setStyle(Style.ITALIC);
        list.add(italics);

        bold = new TextTag();
        bold.setWeight(Weight.BOLD);
        list.add(bold);

        hidden = new TextTag();
        hidden.setInvisible(true);

        tags = list.toArray(new TextTag[list.size()]);
    }
}
