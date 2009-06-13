/*
 * EfficientNoNodeFactory.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

import java.util.ArrayList;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;

/**
 * Use XOM as a front end to whatever XML parser and then work its callbacks
 * to extract text & format information. Fun name. Points out that we're not
 * actually building a NodeTree with this, but in fact building up a Chunk[]
 * that ultimately can be used to construct a Text.
 * 
 * @author Andrew Cowie
 */
public class EfficientNoNodeFactory extends NodeFactory
{
    private static final Nodes empty;

    static {
        empty = new Nodes();
    }

    private final ArrayList<Segment> list;

    /**
     * The (single) formatting applicable at this insertion point.
     */
    private Markup markup;

    /**
     * Did we just enter a block level element?
     */
    private boolean block;

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

    public EfficientNoNodeFactory() {
        list = new ArrayList<Segment>(5);
        chain = null;
    }

    public Series createSeries() {
        Segment[] segments;

        segments = new Segment[list.size()];
        list.toArray(segments);

        return new Series(segments);
    }

    private void setSegment(Segment segment) {
        chain = new TextChain();
        segment.setText(chain);
        this.segment = segment;
        list.add(segment);
    }

    /*
     * The rest of this class are overrides of the NodeFactory parent class.
     */

    /*
     * This will be the contiguous text body of the element until either a) an
     * nested (inline) element starts, or b) the end of the element is
     * reached. So we trim off the leading pretty-print whitespace then add a
     * single StringSpan with this content.
     */
    public Nodes makeText(String text) {
        final String trim, str;
        int len;
        char ch;

        len = text.length();

        /*
         * These two cases are common for structure tags
         */

        if (len == 0) {
            space = false;
            return empty;
        }

        if (len == 1) {
            if (text.charAt(0) == '\n') {
                space = false;
                return empty;
            }
        }

        /*
         * Do we think we're starting a block? If so, trim off the leading
         * newline. If we're starting an inline and we swollowed a trailing
         * space from the previous run of text, pad the inline with one space.
         */

        if (block) {
            block = false;
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

        /*
         * We end with the minimum to keep NodeFactory going.
         */

        return empty;
    }

    /*
     * The only state we carry around is what markup this element represents,
     * and whether to convert line endings. So we set those two things, and
     * then return.
     */
    public Element startMakingElement(String name, String namespace) {
        /*
         * Block elements are so common that we handle them first and bail out
         * of here.
         */

        if (name.equals("para")) {
            markup = null;
            block = true;
            preserve = false;
            if (segment instanceof ParagraphSegment) {
                chain.append(new CharacterSpan('\n', null));
            } else {
                setSegment(new ParagraphSegment());
            }
            return null;
        } else if (name.equals("programlisting")) {
            markup = null;
            block = true;
            preserve = true;
            if (!(segment instanceof PreformatSegment)) {
                setSegment(new PreformatSegment());
            }
            return null;
        } else if (name.equals("section")) {
            markup = null;
            block = true;
            preserve = false;
            setSegment(new HeadingSegment());
            return null;
        } else if (name.equals("chapter")) {
            markup = null;
            block = true;
            preserve = false;
            setSegment(new ComponentSegment());
            return null;
        } else if (name.equals("title")) {
            markup = null;
            block = true;
            preserve = false;
            if (segment instanceof HeadingSegment) {
                // kludge
                chain = new TextChain();
                segment.setText(chain);
            }
            return null;
        }

        /*
         * Otherwise we're dealing with an inline spanning element.
         */

        block = false;
        preserve = false;

        if (name.equals("function")) {
            markup = Common.FUNCTION;
        } else if (name.equals("filename")) {
            markup = Common.FILENAME;
        } else if (name.equals("type")) {
            markup = Common.TYPE;
        } else if (name.equals("code")) {
            markup = Common.CODE;
        } else if (name.equals("command")) {
            markup = Common.COMMAND;
        } else if (name.equals("application")) {
            markup = Common.APPLICATION;
        } else if (name.equals("userinput")) {
            markup = Preformat.USERINPUT;
        } else if (name.equals("application")) {
            markup = Common.APPLICATION;
        } else if (name.equals("emphasis")) {
            markup = Common.ITALICS;
            // TODO what about bold?
        } else {
            /*
             * No need to warn, really. The structure tags don't count. But if
             * we're losing semantic data, this is where its happening.
             */
            markup = null;
        }

        /*
         * Annoyingly, we need to return an element after all, otherwise
         * makeAttribute() never gets called.
         */
        return new Element("fake");
    }

    public Nodes makeAttribute(String name, String URI, String value, Attribute.Type type) {
        if (markup == Common.ITALICS) {
            if (name.equals("role")) {
                if (value.equals("bold")) {
                    markup = Common.BOLD;
                }
            }
        }
        return empty;
    }

    /*
     * From here on we're deliberately faking Builder out, only returning the
     * minimum necessary for it to still function.
     */

    public Element makeRootElement(String name, String namespace) {
        return new Element("fake");
    }

    public Nodes finishMakingElement(Element element) {
        return new Nodes(element);
    }

    public void finishMakingDocument(Document document) {

    }

    public Nodes makeComment(String text) {
        return empty;
    }

    public Nodes makeProcessingInstruction(String target, String data) {
        return empty;
    }

    public Nodes makeDocType(String rootElementName, String publicID, String systemID) {
        return empty;
    }
}
