package com.epam.training.ticketservice.core.result;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Result<OkT> {
    record Ok<OkT>(OkT value) implements Result<OkT> {
        @Override
        public <OtherOkT> Result<OtherOkT> map(Function<OkT, OtherOkT> fn, Function<Error, Error> fnErr) {
            return ok(fn.apply(value));
        }

        @Override
        public <OtherOkT> Result<OtherOkT> flatMap(Function<OkT, Result<OtherOkT>> fn) {
            return fn.apply(value);
        }

        @Override
        public Optional<Error> toOptional() {
            return Optional.empty();
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public OkT unwrap() {
            return value;
        }

        @Override
        public Error unwrapErr() {
            throw new RuntimeException("UnwrapErr on an ok value: " + value);
        }
    }

    record Err<OkT>(Error error) implements Result<OkT> {

        @Override
        public <OtherOkT> Result<OtherOkT> map(Function<OkT, OtherOkT> fn, Function<Error, Error> fnErr) {
            return err(fnErr.apply(error));
        }

        @Override
        public <OtherOkT> Result<OtherOkT> flatMap(Function<OkT, Result<OtherOkT>> fn) {
            //noinspection unchecked
            return (Result<OtherOkT>) this;
        }

        @Override
        public Optional<Error> toOptional() {
            return Optional.of(error);
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public OkT unwrap() {
            throw new RuntimeException("Unwrap on an error: " + error.getMessage());
        }

        @Override
        public Error unwrapErr() {
            return error;
        }
    }

    static <OkT> Ok<OkT> ok(OkT value) {
        return new Ok<>(value);
    }

    static <OkT> Err<OkT> err(Error error) {
        return new Err<>(error);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <OkT> Result<OkT> fromOptional(Optional<OkT> optional, Error orError) {
        return optional.<Result<OkT>>map(Result::ok).orElse(err(orError));
    }

    <OtherOkT> Result<OtherOkT> map(Function<OkT, OtherOkT> fn, Function<Error, Error> fnErr);

    default <OtherOkT> Result<OtherOkT> map(Function<OkT, OtherOkT> fn) {
        return map(fn, error -> error);
    }

    <OtherOkT> Result<OtherOkT> flatMap(Function<OkT, Result<OtherOkT>> fn);

    Optional<Error> toOptional();

    boolean isOk();

    OkT unwrap();

    Error unwrapErr();

    default Result<OkT> use(Consumer<OkT> fn) {
        return map(t -> {
            fn.accept(t);
            return t;
        });
    }

}
