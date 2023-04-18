/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.syntax;

import java.util.Collections;
import java.util.List;

final class TokenTreeLeaf implements TokenTree {

    private final CapturedToken token;
    private TokenTree parent;

    TokenTreeLeaf(CapturedToken token) {
        this.token = token;
    }

    @Override
    public TokenTree getParent() {
        return parent;
    }

    @Override
    public void setParent(TokenTree parent) {
        this.parent = parent;
        clearCaches();
    }

    @Override
    public void clearCaches() {
        if (parent != null) {
            parent.clearCaches();
        }
    }

    @Override
    public List<CapturedToken> getTokens() {
        return Collections.singletonList(token);
    }

    @Override
    public TreeType getType() {
        return TreeType.TOKEN;
    }

    @Override
    public List<TokenTree> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void appendChild(TokenTree tree) {
        throw new UnsupportedOperationException("Cannot append a child to a leaf node");
    }

    @Override
    public String toString() {
        if (token.getErrorMessage() != null) {
            return token.getToken() + "('" + token.getLexeme() + "'; Error: " + token.getErrorMessage() + ')';
        } else {
            return token.getToken().getDebug(token.getLexeme());
        }
    }

    @Override
    public int getStartPosition() {
        return token.getPosition();
    }

    @Override
    public int getStartLine() {
        return token.getStartLine();
    }

    @Override
    public int getStartColumn() {
        return token.getStartColumn();
    }

    @Override
    public int getEndLine() {
        return token.getEndLine();
    }

    @Override
    public int getEndColumn() {
        return token.getEndColumn();
    }
}
