/*
 * DocBookConverter.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.converter;

/*
 * A big reason to have this in its own package is so that we are using only
 * the public interface of our docbook module. Secondarily is that if other
 * output formats grow up, this will be the place their converters can live.
 */

import java.io.File;
import java.io.IOException;

import markerpen.docbook.Block;
import markerpen.docbook.Bold;
import markerpen.docbook.BookDocument;
import markerpen.docbook.Chapter;
import markerpen.docbook.Document;
import markerpen.docbook.Filename;
import markerpen.docbook.Function;
import markerpen.docbook.Inline;
import markerpen.docbook.Italics;
import markerpen.docbook.Paragraph;
import markerpen.docbook.Section;
import markerpen.docbook.Type;
import markerpen.textbase.CharacterSpan;
import markerpen.textbase.Common;
import markerpen.textbase.EfficientNoNodeFactory;
import markerpen.textbase.Extract;
import markerpen.textbase.Markup;
import markerpen.textbase.Preformat;
import markerpen.textbase.Span;
import markerpen.textbase.StringSpan;
import markerpen.textbase.TextStack;
import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * Build a DocBook XOM tree equivalent to the data in our textbase, ready for
 * subsequent serialization (and thence saving to disk).
 * 
 * @author Andrew Cowie
 */
/*
 * Build up Elements character by character. While somewhat plodding, this
 * allows us to create new Paragraphs etc as newlines are encountered.
 */
public class DocBookConverter
{
    private final Document book;

    private final StringBuilder buf;

    /**
     * Current Section we are appending Paragraphs to
     */
    private Section section;

    /**
     * Current block we are building up.
     */
    private Block block;

    private Inline inline;

    public DocBookConverter() {
        book = new BookDocument();
        buf = new StringBuilder();
    }

    /**
     * Append a Text, which represents a Chapter (probably).
     * 
     * FUTURE as of this writing we have no modelling of Sections in the UI,
     * so we'll assume there is one (and only one) for now.
     * 
     */
    public void append(TextStack text) {
        final Chapter chapter;
        final Extract entire;
        final int num;
        int i, j, len;
        Span span;
        String str;
        char ch;
        Markup previous, markup;

        entire = text.extractAll();
        if (entire == null) {
            return;
        }

        chapter = new Chapter();
        book.add(chapter);

        section = new Section();
        chapter.add(section);

        num = entire.size();
        previous = entire.get(0).getMarkup();
        start(previous);

        for (i = 0; i < num; i++) {
            span = entire.get(i);

            markup = span.getMarkup();
            if (markup != previous) {
                finish();
                start(markup);
                previous = markup;
            }

            if (span instanceof CharacterSpan) {
                ch = span.getChar();
                process(ch);
            } else if (span instanceof StringSpan) {
                str = span.getText();
                len = str.length();
                for (j = 0; j < len; j++) {
                    process(str.charAt(j));
                }
            }
        }

        /*
         * Finally, we need to deal with the fact that TextStacks (like the
         * TextBuffers they back) do not end with a paragraph separator, so we
         * need to act to close out the last block.
         */
        process('\n');
    }

    public Document result() {
        return book;
    }

    /**
     * Start a new element. This is a somewhat complicated expression, as it
     * counts for the case of returning from Inline to Block as well as
     * nesting Inlines into Blocks.
     */
    private void start(Markup format) {
        /*
         * Are we returning from an inline to block level? If so, we're
         * already nested and can just reset the state and escape.
         */
        if (inline != null) {
            inline = null;
            return;
        }

        /*
         * Otherwise, we're either starting a new block or a new inline. Deal
         * with the Block cases first:
         */

        if (format == null) {
            block = new Paragraph();
            return;
        }

        /*
         * Failing that, we cover off all the the Inline cases:
         */

        if (inline == null) {
            if (format == Common.FILENAME) {
                inline = new Filename();
            } else if (format == Common.TYPE) {
                inline = new Type();
            } else if (format == Common.FUNCTION) {
                inline = new Function();
            } else if (format == Common.ITALICS) {
                inline = new Italics();
            } else if (format == Common.BOLD) {
                inline = new Bold();
            } else if (format == Preformat.USERINPUT) {
                // boom?
            } else {
                // boom!
            }
        }
    }

    /**
     * Add accumulated text to the pending element. Reset the accumulator.
     */
    private void finish() {
        if (buf.length() == 0) {
            /*
             * At the moment we have no empty tags, and so nothing that would
             * cause us to flush something with no content. When we do, we'll
             * handle it here.
             */
            return;
        }

        if (inline != null) {
            inline.add(buf.toString());
            block.add(inline);
        } else {
            block.add(buf.toString());
        }
        buf.setLength(0);
    }

    /**
     * Accumulate a character.
     */
    private void process(char ch) {
        if (ch == '\n') {
            finish();
            if (inline != null) {
                inline = null;
            }
            section.add(block);
            block = new Paragraph();
        } else {
            buf.append(ch);
        }
    }

    /*
     * This will be moving somewhere else, I expect
     */
    public static TextStack parseTree(File source) throws ValidityException, ParsingException,
            IOException {
        final Builder parser;
        final EfficientNoNodeFactory factory;

        factory = new EfficientNoNodeFactory();

        parser = new Builder(factory);
        parser.build(source);

        return factory.createText();
    }
}
