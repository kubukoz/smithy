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

import software.amazon.smithy.model.loader.IdlToken;
import software.amazon.smithy.model.loader.IdlTokenizer;
import software.amazon.smithy.model.loader.ModelSyntaxException;

/**
 * A persisted token captured from a {@link IdlTokenizer}.
 */
public final class CapturedToken {

    private final IdlToken token;
    private final int position;
    private final int startLine;
    private final int startColumn;
    private final int endLine;
    private final int endColumn;
    private final CharSequence lexeme;
    private final String stringContents;
    private final String errorMessage;

    private CapturedToken(
            IdlToken token,
            int position,
            int startLine,
            int startColumn,
            int endLine,
            int endColumn,
            CharSequence lexeme,
            String stringContents,
            String errorMessage
    ) {
        this.token = token;
        this.position = position;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.lexeme = lexeme;
        this.stringContents = stringContents;
        this.errorMessage = errorMessage;
    }

    /**
     * Persist the current token of a {@link IdlTokenizer}.
     *
     * @param tokenizer Tokenizer to capture.
     * @return Returns the persisted token.
     */
    public static CapturedToken from(IdlTokenizer tokenizer) {
        IdlToken token = tokenizer.getCurrentToken();
        String stringContents = token == IdlToken.STRING || token == IdlToken.TEXT_BLOCK
                                ? tokenizer.getCurrentTokenStringSlice().toString()
                                : null;
        String errorMessage = token == IdlToken.ERROR ? tokenizer.getCurrentTokenError() : null;
        return new CapturedToken(token,
                                 tokenizer.getCurrentTokenStart(),
                                 tokenizer.getCurrentTokenLine(),
                                 tokenizer.getCurrentTokenColumn(),
                                 tokenizer.getLine(),
                                 tokenizer.getColumn(),
                                 tokenizer.getCurrentTokenLexeme(),
                                 stringContents,
                                 errorMessage);
    }

    static CapturedToken error(IdlTokenizer tokenizer, String errorMessage) {
        return new CapturedToken(IdlToken.ERROR,
                                 tokenizer.getCurrentTokenStart(),
                                 tokenizer.getCurrentTokenLine(),
                                 tokenizer.getCurrentTokenColumn(),
                                 tokenizer.getLine(),
                                 tokenizer.getColumn(),
                                 tokenizer.getCurrentTokenLexeme(),
                                 null,
                                 errorMessage);
    }

    static CapturedToken error(IdlTokenizer tokenizer, ModelSyntaxException e) {
        return error(tokenizer, e.getMessageWithoutLocation());
    }

    public IdlToken getToken() {
        return token;
    }

    public int getPosition() {
        return position;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public int getSpan() {
        return lexeme.length();
    }

    public CharSequence getLexeme() {
        return lexeme;
    }

    public String getStringContents() {
        return stringContents;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
