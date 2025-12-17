package org.tectuinno.compiler.assembler;

import java.util.Objects;

/**
 * Represents a syntactic or semantic issue detected during assembler analysis.
 */
public record AnalysisError(int line, int column, String message, Severity severity) {

    public enum Severity {
        ERROR, WARNING
    }

    public AnalysisError {
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(severity, "severity");
    }
}
