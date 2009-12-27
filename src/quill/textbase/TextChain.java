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

import static quill.textbase.Extract.isWhitespace;

/**
 * A mutable buffer of unicode text which manages a binary tree of Spans in
 * order to maximize sharing of character array storage while giving us
 * efficient lookup by offset.
 * 
 * @author Andrew Cowie
 */
public class TextChain
{
    Node root;

    public TextChain() {
        root = null;
    }

    TextChain(final String str) {
        final Span span;

        span = Span.createSpan(str, null);
        root = Node.createNode(span);
    }

    TextChain(Span initial) {
        root = Node.createNode(initial);
    }

    /**
     * The length of this Text, in characters.
     */
    public int length() {
        if (root == null) {
            return 0;
        } else {
            return root.getWidth();
        }
    }

    /**
     * This is ineffecient! Use for debugging purposes only.
     */
    public String toString() {
        final StringBuilder str;

        if (root == null) {
            return "";
        }

        str = new StringBuilder();

        root.visit(new CharacterVisitor() {
            public boolean visit(int character, Markup markup) {
                str.appendCodePoint(character);
                return false;
            }
        });

        return str.toString();
    }

    /*
     * This is an inefficient implementation!
     */
    public void append(Span addition) {
        if (addition == null) {
            throw new IllegalArgumentException();
        }

        /*
         * Handle empty TextChain case
         */

        if (root == null) {
            root = Node.createNode(addition);
            return;
        }

        /*
         * Otherwise, we are appending. Hop to the end.
         */

        root = root.append(addition);
    }

    /*
     * TODO if we change to an EmptyNode singleton, then it should be returned
     * if empty.
     */
    Node getTree() {
        return root;
    }

    /**
     * Get the Span at a given offset, for testing purposes.
     */
    Span spanAt(int offset) {
        if (root == null) {
            if (offset == 0) {
                return null;
            } else {
                throw new IllegalStateException();
            }
        }
        return root.getSpanAt(offset);
    }

    /**
     * Insert the given Java String at the specified offset.
     */
    protected void insert(int offset, String what) {
        insert(offset, Span.createSpan(what, null));
    }

    /**
     * Insert the given tree of Spans at the specified offset.
     */
    protected void insert(int offset, Extract extract) {
        final Node tree;

        if (offset < 0) {
            throw new IllegalArgumentException();
        }

        tree = (Node) extract;

        /*
         * Create the insertion point
         */
        if (root == null) {
            root = tree;
        } else {
            root = root.insertTreeAt(offset, tree);
        }
    }

    /**
     * Splice a Chunk into the Text. The result of doing this is three Pieces;
     * a new Piece before and after, and a Piece wrapping the Chunk and linked
     * between them. This is the workhorse of this class.
     */
    void insert(int offset, Span addition) {
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }

        if (root == null) {
            if (offset != 0) {
                throw new IndexOutOfBoundsException();
            }
            root = Node.createNode(addition);
            return;
        }

