package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.service.UsersClient;
import guru.qa.rococo.service.impl.UsersDbClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class UsersClientExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(UsersClient.class)) {
                field.setAccessible(true);
                field.set(testInstance, new UsersDbClient());
            }
        }
    }
}
