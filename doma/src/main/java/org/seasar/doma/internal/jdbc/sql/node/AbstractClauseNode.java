/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.jdbc.sql.node;

import static org.seasar.doma.internal.util.AssertionUtil.*;

import org.seasar.doma.jdbc.SqlNode;

/**
 * @author taedium
 * 
 */
public abstract class AbstractClauseNode extends AbstractSqlNode implements
        ClauseNode {

    protected final WordNode wordNode;

    protected AbstractClauseNode(String word) {
        this(new WordNode(word));
    }

    protected AbstractClauseNode(WordNode wordNode) {
        assertNotNull(wordNode);
        this.wordNode = wordNode;
    }

    @Override
    public WordNode getWordNode() {
        return wordNode;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(wordNode);
        for (SqlNode child : children) {
            buf.append(child);
        }
        return buf.toString();
    }
}
