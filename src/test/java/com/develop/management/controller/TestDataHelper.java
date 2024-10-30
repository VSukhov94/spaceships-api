package com.develop.management.controller;

import lombok.Data;

@Data
public class TestDataHelper {

    public static final String VALID_CREDENTIALS_JSON = """
        {
            "email": "test@example.com",
            "password": "password123"
        }
        """;

    public static final String INVALID_CREDENTIALS_JSON = """
        {
            "email": "wrong@example.com",
            "password": "wrongpassword"
        }
        """;

    public static final String INVALID_REQUEST_BODY_JSON = """
        {
            "email": "invalidemail"
        }
        """;

    public static final String CREATION_NAME_REQUEST_BODY_JSON = """
        {
            "name": "Millennium Falcon"
        }
        """;

    public static final String UPDATE_NAME_REQUEST_BODY_JSON = """
        {
            "name": "Updated Name"
        }
        """;

    public static final String TOKEN_JSON_PATH = "$.token";

    public static final String MESSAGE_JSON_PATH = "$.message";

    public static final String ERRORS_JSON_PATH = "$.errors";

    public static final String UNAUTHORIZED_MESSAGE = "Unauthorized: ";

    public static final String INVALID_CREDENTIALS_ERROR = "Invalid credentials";

    public static final String REQUEST_BODY_INVALID_MESSAGE = "Request body is not valid";

    public static final String SPACESHIP_NAME = "$.name";

    public static final String SPACESHIPS_PATH = "$.content";

    public static final String SPACESHIPS_ARRAY_PATH = "$.spaceships";

    public static final String CONTENT_LIST_PATH = "$.content[0].name";

    public static final String LIST_FROM_SPACESHIP_NAME_PATH = "$.spaceships[0].name";

    public static final String PAGE_NUMBER_PATH = "$.pageNumber";

    public static final String PAGE_SIZE_PATH = "$.pageSize";

    public static final String TOTAL_ELEMENTS_PATH = "$.totalElements";

    public static final String TOTAL_PAGES_PATH = "$.totalPages";

}
