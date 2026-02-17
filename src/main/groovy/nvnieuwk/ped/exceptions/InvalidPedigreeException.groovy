package nvnieuwk.ped.exceptions

import groovy.transform.CompileStatic
import nextflow.exception.AbortOperationException

/**
 * Implements a custom exception for invalid PED file contents.
 */
@CompileStatic
class InvalidPedigreeException extends AbortOperationException {

    InvalidPedigreeException(String message) {
        super(message)
    }

}
