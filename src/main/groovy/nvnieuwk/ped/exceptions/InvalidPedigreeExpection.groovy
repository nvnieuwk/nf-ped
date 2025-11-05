package nvnieuwk.ped.exceptions

import groovy.transform.CompileStatic
import nextflow.exception.AbortOperationException

@CompileStatic
class InvalidPedigreeException extends AbortOperationException {

    InvalidPedigreeException(String message) {
        super(message)
    }
}