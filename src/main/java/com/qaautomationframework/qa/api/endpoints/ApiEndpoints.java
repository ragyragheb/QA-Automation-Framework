package com.qaautomationframework.qa.api.endpoints;

public class ApiEndpoints {
 
    public static final String USERS_BASE = "/api/users";

    public static final String CREATE_USER = USERS_BASE;
    public static final String GET_USER = USERS_BASE + "/{id}";
    public static final String UPDATE_USER = USERS_BASE + "/{id}";
    public static final String DELETE_USER = USERS_BASE + "/{id}";
}
