package Tracker.Infrastructure.Utils;

public class Result<T> {
    private final boolean success;
    private final T result;

    private Result(boolean success, T result) {
        this.success = success;
        this.result = result;
    }

    private Result(boolean success) {
        this.success = success;
        this.result = null;
    }

    public boolean success() {
        return this.success;
    }

    public T getOrThrow() throws FailureException {
        if (this.success) {
            return this.result;
        }
        throw new FailureException();
    }

    public static <T> Result<T> success(T result) {
        return new Result<T>(true, result);
    }

    public static <T> Result<T> fail() {
        return new Result<T>(false, null);
    }

}
