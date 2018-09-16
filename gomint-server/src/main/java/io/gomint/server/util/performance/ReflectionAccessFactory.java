/*
 * Copyright (c) 2018, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author geNAZt
 * @version 1.0
 */
public class ReflectionAccessFactory implements ConstructionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger( ConstructorAccessFactory.class );

    private Constructor constructor;

    public ReflectionAccessFactory( Class<?> clazz ) {
        try {
            this.constructor = clazz.getConstructor();
            this.constructor.setAccessible( true );
        } catch ( NoSuchMethodException e ) {
            LOGGER.error( "Can't construct access factory for {}", clazz.getName(), e );
        }
    }

    @Override
    public Object newInstance() {
        try {
            return this.constructor.newInstance( null );
        } catch ( InstantiationException | InvocationTargetException | IllegalAccessException e ) {
            LOGGER.error( "Can't construct new object", e );
        }

        return null;
    }

}
