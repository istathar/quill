/*
 * DocBookConverter.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.converter;

import markerpen.docbook.BookDocument;
import markerpen.docbook.Chapter;
import markerpen.docbook.Document;
import markerpen.docbook.Paragraph;
import markerpen.docbook.Section;
import markerpen.textbase.TextStack;

import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextIter;

import static org.gnome.gtk.TextBuffer.OBJECT_REPLACEMENT_CHARACTER;

/**
 * Build a DocBook XOM tree equivalent to the data in our textbase, ready for
 * subsequent serialization (and thence saving to disk).
 * 
 * @author Andrew Cowie
 */
/*
 * A big reason to have this in its own package is so that we are using only
 * the public interface of our docbook module. Secondarily is that if other
 * output formats grow up, this will be the place their converters can live.
 */
public class DocBookConverter
{
    public static Document buildTree(TextStack text) {
        final Document book;
        final Chapter chapter;
        final Section section;
        Paragraph para;

        book = new BookDocument();

        chapter = new Chapter();
        book.add(chapter);

        section = new Section();
        chapter.add(section);

        // FIXME actually iterate over the format changes!

        while (true) {
            para = new Paragraph(text.toString());
            section.add(para);
            break;
        }

        return book;
    }

    public static Document buildTree(TextBuffer buffer) {
        final Document book;
        final Chapter chapter;
        final Section section;
        final StringBuilder str;
        TextIter pointer;
        char ch;
        Paragraph para;

        book = new BookDocument();

        chapter = new Chapter();
        book.add(chapter);

        section = new Section();
        chapter.add(section);

        str = new StringBuilder();

        pointer = buffer.getIterStart();

        while (true) {
            /*
             * Close markup for formats that are now ending
             */
            // TODO
            if (pointer.endsLine()) {
                para = new Paragraph(str.toString());
                section.add(para);
                str.setLength(0);
            }

            if (pointer.isEnd()) {
                break;
            }

            /*
             * Open markup that represents formats that are now beginning.
             */
            // TODO
            /*
             * Finally, add the TextBuffer's content at this position, and
             * move to the next character... unless it's something special
             */

            ch = pointer.getChar();

            // TODO images
            if (ch == OBJECT_REPLACEMENT_CHARACTER) {
                continue;
            } else {
                str.append(ch);
            }

            pointer.forwardChar();
        }

        return book;
    }
}