        root = root.insertSpanAt(offset, addition);
    }

    /*
     * FUTURE replace with use of TextChain's getTree() instead?
     */
    public Node extractAll() {
        return root;
    }

    /**
     * Delete a width wide segment starting at offset. Because people have to
     * call extractRange() right before this in order to create a
     * DeleteChange, this will duplicate effort. So, TODO create one which
     * passes in a tree of the known bit to be removed.
     */
    protected void delete(final int offset, final int wide) {
        final Node preceeding, following;
        final int start, across;

        if (root == null) {
            throw new IllegalStateException("Can't delete when already emtpy");
        }
        if (wide == 0) {
            throw new IllegalArgumentException("Can't delete nothing");
        }

        /*
         * Handle the special case of deleting everything.
         */

        if ((offset == 0) && (wide == root.getWidth())) {
            root = null;
            return;
        }

        /*
         * Create subtrees for everything before and after the deletion range
         */

        preceeding = root.subset(0, offset);

        start = offset + wide;
        across = root.getWidth() - start;
        following = root.subset(start, across);

        /*
         * Now combine these subtrees to effect the deletion.
         */

        root = Node.createNode(preceeding, following);
    }

    /**
     * Add or remove a Markup format from a range of text.
     */
    protected void format(int offset, int wide, Markup format) {
        final Node preceeding, following;
        final int start, across;
        final Span span;
        AccumulatingCharacterVisitor tourist;

        /*
         * Create subtrees for everything before and after the changed range
         */

        preceeding = root.subset(0, offset);

        start = offset + wide;
        across = root.getWidth() - start;
        following = root.subset(start, across);

        /*
         * Accumulate the text in the given range, and then create a new Span
         * with the supplied new Markup.
         */

        tourist = new AccumulatingCharacterVisitor(wide, format);

        root.visit(tourist, offset, wide);

        span = tourist.toSpan();

        /*
         * Now combine these subtrees to effect the deletion.
         */

        root = Node.createNode(preceeding, span, following);
    }

    private static class AccumulatingCharacterVisitor implements CharacterVisitor
    {
        private final int[] characters;

        private final Markup markup;

        private int index;

        /**
         * Create an accumulator for a given number of characters, to
         * subsequently have the supplied format.
         */
        private AccumulatingCharacterVisitor(final int num, Markup format) {
            characters = new int[num];
            markup = format;
            index = 0;
        }

        public boolean visit(int character, Markup markup) {
            characters[index] = character;
            index++;
            return false;
        }

        /**
         * Convert the result of the accumulation into a single Span
         */
        /*
         * FIXME This is awful; we need a Span constructor that works in
         * character[]
         */
        private Span toSpan() {
            StringBuilder str;
            int i;

            str = new StringBuilder(characters.length);

            for (i = 0; i < characters.length; i++) {
                str.appendCodePoint(characters[i]);
            }

            return Span.createSpan(str.toString(), markup);
        }
    }

    public Markup getMarkupAt(int offset) {
        Span span;

        if (root == null) {
            return null;
        }

        span = root.getSpanAt(offset);

        if (span == null) {
            return null;
        } else {
            return span.getMarkup();
        }
    }

    /**
     * Gets the Spans that represent the characters and formatting width wide
     * from start. The result is returned wrapped in a read-only Extract
     * object.
     * 
     * <p>
     * If width is negative, start will be decremented by that amount and the
     * range will be
     * 
     * <pre>
     * extractRange(start-width, |width|)
     * </pre>
     * 
     * This accounts for the common but subtle bug that if you have selected
     * moving backwards, selectionBound will be at a point where the range
     * ends - and greater than insertBound.
     */
    /*
     * Having exposed this so that external APIs can get an Extract to pass
     * when constructing a DeleteChange, we probably end up duplicating a lot
     * of work when actually calling delete() after this here.
     */
    public Extract extractRange(int start, int wide) {
        if (wide < 0) {
            throw new IllegalArgumentException();
        }
        if (wide == 0) {
            return null;
        }

        return root.subset(start, wide);
    }

    /**
     * Generate an array of Extracts, one for each \n separated paragraph.
     */
    public Extract[] extractParagraphs() {
        final ArrayList<Integer> paragraphs;
        final Node[] nodes;
        int num, i, offset, wide;

        if (root == null) {
            return new Extract[] {};
        }

        /*
         * First work out how many lines are in this Text as it stands right
         * now. There is one paragraph if we're not empty.
         */

        paragraphs = new ArrayList<Integer>(8);
        paragraphs.add(0);

        root.visit(new CharacterVisitor() {
            private int offset = 0;

            public boolean visit(int character, Markup markup) {
                if (character == '\n') {
                    paragraphs.add(offset + 1);
                }
                offset++;
                return false;
            }
        });

        /*
         * Now form the array of Nodes which represent the ranges of each
         * paragraph. Note there is an assumption that there is not a newline
         * character at the end of the TextChain.
         */

        num = paragraphs.size();
        nodes = new Node[num];

        if (num == 1) {
            nodes[0] = root;
        } else {
            offset = 0;
            wide = paragraphs.get(1);

            for (i = 0; i < num - 1; i++) {
                offset = paragraphs.get(i);
                wide = paragraphs.get(i + 1) - 1 - paragraphs.get(i);

                nodes[i] = root.subset(offset, wide);
            }

            offset = 1 + offset + wide;
            wide = root.getWidth() - offset;
            nodes[i] = root.subset(offset, wide);
        }

        /*
         * Since Node is now Extract, we can just return our temporary array.
         */

        return nodes;
    }

    private Segment belongs;

    /**
     * Tell this TextChain what Segment it belongs to
     */
    void setEnclosingSegment(Segment segment) {
        this.belongs = segment;
    }

    /**
     * Get the Segment that this TextChain is backing
     */
    Segment getEnclosingSegment() {
        return belongs;
    }

    public int wordBoundaryBefore(final int offset) {
        if (root == null) {
            return 0;
        } else {
            return root.getWordBoundaryBefore(offset);
        }
    }

    /*
     * TODO this is still useful while refactoring, but we should change this
     * to a strong Visitor interface.
     */
    public int wordBoundaryAfter(final int offset) {
        if (root == null) {
            return 0;
        } else {
            return root.getWordBoundaryAfter(offset);
        }
    }

    /**
     * Given a cursor location in offset, work backwards to find a word
     * boundary, and then forwards to the next word boundary, and return the
     * word contained between those two points.
     */
    /*
     * After a huge amount of work, the current implementation in
     * EditorTextView doesn't call this, but needs the same boundary lookups,
     * and meanwhile this is heavily tested.
     */
    String getWordAt(final int offset) {
        final int begin, end;
        final StringBuilder str;
        final String result;

        if (offset == root.getWidth()) {
            return null;
        }

        str = new StringBuilder();

        /*
         * Seek backwards from the current offset to find a word boundary.
         */

        begin = root.getWordBoundaryBefore(offset);
        end = root.getWordBoundaryAfter(offset);

        /*
         * Iterate forward over the characters to get the word.
         */

        root.visit(new CharacterVisitor() {
            public boolean visit(int character, Markup markup) {
                str.appendCodePoint(character);
                return false;
            }
        }, begin, end - begin);

        /*
         * Pull out the word
         */

        result = str.toString();
        if (result.equals("")) {
            return null;
        } else {
            return result;
        }
    }

    /**
     * Iterate over a given range and build encountered characters into words.
     * 
     * @author Andrew Cowie
     */
    private static class WordBuildingCharacterVisitor implements CharacterVisitor
    {
        /*
         * The WordVisitor passed in to TextChain's visit() that will be
         * invoked here as words are accumulated.
         */
        private WordVisitor tourist;

        private StringBuilder str;

        private int begin;

        private int end;

        private boolean skip;

        /**
         * Marker to allow us to avoid calling on the last word if the tourist
         * returned true while visiting.
         */
        private boolean stop;

        /*
         * If a word has any range of non-spell checkable markup, then the
         * whole word is not to be checkable.
         */
        private static boolean skipSpellCheck(Markup markup) {
            if (markup == null) {
                return false; // normal
            }
            if (markup.isSpellCheckable()) {
                return false;
            } else {
                return true;
            }
        }

        /**
         * @param from
         *            Because we visit words over a range, we need to bump our
         *            starting offset by whatever the start of the range was
         *            given in the call to TextChain's visit().
         */
        private WordBuildingCharacterVisitor(final WordVisitor visitor, final int from) {
            tourist = visitor;
            str = new StringBuilder();
            begin = from;
            end = from;
            skip = false;
            stop = false;
        }

        public boolean visit(final int character, final Markup markup) {
            if (!skip) {
                if (skipSpellCheck(markup)) {
                    skip = true;
                }
            }

            if (!isWhitespace(character)) {
                str.appendCodePoint(character);
                end++;
                return false;
            }

            if (handleWord()) {
                stop = true;
                return true;
            }

            str.setLength(0);
            end++;
            begin = end;
            skip = false;

            return false;
        }

        /**
         * Action the accumulated word.
         */
        /*
         * Seperate code so that it can be invoked on the last word by the
         * calling visit() method (the CharacterVisitor doesn't know when it's
         * done, and so can't call this one last time).
         */
        private boolean handleWord() {
            final String word;

            if (stop) {
                return true;
            }

            word = str.toString();
            if (tourist.visit(word, skip, begin, end)) {
                stop = true;
                return true;
            }

            return false;
        }
    }

    /**
     * Visit through the given range and, as complete words are accumulated,
     * invoke tourist's visit() method. Used in spell checking!
     */
    public void visit(final WordVisitor tourist, final int begin, final int end) {
        final WordBuildingCharacterVisitor builder;

        if (root == null) {
            return;
        }

        builder = new WordBuildingCharacterVisitor(tourist, begin);

        root.visit(builder, begin, end - begin);
        builder.handleWord();
    }
}
