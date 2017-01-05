package org.wildfly.extension.undertow;

import java.util.logging.Level;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ReloadRequiredWriteAttributeHandler;
import org.jboss.as.controller.registry.Resource;
import org.jboss.dmr.ModelNode;
import org.wildfly.extension.undertow.logging.UndertowLogger;

public class HttpListenerWorkerAttributeWriteHandler extends ReloadRequiredWriteAttributeHandler {

    public HttpListenerWorkerAttributeWriteHandler(AttributeDefinition... definitions) {
        super(definitions);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void finishModelStage(OperationContext context, ModelNode operation, String attributeName, ModelNode newValue,
            ModelNode oldValue, Resource model) throws OperationFailedException {
        super.finishModelStage(context, operation, attributeName, newValue, oldValue, model);
        context.addResponseWarning(Level.WARNING, UndertowLogger.ROOT_LOGGER.workerValueInHTTPListenerMustMatchRemoting(newValue.asString()));
    }
}
