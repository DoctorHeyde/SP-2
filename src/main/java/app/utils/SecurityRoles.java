package app.utils;

import io.javalin.security.RouteRole;

public enum SecurityRoles implements RouteRole {
    ADMIN, USER, ANYONE
}
