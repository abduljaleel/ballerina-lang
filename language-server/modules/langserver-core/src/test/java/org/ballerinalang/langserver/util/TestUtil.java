/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.langserver.util;

import com.google.gson.Gson;
import org.ballerinalang.langserver.BallerinaLanguageServer;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ReferenceContext;
import org.eclipse.lsp4j.ReferenceParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.eclipse.lsp4j.jsonrpc.Endpoint;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseMessage;
import org.eclipse.lsp4j.jsonrpc.services.ServiceEndpoints;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Common utils that are reused within test suits.
 */
public class TestUtil {
    
    private static final String HOVER = "textDocument/hover";
    
    private static final String SIGNATURE_HELP = "textDocument/signatureHelp";
    
    private static final String DEFINITION = "textDocument/definition";
    
    private static final String REFERENCES = "textDocument/references";
    
    private static final String EXECUTE_COMMAND = "workspace/executeCommand";
    
    private static final String CODE_ACTION = "textDocument/codeAction";

    private static final Gson GSON = new Gson();

    private TestUtil() {
    }

    /**
     * Get the textDocument/hover response.
     *
     * @param filePath          Path of the Bal file
     * @param position          Cursor Position
     * @param serviceEndpoint   Service Endpoint to Language Server
     * @return {@link String}   Response as String
     */
    public static String getHoverResponse(String filePath, Position position, Endpoint serviceEndpoint) {
        CompletableFuture result = serviceEndpoint.request(HOVER, getTextDocumentPositionParams(filePath, position));
        return getResponseString(result);
    }

    /**
     * Get the textDocument/signatureHelp response.
     *
     * @param filePath          Path of the Bal file
     * @param position          Cursor Position
     * @param serviceEndpoint   Service Endpoint to Language Server
     * @return {@link String}   Response as String
     */
    public static String getSignatureHelpResponse(String filePath, Position position, Endpoint serviceEndpoint) {
        CompletableFuture result = serviceEndpoint.request(SIGNATURE_HELP,
                getTextDocumentPositionParams(filePath, position));
        return getResponseString(result);
    }

    /**
     * Get the textDocument/definition response.
     *
     * @param filePath          Path of the Bal file
     * @param position          Cursor Position
     * @param serviceEndpoint   Service Endpoint to Language Server
     * @return {@link String}   Response as String
     */
    public static String getDefinitionResponse(String filePath, Position position, Endpoint serviceEndpoint) {
        CompletableFuture result = serviceEndpoint.request(DEFINITION,
                getTextDocumentPositionParams(filePath, position));
        return getResponseString(result);
    }

    /**
     * Get the textDocument/reference response.
     *
     * @param filePath          Path of the Bal file
     * @param position          Cursor Position
     * @param serviceEndpoint   Service Endpoint to Language Server
     * @return {@link String}   Response as String
     */
    public static String getReferencesResponse(String filePath, Position position, Endpoint serviceEndpoint) {
        ReferenceParams referenceParams = new ReferenceParams();

        ReferenceContext referenceContext = new ReferenceContext();
        referenceContext.setIncludeDeclaration(true);

        referenceParams.setPosition(new Position(position.getLine(), position.getCharacter()));
        referenceParams.setTextDocument(getTextDocumentIdentifier(filePath));
        referenceParams.setContext(referenceContext);

        CompletableFuture result = serviceEndpoint.request(REFERENCES, referenceParams);
        return getResponseString(result);
    }

    /**
     * Get Code Action Response as String.
     *
     * @param serviceEndpoint       Language Server Service endpoint
     * @param filePath              File path for the current file
     * @param range                 Cursor range
     * @param context               Code Action Context
     * @return {@link String}       code action response as a string
     */
    public static String getCodeActionResponse(Endpoint serviceEndpoint, String filePath, Range range,
                                               CodeActionContext context) {
        TextDocumentIdentifier identifier = getTextDocumentIdentifier(filePath);
        CodeActionParams codeActionParams = new CodeActionParams(identifier, range, context);
        CompletableFuture result = serviceEndpoint.request(CODE_ACTION, codeActionParams);
        return getResponseString(result);
    }

