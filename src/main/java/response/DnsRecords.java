package response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DnsRecords {

    private List<Record> result;
    private boolean success;
    private List<Error> errors;
//    private String messages;
    private Result_info result_info;

    public List<Record> getResult() {
        return result;
    }

    public void setResult(List<Record> result) {
        this.result = result;
    }

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

//    public String getMessages() {
//        return messages;
//    }
//
//    public void setMessages(String messages) {
//        this.messages = messages;
//    }

    public Result_info getResult_info() {
        return result_info;
    }

    public void setResult_info(Result_info result_info) {
        this.result_info = result_info;
    }
}
