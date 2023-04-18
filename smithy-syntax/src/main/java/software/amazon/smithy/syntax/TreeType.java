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

public enum TreeType {
    IDL,

    KEY,
    VALUE,

    CONTROL_SECTION,
    CONTROL_STATEMENT,

    METADATA_SECTION,
    METADATA_STATEMENT,

    SHAPE_SECTION,
    SHAPE_SECTION_NAMESPACE,

    USE_SECTION,
    USE_STATEMENT,

    SHAPE_STATEMENTS,
    SHAPE_STATEMENT,
    SHAPE_DOCS,
    SHAPE_TRAITS,
    SHAPE_TYPE,
    SHAPE_NAME,
    SHAPE_BODY,
    SHAPE_MEMBER_NAME,
    SHAPE_MEMBER_TARGET,
    SHAPE_PROPERTY_NAME,
    SHAPE_PROPERTY_VALUE,

    // Used in shapes, members, and apply statements.
    APPLY_TRAIT,

    APPLY_STATEMENT,
    APPLY_STATEMENT_SINGULAR,
    APPLY_STATEMENT_BLOCK,
    APPLY_STATEMENT_TARGET,
    APPLY_STATEMENT_VALUE,

    NODE_VALUE,

    REFERENCE,
    ID_NAMESPACE,
    ID_NAME,
    ID_MEMBER,

    BR,
    KEYWORD,
    ERROR,
    TOKEN
}
