/*
 * ArticleDocument.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package docbook;

public class ArticleDocument extends Document
{
    public ArticleDocument() {
        super(new Article());
    }
}

/*
 * This could possibly be made public as it is indeed a peer of <component>,
 * although why you'd be packing a series of articles into a <book> I don't
 * know.
 */
class Article extends Component
{
    Article() {
        super("article");
    }
}
