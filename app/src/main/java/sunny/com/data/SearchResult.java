package sunny.com.data;

import java.util.List;

/**
 * Created by Sunny on 2015/10/14.
 */
public class SearchResult {
    private List<Similar> similarpersonface;

    @Override
    public String toString() {
        return "SearchResult{" +
                "similarpersonface=" + similarpersonface +
                '}';
    }

    public List<Similar> getSimilarpersonface() {
        return similarpersonface;
    }

    public void setSimilarpersonface(List<Similar> similarpersonface) {
        this.similarpersonface = similarpersonface;
    }


}
