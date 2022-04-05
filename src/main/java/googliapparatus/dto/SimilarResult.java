package googliapparatus.dto;

public class SimilarResult {
    private Integer count;

    private String title;

    public SimilarResult(Integer count, String title) {
        this.count = count;
        this.title = title;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
