package io.github.taciosd.rmi.promise.demo.common.service;

/**
 * Businness class example to show how "progress state" can be used.
 *
 * Created by taciosd on 12/20/19.
 */
public enum ExecutionFlow {
    READING_DATA,
    PARSING_TOKENS,
    DOING_THE_MATH,
    COMPUTING_THE_RESULT
}
