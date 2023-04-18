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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class TokenTreeNode implements TokenTree {

    private final TreeType treeType;
    private final List<TokenTree> children = new ArrayList<>();
    private TokenTree parent;
    private List<CapturedToken> tokenCache;

    TokenTreeNode(TreeType treeType, List<CapturedToken> tokens) {
        this.treeType = treeType;
        tokens.forEach(this::appendChild);
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
        tokenCache = null;
        if (parent != null) {
            parent.clearCaches();
        }
    }

    @Override
    public TreeType getType() {
        return treeType;
    }

    @Override
    public List<CapturedToken> getTokens() {
        List<CapturedToken> captures = tokenCache;
        if (captures == null) {
            captures = new ArrayList<>();
            for (TokenTree tree : children) {
                captures.addAll(tree.getTokens());
            }
            tokenCache = captures;
        }
        return Collections.unmodifiableList(captures);
    }

    @Override
    public List<TokenTree> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void appendChild(TokenTree tree) {
        children.add(tree);
        tree.setParent(this);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getType())
                .append(" (").append(getStartLine()).append(", ").append(getStartColumn())
                .append(") - (")
                .append(getEndLine()).append(", ").append(getEndColumn())
                .append(") {")
                .append('\n');
        for (TokenTree child : children) {
            result.append("    ").append(child.toString().replace("\n", "\n    ")).append('\n');
        }
        result.append('}');
        return result.toString();
    }

    @Override
    public int getStartPosition() {
        return children.isEmpty() ? 0 : children.get(0).getStartPosition();
    }

    @Override
    public int getStartLine() {
        return children.isEmpty() ? 0 : children.get(0).getStartLine();
    }

    @Override
    public int getStartColumn() {
        return children.isEmpty() ? 0 : children.get(0).getStartColumn();
    }

    @Override
    public int getEndLine() {
        return children.isEmpty() ? getStartLine() : getLastToken().getEndLine();
    }

    @Override
    public int getEndColumn() {
        return children.isEmpty() ? getStartColumn() : getLastToken().getEndColumn();
    }

    private CapturedToken getLastToken() {
        List<CapturedToken> capturedTokens = getTokens();
        return capturedTokens.get(capturedTokens.size() - 1);
    }
}
