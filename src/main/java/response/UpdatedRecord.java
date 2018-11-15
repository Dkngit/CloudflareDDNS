package response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatedRecord {

    private Record result;
    private boolean success;
    private List<Error> errors;
    private Result_info result_info;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }


    public Result_info getResult_info() {
        return result_info;
    }

    public void setResult_info(Result_info result_info) {
        this.result_info = result_info;
    }

    public Record getResult() {
        return result;
    }

    public void setResult(Record result) {
        this.result = result;
    }
}
