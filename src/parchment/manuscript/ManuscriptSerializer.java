/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010 Operational Dynamics Consulting, Pty Ltd
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
package parchment.manuscript;

/*
 * Forked from QuackSerializer.java
 */

import java.io.OutputStream;

import nu.xom.Serializer;

/**
 * Render a document's high level <code>&lt;manuscript&gt;</code> metadata as
 * valid XML. We'd expect this to get written to a <code>.parchment</code>
 * file.
 * 
 * <p>
 * There are no text nodes in the <code>&lt;manuscript&gt;</code> schema but
 * there are some whitespace conventions we'll observe to keep the files line
 * oriented and versionable.
 * 
 * @author Andrew Cowie
 */
class ManuscriptSerializer extends Serializer
{
    private final ManuscriptElement root;

    ManuscriptSerializer(OutputStream out, ManuscriptElement root) {
        super(out);
        super.setLineSeparator("\n");
        super.setIndent(2);
        super.setMaxLength(0);
        this.root = root;
    }
}
