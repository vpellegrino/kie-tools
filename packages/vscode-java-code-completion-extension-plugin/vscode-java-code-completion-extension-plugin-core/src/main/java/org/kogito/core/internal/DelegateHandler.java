/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.kogito.core.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.kogito.core.internal.engine.ActivationChecker;
import org.kogito.core.internal.engine.JavaEngine;
import org.kogito.core.internal.handlers.AutocompleteHandler;
import org.kogito.core.internal.handlers.GetAccessorsHandler;
import org.kogito.core.internal.handlers.GetClassesHandler;
import org.kogito.core.internal.handlers.Handler;
import org.kogito.core.internal.handlers.HandlerConstants;
import org.kogito.core.internal.handlers.IsLanguageServerAvailableHandler;
import org.kogito.core.internal.util.WorkspaceUtil;

public class DelegateHandler implements IDelegateCommandHandler {

    private static final JavaEngine JAVA_ENGINE = new JavaEngine();
    private static final ActivationChecker ACTIVATION_CHECKER = new ActivationChecker(new WorkspaceUtil());
    private static final AutocompleteHandler AUTOCOMPLETE_HANDLER = new AutocompleteHandler(ACTIVATION_CHECKER);
    private final IsLanguageServerAvailableHandler isAvailableHandler;

    private static final List<Handler<?>> handlers = new ArrayList<>() {
        {
            add(new GetClassesHandler(HandlerConstants.GET_CLASSES, JAVA_ENGINE, AUTOCOMPLETE_HANDLER));
            add(new GetAccessorsHandler(HandlerConstants.GET_ACCESSORS, JAVA_ENGINE, AUTOCOMPLETE_HANDLER));
        }
    };

    public DelegateHandler() {
        ACTIVATION_CHECKER.check();
        this.isAvailableHandler = new IsLanguageServerAvailableHandler(HandlerConstants.IS_AVAILABLE,
                ACTIVATION_CHECKER);
    }

    public CompletableFuture<?> executeCommand(String commandId, List<Object> arguments, IProgressMonitor progress)
            throws Exception {
        JavaLanguageServerPlugin.logInfo(commandId);

        if (this.isAvailableHandler.canHandle(commandId)) {
            return this.isAvailableHandler.handle(arguments, progress);
        }

        if (ACTIVATION_CHECKER.existActivator()) {
            return handlers.stream().filter(handler -> handler.canHandle(commandId)).findFirst()
                    .map(handler -> handler.handle(arguments, progress))
                    .orElseThrow(() -> new UnsupportedOperationException(
                            String.format("Unsupported command '%s'!", commandId)));
        } else {
            ACTIVATION_CHECKER.check();
            return CompletableFuture.supplyAsync(() -> "");
        }
    }
}
