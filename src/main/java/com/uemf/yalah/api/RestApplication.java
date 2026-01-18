package com.uemf.yalah.api;

import com.uemf.yalah.api.AuthResource;
import com.uemf.yalah.api.BookingResource;
import com.uemf.yalah.api.RideResource;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration JAX-RS pour l'API REST.
 * Configuré via web.xml (servlet mapping /api/v1/*)
 */
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(AuthResource.class);
        classes.add(BookingResource.class);
        classes.add(RideResource.class);
        classes.add(PingResource.class);
        return classes;
    }
}
