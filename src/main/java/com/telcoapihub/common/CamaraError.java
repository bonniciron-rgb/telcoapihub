package com.telcoapihub.common;

/**
 * Error body shaped to the CAMARA Commonalities specification.
 */
public record CamaraError(int status, String code, String message) {
}
