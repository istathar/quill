/*
 * DocBookLoader.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

import java.util.ArrayList;

import nu.xom.Document;
import quill.docbook.Application;
import quill.docbook.Block;
import quill.docbook.Blockquote;
import quill.docbook.Book;
import quill.docbook.Chapter;
import quill.docbook.Command;
import quill.docbook.Component;
import quill.docbook.Division;
import quill.docbook.Emphasis;
import quill.docbook.Filename;
import quill.docbook.Function;
import quill.docbook.Inline;
import quill.docbook.Literal;
import quill.docbook.Paragraph;
import quill.docbook.ProgramListing;
import quill.docbook.Section;
import quill.docbook.Title;
import quill.docbook.Type;

/**
 * Process a XOM tree with our DocBookElements into our internal in-mempry
 * textchain representation.
 * 
 * @author Andrew Cowie
 */
public class DocBookLoader
{
    private final ArrayList<Segment> list;

    /**
     * The (single) formatting applicable at this insertion point.
     */
    private Markup markup;

    /**
     * Did we just enter a block level element?
     */
    private boolean start;

    /**
     * The current block level element wrapper
     */
    private Segment segment;

    /**
     * The current text body we are building up.
     */
    private TextChain chain;

    /**
     * Are we in a block where line endings and other whitespace is preserved?
     */
    private boolean preserve;

    /**
     * Did we trim a whitespace character (newline, specifically) from the end
     * of the previous Text?
     */
    private boolean space;

    public DocBookLoader() {
        list = new ArrayList<Segment>(5);
        chain = null;
    }

    private void setSegment(Segment segment) {
        chain = new TextChain();
        segment.setText(chain);
        this.segment = segment;
        list.add(segment);
    }

    /*
     * TODO This assumes our documents have only one chapter in them. That's
     * probably not correct.
     */
    /*
     * This kinda assumes we only load one Document; if that's not the case,
     * and we don't force instantiating a new DocBookLoader, then clear the
     * processor state here.
     */
    /*
     * FIXME what about the anonymous section leading a chapter?
     */
    Series process(Document doc) {
        final Book book; // FIXME change to an interface, or reject?
        final Component chapter;
        final Division[] sections;
        int i, j;
        Block[] blocks;
        final Segment[] result;

        book = (Book) doc.getRootElement();
        chapter = book.getComponents()[0];

        processComponent(chapter);

        sections = chapter.getDivisions();

        for (i = 0; i < sections.length; i++) {
            processDivision(sections[i]);

            blocks = sections[i].getBlocks();

            for (j = 0; j < blocks.length; j++) {
                processBlock(blocks[j]);
            }
        }

        result = new Segment[list.size()];
        list.toArray(result);

        return new Series(result);
    }

    private void processComponent(Component component) {
        if (component instanceof Chapter) {
            markup = null;
            start = true;
            preserve = false;
            setSegment(new ComponentSegment());
        }
        // TODO Article?
    }

    private void processDivision(Division division) {
        if (division instanceof Section) {
            markup = null;
            start = true;
            preserve = false;
            setSegment(new HeadingSegment());
        }
    }

    private void processBlock(Block block) {
        Inline[] spans;
        int i;

        /*
         * Block elements are so common that we handle them first and bail out
         * of here.
         */

        if (block instanceof Paragraph) {
            markup = null;
            start = true;
            preserve = false;
            if (segment instanceof ParagraphSegment) {
                chain.append(new CharacterSpan('\n', null));
            } else if (segment instanceof BlockquoteSegment) {
                chain.append(new CharacterSpan('\n', null));
            } else {
                setSegment(new ParagraphSegment());
            }
        } else if (block instanceof ProgramListing) {
            markup = null;
            start = true;
            preserve = true;
            if (!(segment instanceof PreformatSegment)) {
                setSegment(new PreformatSegment());
            }
        } else if (block instanceof Blockquote) {
            markup = null;
            start = true;
            preserve = false;
            setSegment(new BlockquoteSegment());
        } else if (block instanceof Title) {
            markup = null;
            start = true;
            preserve = false;
            if (segment instanceof HeadingSegment) {
                // kludge
                chain = new TextChain();
                segment.setText(chain);
            }
        }

        /*
         * Now handle the bodies of the Elements
         */

        spans = block.getSpans();
        for (i = 0; i < spans.length; i++) {
            handleSpans(spans[i]);
        }

    }

    private void handleSpans(Inline span) {

        /*
         * Otherwise we're dealing with an inline spanning element.
         */

        start = false;
        preserve = false;

        if (span instanceof Function) {
            markup = Common.FUNCTION;
        } else if (span instanceof Filename) {
            markup = Common.FILENAME;
        } else if (span instanceof Type) {
            markup = Common.TYPE;
        } else if (span instanceof Literal) {
            markup = Common.CODE;
        } else if (span instanceof Command) {
            markup = Common.COMMAND;
        } else if (span instanceof Application) {
            markup = Common.APPLICATION;
            // } else if (span instanceof UserInput) { // TODO
            // markup = Preformat.USERINPUT;
        } else if (span instanceof Emphasis) {
            final Emphasis element;

            /*
             * Work out if this is italics or bold. This is not the cleanest
             * code. Should isBold() move to the Inline interface?
             */

            element = (Emphasis) span;

            if (element.isBold()) {
                markup = Common.BOLD;
            } else {
                markup = Common.ITALICS;
            }
        } else {
            /*
             * No need to warn, really. The structure tags don't count. But if
             * we're losing semantic data, this is where its happening.
             */
            markup = null;
        }

        processText(span.getText());
    }

    /*
     * This will be the contiguous text body of the element until either a) an
     * nested (inline) element starts, or b) the end of the element is
     * reached. So we trim off the leading pretty-print whitespace then add a
     * single StringSpan with this content.
     */
    public void processText(String text) {
        final String trim, str;
        int len;
        char ch;

        len = text.length();

        /*
         * These two cases are common for structure tags
         */

        if (len == 0) {
            space = false;
            return; // empty
        }

        if (len == 1) {
            if (text.charAt(0) == '\n') {
                space = false;
                return; // empty
            }
        }

        /*
         * Do we think we're starting a block? If so, trim off the leading
         * newline. If we're starting an inline and we swollowed a trailing
         * space from the previous run of text, pad the inline with one space.
         */

        if (start) {
            start = false;
            space = false;
            text = text.substring(1);
            len--;
        } else if (space) {
            chain.append(new CharacterSpan(' ', null));
        }

        /*
         * Trim the trailing newline (if there is one) as it could be the
         * break before a close-element tag. We replace it with a space and
         * prepend it if we find it is just a linebreak separator between a
         * Text and an Inline when making the next Text node.
         */

        ch = text.charAt(len - 1);
        if (ch == '\n') {
            trim = text.substring(0, len - 1);
            len--;
            space = true;
        } else {
            trim = text;
            space = false;
        }

        /*
         * If not preformatted text, turn any interior newlines into spaces,
         * then add.
         */

        if (preserve) {
            str = trim;
        } else {
            str = trim.replace('\n', ' ');
        }

        chain.append(new StringSpan(str, markup));

        /*
         * And, having processed the inline, reset to normal.
         */

        markup = null;
    }
}
