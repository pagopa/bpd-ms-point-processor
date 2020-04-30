package it.gov.pagopa.bpd.point_processor.factory;

/**
 * interface to be used for inheritance for model factories from a DTO
 */

public interface ModelFactory<T, U> {

    U createModel(T dto);

}
