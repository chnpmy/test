package algo;

import java.awt.image.BufferedImage;

/**
 * Created by chnpmy on 2014/5/26.
 */
public interface Energy {
    /**
     *
     * @param img
     * @return
     */
    public int[][] getEnergy(BufferedImage img);
}
