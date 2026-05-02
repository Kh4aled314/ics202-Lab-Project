package kfupm.clinic.api;

/** Simple success/failure wrapper. */
public record Result<T>(boolean ok, String message, T data) {
    public static <T> Result<T> ok(T data, String message) {
        return new Result<>(true, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(false, message, null);
    }
}
