package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data;


public class TrailerData {

    public static final String RESULTS_NAME = "name";
    public static final String RESULTS_KEY = "key";

    String text;
    String url;

    public TrailerData(){}

    public TrailerData(String text, String url){
        this.text = text;
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }





}
