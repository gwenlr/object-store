package com.triel.bo.service;

import java.util.UUID;

/**
 * Tiny helper to enhance usage of UUID in REST services
 */
public class UUIDHelper {

    public static UUID toUUID(String string ) throws InvalidUUIDException {
        try {
            return UUID.fromString(string);
        } catch( Throwable th) {
            throw new InvalidUUIDException(th);
        }
    }

}
