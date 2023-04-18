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
import software.amazon.smithy.model.loader.IdlTokenizer;
import software.amazon.smithy.model.loader.ModelSyntaxException;

/**
 * Provides a labeled tree of tokens returned from {@link IdlTokenizer}.
 *
 * <p>This abstraction is a kind of parse tree based on lexer tokens. Each consumed token is present in the tree,
 * and grouped together into nodes with labels defined by {@link TreeType}.
 */
public interface TokenTree {

    /**
     * Create a TokenTree from a {@link IdlTokenizer}.
     *
     * @param tokenizer Tokenizer to traverse.
     * @return Returns the created tree.
     */
    static TokenTree of(IdlTokenizer tokenizer) {
        return new TokenTreeBuilder(tokenizer).create();
    }

    /**
     * Create a tree from a single token.
     *
     * @param token Token to wrap into a tree.
     * @return Returns the created tree.
     */
    static TokenTree of(CapturedToken token) {
        return new TokenTreeLeaf(token);
    }

    /**
     * Create an empty tree of a specific {@code type}.
     *
     * @param type Tree type to create.
     * @return Returns the created tree.
     */
    static TokenTree of(TreeType type) {
        return of(type, Collections.emptyList());
    }

    /**
     * Create a tree of a specific {@code type} that contains a captured token.
     *
     * @param type  Tree type to create.
     * @param token Captured token to convert into a tree and append as a child.
     * @return Returns the created tree.
     */
    static TokenTree of(TreeType type, CapturedToken token) {
        return of(type, Collections.singletonList(token));
    }

    /**
     * Create a tree of a specific {@code type} that contains a list of captured tokens.
     *
     * @param type   Tree type to create.
     * @param tokens Tokens to convert into trees and append as a child.
     * @return Returns the created tree.
     */
    static TokenTree of(TreeType type, List<CapturedToken> tokens) {
        return new TokenTreeNode(type, tokens);
    }

    /**
     * Create a tree from a {@link ModelSyntaxException} and {@link IdlTokenizer}.
     *
     * @param e         Exception to get a message from.
     * @param tokenizer Tokenizer to get position and coordinates.
     * @return Returns the created tree.
     */
    static TokenTree of(ModelSyntaxException e, IdlTokenizer tokenizer) {
        return new TokenTreeLeaf(CapturedToken.error(tokenizer, e));
    }

    /**
     * Get the parent of the tree.
     *
     * @return Returns the parent, or null if the tree has no parent.
     */
    TokenTree getParent();

    /**
     * Get the parents of the current node, in order of direct parent, to grandparent, etc.
     *
     * @return Returns the parents of the tree.
     */
    default List<TokenTree> getParents() {
        List<TokenTree> parents = new ArrayList<>();
        TokenTree tree = this;
        while (tree.getParent() != null) {
            tree = tree.getParent();
            parents.add(tree);
        }
        return parents;
    }

    /**
     * Set the parent of the tree.
     *
     * @param parent Parent to set, or null to clear the parent.
     */
    void setParent(TokenTree parent);

    /**
     * Clear any caches of the tree.
     */
    void clearCaches();

    /**
     * Gets the token tree type.
     *
     * @return Returns the type.
     */
    TreeType getType();

    /**
     * Get direct children of the tree.
     *
     * @return Returns direct children.
     */
    List<TokenTree> getChildren();

    /**
     * Get the first immediate child contained in the tree of a specific type.
     *
     * @param type Type to return.
     * @return Returns the first found child of the given type, or null if not found.
     */
    default TokenTree getFirstChild(TreeType type) {
        for (TokenTree tree : getChildren()) {
            if (tree.getType() == type) {
                return tree;
            }
        }
        return null;
    }

    /**
     * Get a list of all immediate children contained in the tree of a specific type.
     *
     * @param type Type to return.
     * @return Returns the list of contained children, or an empty list if none are found.
     */
    default List<TokenTree> getChildren(TreeType type) {
        List<TokenTree> result = new ArrayList<>();
        for (TokenTree tree : getChildren()) {
            if (tree.getType() == type) {
                result.add(tree);
            }
        }
        return result;
    }

    /**
     * Recursively finds every node in the tree of the given {@code type}.
     *
     * @param type TokenTree type to find.
     * @return Returns the matching trees, or an empty list if none are found.
     */
    default List<TokenTree> findChildren(TreeType type) {
        List<TokenTree> result = new ArrayList<>();
        for (TokenTree tree : getChildren()) {
            if (tree.getType() == type) {
                result.add(tree);
            }
            result.addAll(tree.findChildren(type));
        }
        return result;
    }

    /**
     * Append a child to the tree.
     *
     * @param tree Tree to append.
     */
    void appendChild(TokenTree tree);

    /**
     * Append a child to the tree.
     *
     * @param token Token to turn into a tree to append.
     */
    default void appendChild(CapturedToken token) {
        appendChild(TokenTree.of(token));
    }

    /**
     * Gets a flat list of all captured tokens contained within the tree.
     *
     * @return Returns the contained tokens.
     */
    List<CapturedToken> getTokens();

    /**
     * Get the absolute start position, starting at 0.
     *
     * @return Returns the start position of this tree.
     */
    int getStartPosition();

    /**
     * Get the line the tree starts, starting at 1.
     *
     * @return Returns the start line.
     */
    int getStartLine();

    /**
     * Get the column the tree starts, starting at 1.
     *
     * @return Returns the start column.
     */
    int getStartColumn();

    /**
     * Get the line the tree ends, starting at 1.
     *
     * @return Returns the end line.
     */
    int getEndLine();

    /**
     * Get the column the tree end, starting at 1.
     *
     * @return Returns the end column.
     */
    int getEndColumn();

    /**
     * Find the innermost tree that contains the given coordinates.
     *
     * @param line   Line to find.
     * @param column Column to find.
     * @return Returns the innermost tree that contains the coordinates.
     */
    default TokenTree findAt(int line, int column) {
        TokenTree current = this;

        if (getChildren().isEmpty()) {
            return current;
        }

        outer: while (true) {
            for (TokenTree child : current.getChildren()) {
                int startLine = child.getStartLine();
                int endLine = child.getEndLine();
                int startColumn = child.getStartColumn();
                int endColumn = child.getEndColumn();
                boolean isMatch = false;
                if (line == startLine && line == endLine) {
                    // Column span checks are exclusive to not match the ends of tokens.
                    isMatch = column >= startColumn && column < endColumn;
                } else if (line == startLine && column >= startColumn) {
                    isMatch = true;
                } else if (line == endLine && column <= endColumn) {
                    isMatch = true;
                } else if (line > startLine && line < endLine) {
                    isMatch = true;
                }
                if (isMatch) {
                    current = child;
                    continue outer;
                }
            }
            return current;
        }
    }
}
