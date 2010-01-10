/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2009 Operational Dynamics Consulting, Pty Ltd
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
package quill.textbase;

import java.util.ArrayList;

/**
 * Add format to a range of Text.
 * 
 * @author Andrew Cowie
 */
public class FormatTextualChange extends TextualChange
{
    Markup format;

    /**
     * Toggle format in the given range. This means applying it, unless the
     * first Span in the Extract is that format, in which case toss it.
     */
    public FormatTextualChange(TextChain chain, int offset, Extract range, Markup format) {
        super(chain, offset, range, toggleMarkup(range, format));
        this.format = format;
    }

    private static class FirstSpanFinder implements SpanVisitor
    {
        private Markup markup;

        private FirstSpanFinder() {}

        public boolean visit(Span span) {
            markup = span.getMarkup();
            return true;
        }

        private Markup getFirst() {
            return markup;
        }
    }

    private static Extract toggleMarkup(Extract original, Markup format) {
        final FirstSpanFinder finder;
        final Markup markup;

        /*
         * Get the first Span's Markup
         */

        finder = new FirstSpanFinder();
        original.visit(finder);
        markup = finder.getFirst();

        // TODO change to handle instances rather than singletons
        if (markup == null) {
            return applyMarkup(original, format);
        } else if (markup == format) {
            return removeMarkup(original, format);
        } else {
            return applyMarkup(original, format);
        }
    }

    /**
     * Apply a given Markup to the specified tree. Does NOT apply that Markup
     * to any \n characters, but otherwise converts the range into a single
     * Span.
     */
    /*
     * This may not be ideal; if we already had only one StringSpan then we
     * won't reuse its backing String. We could actually fix this to be a
     * SpanVisitor if we could ENSURE that all '\n' were in CharacterSpans of
     * their own (or perhaps even a special NewlineSpan).
     */
    private static class Applicator implements CharacterVisitor
    {
        final StringBuilder str;

        final ArrayList<Span> list;

        final Markup replacement;

        private Applicator(int guess, Markup markup) {
            str = new StringBuilder(guess);
            list = new ArrayList<Span>();
            replacement = markup;
        }

        public boolean visit(int character, Markup markup) {
            Span span;

            if (character == '\n') {
                span = Span.createSpan(str.toString(), replacement);
                list.add(span);
                span = Span.createSpan('\n', null);
                list.add(span);
                str.setLength(0);
            } else {
                str.appendCodePoint(character);
            }

            return false;
        }

        private Node getTree() {
            Span span;
            Node node;
            int i;

            /*
             * Handle remaining characters
             */

            span = Span.createSpan(str.toString(), replacement);
            list.add(span);

            /*
             * TODO replace with an efficient list -> tree builder?
             */

            span = list.get(0);
            node = Node.createNode(span);

            for (i = 1; i < list.size(); i++) {
                span = list.get(i);
                node = node.append(span);
            }

            return node;
        }
    }

    private static Extract applyMarkup(final Extract original, final Markup format) {
        Applicator tourist;
        Node node;

        tourist = new Applicator(original.getWidth(), format);
        original.visit(tourist);
        node = tourist.getTree();

        return node;
    }

    /**
     * Remove a Markup from a range of text. While this normally is just
     * reverting to clear, it is implemented somewhat different that the
     * applyMarkup() case above: this one removes the specified format ONLY,
     * leaving any other Markups in place.
     * 
     * This is useful when (for example) you've got a range of italic text,
     * and a word within it marked as <type>. You want to remove the italics,
     * but the marked up word will remain marked up.
     */
    private static Extract removeMarkup(final Extract original, final Markup format) {
        final ArrayList<Span> list;
        Node node;
        Span span;
        int i;

        list = new ArrayList<Span>();

        original.visit(new SpanVisitor() {
            public boolean visit(Span span) {
                final Span replacement;

                if (span.getMarkup() == format) {
                    replacement = span.copy(null);
                } else {
                    replacement = span;
                }

                list.add(replacement);
                return false;
            }
        });

        /*
         * TODO replace with an efficient list -> tree builder!
         */

        span = list.get(0);
        node = Node.createNode(span);

        for (i = 1; i < list.size(); i++) {
            span = list.get(i);
            node = node.append(span);
        }

        return node;
    }

    /**
     * Clear all format in the given range.
     */
    public FormatTextualChange(TextChain chain, int offset, Extract original) {
        super(chain, offset, original, clearMarkup(original));
        this.format = null;
    }

    private static Extract clearMarkup(Extract original) {
        final ArrayList<Span> list;
        Node node;
        Span span;
        int i;

        list = new ArrayList<Span>();

        original.visit(new SpanVisitor() {
            public boolean visit(Span span) {
                final Span replacement;
                replacement = span.copy(null);
                list.add(replacement);
                return false;
            }
        });

        /*
         * TODO replace with an efficient list -> tree builder!
         */

        span = list.get(0);
        node = Node.createNode(span);

        for (i = 1; i < list.size(); i++) {
            span = list.get(i);
            node = node.append(span);
        }

        return node;
    }

    /*
     * Doing clear() this way is cumbersome.
     */
    protected void apply() {
        chain.delete(offset, removed.getWidth());
        chain.insert(offset, added);
    }

    /*
     * What about having cleared? We should be restoring the range, maybe?
     * Actually, this is wrong; if we're undoing we have the Span[] and should
     * be able to just "replace" with it.
     */
    protected void undo() {
        chain.delete(offset, added.getWidth());
        chain.insert(offset, removed);
    }
}
