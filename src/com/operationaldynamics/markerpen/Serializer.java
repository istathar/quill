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
package com.operationaldynamics.markerpen;

import java.util.HashSet;

import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextIter;
import org.gnome.gtk.TextTag;

import static com.operationaldynamics.markerpen.Format.bold;
import static com.operationaldynamics.markerpen.Format.italics;
import static com.operationaldynamics.markerpen.Format.mono;

class Serializer
{
    static String extractToFile(TextBuffer buffer) {
        StringBuilder str;
        TextIter pointer;

        str = new StringBuilder();

        pointer = buffer.getIterStart();

        while (true) {
            /*
             * Close markup for formats that are now ending
             */

            if (pointer.endsTag(mono)) {
                str.append("`");
            }
            if (pointer.endsTag(bold)) {
                str.append("**");
            }
            if (pointer.endsTag(italics)) {
                str.append("_");
            }

            if (pointer.isEnd()) {
                break;
            }

            /*
             * While a single newline terminates a paragraph in Markdown, for
             * aesthetic purposes we'll double tap it to create a blank line
             * in the output textfile.
             */

            if (pointer.endsLine()) {
                str.append("\n");
            }

            /*
             * Open markup that represents formats that are now beginning.
             */

            if (pointer.beginsTag(Format.italics)) {
                str.append("_");
            }
            if (pointer.beginsTag(Format.bold)) {
                str.append("**");
            }
            if (pointer.beginsTag(Format.mono)) {
                str.append("`");
            }

            /*
             * Finally, add the TextBuffer's content at this position, and
             * move to the next character.
             */

            str.append(pointer.getChar());

            pointer.forwardChar();
        }

        return str.toString();
    }

    private static HashSet<TextTag> tags;

    static TextBuffer loadFile(String contents) {
        final TextBuffer buffer;
        int i;
        TextIter pointer, begin;
        char ch;

        buffer = new TextBuffer();

        if (contents.length() == 0) {
            return buffer;
        }

        i = 0;
        tags = new HashSet<TextTag>(4);
        pointer = buffer.getIterStart();

        while (i < contents.length()) {
            ch = contents.charAt(i);
            i++;
            if (ch == '_') {
                toggleFormat(italics);
                continue;
            }
            if (ch == '*') {
                if (contents.charAt(i) == '*') {
                    toggleFormat(bold);
                }
                continue;
            }
            if (ch == '`') {
                toggleFormat(mono);
                continue;
            }
            if (ch == '\n') {
                if (contents.charAt(i) == '\n') {
                    tags.clear();
                    continue;
                }
            }

            buffer.insert(pointer, String.valueOf(ch), tags);
        }

        return buffer;
    }

    private static void toggleFormat(TextTag format) {
        if (tags.contains(format)) {
            tags.remove(format);
        } else {
            tags.add(format);
        }
    }
}