    /**
     * Get the workspace/executeCommand response.
     *
     * @param params            Execute command parameters
     * @param serviceEndpoint   Service endpoint to language server
     * @return {@link String}   Lang server Response as String
     */
    public static String getExecuteCommandResponse(ExecuteCommandParams params, Endpoint serviceEndpoint) {
        CompletableFuture result = serviceEndpoint.request(EXECUTE_COMMAND, params);
        return getResponseString(result);
    }

    /**
     * Open a document.
     * 
     * @param serviceEndpoint   Language Server Service Endpoint
     * @param filePath          Path of the document to open
     * @throws IOException      Exception while reading the file content
     */
    public static void openDocument(Endpoint serviceEndpoint, Path filePath) throws IOException {
        DidOpenTextDocumentParams documentParams = new DidOpenTextDocumentParams();
        TextDocumentItem textDocumentItem = new TextDocumentItem();
        TextDocumentIdentifier identifier = new TextDocumentIdentifier();

        byte[] encodedContent = Files.readAllBytes(filePath);
        identifier.setUri(filePath.toUri().toString());
        textDocumentItem.setUri(identifier.getUri());
        textDocumentItem.setText(new String(encodedContent));
        documentParams.setTextDocument(textDocumentItem);
        
        serviceEndpoint.notify("textDocument/didOpen", documentParams);
    }

    /**
     * Close an already opened document.
     *
     * @param serviceEndpoint   Service Endpoint to Language Server
     * @param filePath          File path of the file to be closed
     */
    public static void closeDocument(Endpoint serviceEndpoint, Path filePath) {
        TextDocumentIdentifier documentIdentifier = new TextDocumentIdentifier();
        documentIdentifier.setUri(filePath.toUri().toString());
        serviceEndpoint.notify("textDocument/didClose", new DidCloseTextDocumentParams(documentIdentifier));
    }

    /**
     * Initialize the language server instance to use.
     * 
     * @return {@link Endpoint}     Service Endpoint
     */
    public static Endpoint initializeLanguageSever() {
        return ServiceEndpoints.toEndpoint(new BallerinaLanguageServer());
    }

    /**
     * Shutdown an already running language server.
     *
     * @param serviceEndpoint   Language server Service Endpoint
     */
    public static void shutdownLanguageServer(Endpoint serviceEndpoint) {
        serviceEndpoint.notify("shutdown", null);
    }

    private static TextDocumentIdentifier getTextDocumentIdentifier(String filePath) {
        TextDocumentIdentifier identifier = new TextDocumentIdentifier();
        identifier.setUri(Paths.get(filePath).toUri().toString());

        return identifier;
    }
    
    private static TextDocumentPositionParams getTextDocumentPositionParams(String filePath, Position position) {
        TextDocumentPositionParams positionParams = new TextDocumentPositionParams();
        positionParams.setTextDocument(getTextDocumentIdentifier(filePath));
        positionParams.setPosition(new Position(position.getLine(), position.getCharacter()));
        
        return positionParams;
    }
    
    private static String getResponseString(CompletableFuture completableFuture) {
        ResponseMessage jsonrpcResponse = new ResponseMessage();
        try {
            jsonrpcResponse.setId("324");
            jsonrpcResponse.setResult(completableFuture.get());
        } catch (InterruptedException e) {
            ResponseError responseError = new ResponseError();
            responseError.setCode(-32002);
            responseError.setMessage("Attempted to retrieve the result of a task/s" +
                    "that was aborted by throwing an exception");
            jsonrpcResponse.setError(responseError);
        } catch (ExecutionException e) {
            ResponseError responseError = new ResponseError();
            responseError.setCode(-32001);
            responseError.setMessage("Current thread was interrupted");
            jsonrpcResponse.setError(responseError);
        }

        return GSON.toJson(jsonrpcResponse);
    }
}
