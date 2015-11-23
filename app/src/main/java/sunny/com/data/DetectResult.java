package sunny.com.data;

import java.util.List;

/**
 * Created by Sunny on 2015/10/13.
 */
public class DetectResult {
    private List<FaceModel> facemodels;

    public List<FaceModel> getFacemodels() {
        return facemodels;
    }

    public void setFacemodels(List<FaceModel> facemodels) {
        this.facemodels = facemodels;
    }

    @Override
    public String toString() {
        return "FaceDetectResult [facemodels=" + facemodels + "]";
    }
}
