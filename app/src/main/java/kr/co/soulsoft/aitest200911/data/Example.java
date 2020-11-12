package kr.co.soulsoft.aitest200911.data;

public class Example {
    private String exampleNumber;
    private String exampleText;

    public Example(String exampleNumber, String exampleText) {
        this.exampleNumber = exampleNumber;
        this.exampleText = exampleText;
    }

    public String getExampleNumber() {
        return exampleNumber;
    }
    public String getExampleText() {
        return exampleText;
    }
    public void setExampleNumber(String exampleNumber) {
        this.exampleNumber = exampleNumber;
    }
    public void setExampleText(String exampleText) {
        this.exampleText = exampleText;
    }
}
