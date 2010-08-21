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
package quill.quack;

import java.util.ArrayList;

import nu.xom.Document;
import quill.textbase.Common;
import quill.textbase.ComponentSegment;
import quill.textbase.Extract;
import quill.textbase.HeadingSegment;
import quill.textbase.ImageSegment;
import quill.textbase.Markup;
import quill.textbase.NormalSegment;
import quill.textbase.PreformatSegment;
import quill.textbase.QuoteSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.Special;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

/**
 * Take a XOM tree (built using QuackNodeFactory and so having our
 * QuackElements) and convert it into our internal in-memory textchain
 * representation.
 * 
 * @author Andrew Cowie
 */
public class QuackLoader
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

    /**
     * If a whitespace was swollowed, then append this Span.
     */
    private Span pending;

    public QuackLoader() {
        list = new ArrayList<Segment>(5);
        chain = null;
    }

    /*
     * FIXME if Segments are to be immutable, then we need to switch the
     * creation time around.
     */
    private void setSegment(Segment next) {
        final Extract entire;

        entire = chain.extractAll();
        segment.setText(entire);
        list.add(segment);

        chain = new TextChain();
        segment = next;
    }

    /*
     * FIXME This assumes our documents have only one chapter in them. That's
     * not correct!
     */
    /*
     * This kinda assumes we only load one Document; if that's not the case,
     * and we don't force instantiating a new QuackLoader, then clear the
     * processor state here.
     */
    public Series process(Document doc) {
        final Component chapter;
        int j;
        Block[] blocks;

        chapter = (Component) doc.getRootElement();
        processComponent(chapter);

        /*
         * Now iterate over the Blocks.
         */

        blocks = chapter.getBlocks();

        for (j = 0; j < blocks.length; j++) {
            processBlock(blocks[j]);
        }

        /*
         * Finally, transpose the resultant collection of Segments into a
         * Series, and return it.
         */

        return new Series(list);
    }

    private void processComponent(Component component) {
        if (component instanceof ChapterElement) {
            start = true;
            preserve = false;
            setSegment(new ComponentSegment());
        } else {
            throw new UnsupportedOperationException("Implement support for <article>?");
        }
    }

    private void processBlock(Block block) {
        start = true;

        if (block instanceof TextElement) {
            preserve = false;
            if (segment instanceof NormalSegment) {
                chain.append(Span.createSpan('\n', null));
            } else {
                setSegment(new NormalSegment());
            }
        } else if (block instanceof CodeElement) {
            preserve = true;
            if (!(segment instanceof PreformatSegment)) {
                setSegment(new PreformatSegment());
            }
        } else if (block instanceof QuoteElement) {
            preserve = false;
            if (segment instanceof QuoteSegment) {
                chain.append(Span.createSpan('\n', null));
            } else {
                setSegment(new QuoteSegment());
            }
        } else if (block instanceof HeadingElement) {
            preserve = false;
            setSegment(new HeadingSegment());
        } else if (block instanceof ImageElement) {
            preserve = false;
            setSegment(new ImageSegment());

            processData(block);
        } else if (block instanceof TitleElement) {
            preserve = false;
            if (!(segment instanceof ComponentSegment)) {
                throw new IllegalStateException("\n"
                        + "The <title> must be the first block in a Quack <chapter>");
            }
        } else {
            throw new IllegalStateException("\n" + "What kind of Block is " + block);
        }

        processBody(block);
    }

    private void processData(final Block block) {
        final Meta[] data;
        int i;

        data = block.getData();
        for (i = 0; i < data.length; i++) {
            processMetadata(data[i]);
        }
    }

    private void processBody(final Block block) {
        Inline[] elements;
        int i;

        elements = block.getBody();
        for (i = 0; i < elements.length; i++) {
            processInline(elements[i]);
        }
    }

    private void processMetadata(final Meta meta) {
        final String str;

        if (meta instanceof SourceAttribute) {
            str = meta.getValue();
            segment.setImage(str);
        } else {
            throw new IllegalStateException("Unknown Meta type");
        }
    }

    private void processInline(Inline span) {
        final String str;

        str = span.getText();

        if (span instanceof Normal) {
            markup = null;
            processText(str);
        } else if (span instanceof InlineElement) {
            if (span instanceof FunctionElement) {
                markup = Common.FUNCTION;
            } else if (span instanceof FilenameElement) {
                markup = Common.FILENAME;
            } else if (span instanceof TypeElement) {
                markup = Common.TYPE;
            } else if (span instanceof LiteralElement) {
                markup = Common.LITERAL;
            } else if (span instanceof CommandElement) {
                markup = Common.COMMAND;
            } else if (span instanceof HighlightElement) {
                markup = Common.HIGHLIGHT;
            } else if (span instanceof ApplicationElement) {
                markup = Common.APPLICATION;
            } else if (span instanceof ItalicsElement) {
                markup = Common.ITALICS;
            } else if (span instanceof BoldElement) {
                markup = Common.BOLD;
            } else {
                throw new IllegalStateException("Unknown Inline type");
            }

            start = false;
            processText(str);
        } else if (span instanceof MarkerElement) {
            if (span instanceof NoteElement) {
                markup = Special.NOTE;
            } else if (span instanceof CiteElement) {
                markup = Special.CITE;
            }
            processMarker(str);
        }
    }

    /*
     * This will be the contiguous text body of the element until either a) an
     * nested (inline) element starts, or b) the end of the element is
     * reached. So we trim off the leading pretty-print whitespace then add a
     * single StringSpan with this content.
     */
    private void processText(String text) {
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
            chain.append(pending);
            pending = null;
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
            pending = Span.createSpan(' ', markup);
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

        if (str.equals("")) {
            throw new IllegalStateException("Can't have bare newlines in an otherwise empty element");
        }

        chain.append(createSpan(str, markup));
    }

    private void processMarker(String str) {
        chain.append(Span.createMarker(str, markup));
    }
}
