package algo;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by chnpmy on 2014/5/26.
 */
public class MyEnergy implements Energy{

    class RGBPoint{
        public int[] color= new int[3];
    }
    @Override
    public int[][] getEnergy(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        RGBPoint[][] points = assignColor(img);
        int[][] energy = new int[width][height];
        for (int x = 0; x < width - 1; x++)
            for (int y = 0; y < height - 1; y++){
                int dr = Math.abs(points[x][y].color[0] - points[x + 1][y].color[0])
                        + Math.abs(points[x][y].color[0] - points[x][y + 1].color[0]);
                int dg = Math.abs(points[x][y].color[1] - points[x + 1][y].color[1])
                        + Math.abs(points[x][y].color[1] - points[x][y + 1].color[1]);
                int db = Math.abs(points[x][y].color[2] - points[x + 1][y].color[2])
                        + Math.abs(points[x][y].color[2] - points[x][y + 1].color[2]);
                energy[x][y] =dr + dg + db;
            }
        for (int x = 0; x < width; x++)
            energy[x][height - 1] = energy[x][height - 2];
        for (int y = 0; y < height; y++)
            energy[width - 1][y] = energy[width - 2][y];
        return energy;
    }

    protected RGBPoint[][] assignColor(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();
        RGBPoint[][] points = new RGBPoint[width][height];
        for (int i = 0; i < width; i++) {
            points[i] = new RGBPoint[height];
            for (int j = 0; j < height; j++){
                points[i][j] = new RGBPoint();
            }
        }
        int type= img.getType();
        if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB ) {
            for (int x = 0; x <width; x++)
                for (int y = 0; y < height; y++) {
                    int[] col = (int[])img.getRaster().getDataElements(x, y, null);
                    points[x][y].color[0] = (col[0] >> 16) & 0xff;
                    points[x][y].color[1] = (col[0] >> 8) & 0xff;
                    points[x][y].color[2] = col[0] & 0xff;
                }
        }
        else {
            for (int x = 0; x <width; x++)
                for (int y = 0; y <height; y++) {
                    int col = img.getRGB(x, y);
                    points[x][y].color[0] = (col >> 16) & 0xff;
                    points[x][y].color[1] = (col >> 8) & 0xff;
                    points[x][y].color[2] = col & 0xff;
                }
        }
        return points;
    }

    /**
     *
     * @param energy
     * @param dim 当dim为'w'时，表示我想在宽度方向上扩展，此时应该
     *            计算竖直的seam；当dim为'w'时，表示我想在高度方向上扩展，此
     *            时应该计算水平的seam
     * @return 返回的seams数组与energy数组大小相同，seams[i][j] = n表示
     * 共有n条不同的seam经过该点
     */
    protected int[][] getSeams(int[][] energy, char dim, int len){
        int width = energy.length;
        int height = energy[0].length;
        int[][] sumOfEnergy = new int[width][];
        int[][] flagOfEnergy = new int[width][];
        int [][] seams = new int[width][];
        for (int i = 0; i < width; i++) {
            sumOfEnergy[i] = new int[height];
            flagOfEnergy[i] = new int[height];
            seams[i] = new int[energy[0].length];
            for (int j = 0; j < height; j++)
                seams[i][j] = 0;
        }
        switch (dim){
            case 'w': {
                for (int x = 0; x < width; x++)
                    sumOfEnergy[x][0] = energy[x][0];
                for (int y = 1; y < height; y++){
                    if (sumOfEnergy[0][y - 1] <= sumOfEnergy[1][y - 1]){
                        sumOfEnergy[0][y] = sumOfEnergy[0][y - 1] + energy[0][y];
                        flagOfEnergy[0][y] = 0;
                    }
                    else{
                        sumOfEnergy[0][y] = sumOfEnergy[1][y - 1] + energy[0][y];
                        flagOfEnergy[0][y] = 1;
                    }

                    if (sumOfEnergy[width - 1][y - 1] <= sumOfEnergy[width - 2][y - 1]){
                        sumOfEnergy[width - 1][y] = sumOfEnergy[width - 1][y - 1] + energy[width - 1][y];
                        flagOfEnergy[width - 1][y] = 0;
                    }
                    else{
                        sumOfEnergy[width - 1][y] = sumOfEnergy[width - 2][y - 1] + energy[width - 1][y];
                        flagOfEnergy[width - 1][y] = -1;
                    }
                    for (int x = 1; x < width - 1; x++){
                        if (sumOfEnergy[x][y - 1] <= sumOfEnergy[x - 1][y - 1] && sumOfEnergy[x][y - 1] <= sumOfEnergy[x + 1][y - 1]){
                            sumOfEnergy[x][y] = sumOfEnergy[x][y - 1] + energy[x][y];
                            flagOfEnergy[x][y] = 0;
                        }
                        else if (sumOfEnergy[x - 1][y - 1] <= sumOfEnergy[x + 1][y - 1]){
                            sumOfEnergy[x][y] = sumOfEnergy[x - 1][y - 1] + energy[x][y];
                            flagOfEnergy[x][y] = -1;
                        }
                        else {
                            sumOfEnergy[x][y] = sumOfEnergy[x + 1][y - 1] + energy[x][y];
                            flagOfEnergy[x][y] = 1;
                        }
                    }
                }
                int[] lastEnergy = new int[width];
                for (int i = 0; i < width; i++)
                    lastEnergy[i] = sumOfEnergy[i][height - 1];
                int maxSeamEnergy = findKmin(lastEnergy, len);
                int seamCount = 0;
                for (int i = 0; i < width; i++){
                    if (sumOfEnergy[i][height - 1] <= maxSeamEnergy && seamCount < len){
                        int pos = i;
                        seams[pos][height - 1]++;
                        for (int y = height - 1; y > 0; y--){
                            if (flagOfEnergy[pos][y] == 0)
                                seams[pos][y - 1]++;
                            else if (flagOfEnergy[pos][y] == 1){
                                seams[++pos][y - 1]++;
                            }
                            else{
                                seams[--pos][y - 1]++;
                            }
                        }
                    }
                    seamCount++;
                }
               break;
            }
            case 'h': {
                for (int y = 0; y < height; y++)
                    sumOfEnergy[0][y] = energy[0][y];
                for (int x = 1; x < width; x++){
                    if (sumOfEnergy[x - 1][0] <= sumOfEnergy[x - 1][1]){
                        sumOfEnergy[x][0] = sumOfEnergy[x - 1][0] + energy[x][0];
                        flagOfEnergy[x][0] = 0;
                    }
                    else{
                        sumOfEnergy[x][0] = sumOfEnergy[x - 1][1] + energy[x][0];
                        flagOfEnergy[x][0] = 1;
                    }

                    if (sumOfEnergy[x - 1][height - 1] <= sumOfEnergy[x -1][height - 2]){
                        sumOfEnergy[x][height - 1] = sumOfEnergy[x - 1][height - 1] + energy[x][height - 1];
                        flagOfEnergy[x][height - 1] = 0;
                    }
                    else{
                        sumOfEnergy[x][height - 1] = sumOfEnergy[x - 1][height - 2] + energy[x][height - 1];
                        flagOfEnergy[x][height - 1] = -1;
                    }
                    for (int y = 1; y < height - 1; y++){
                        if (sumOfEnergy[x - 1][y] <= sumOfEnergy[x - 1][y - 1] && sumOfEnergy[x - 1][y] <= sumOfEnergy[x - 1][y + 1]){
                            sumOfEnergy[x][y] = sumOfEnergy[x - 1][y] + energy[x][y];
                            flagOfEnergy[x][y] = 0;
                        }
                        else if (sumOfEnergy[x - 1][y - 1] <= sumOfEnergy[x - 1][y + 1]){
                            sumOfEnergy[x][y] = sumOfEnergy[x - 1][y - 1] + energy[x][y];
                            flagOfEnergy[x][y] = -1;
                        }
                        else {
                            sumOfEnergy[x][y] = sumOfEnergy[x - 1][y + 1] + energy[x][y];
                            flagOfEnergy[x][y] = 1;
                        }
                    }
                }
                int[] lastEnergy = new int[height];
                for (int i = 0; i < height; i++)
                    lastEnergy[i] = sumOfEnergy[width - 1][i];
                int maxSeamEnergy = findKmin(lastEnergy, len);
                int seamCount = 0;
                for (int i = 0; i < height; i++){
                    if (sumOfEnergy[width - 1][i] <= maxSeamEnergy && seamCount < len){
                        int pos = i;
                        seams[width - 1][pos]++;
                        for (int x = width - 1; x > 0; x--){
                            if (flagOfEnergy[x][pos] == 0)
                                seams[x - 1][pos]++;
                            else if (flagOfEnergy[x][pos] == 1){
                                seams[x - 1][++pos]++;
                            }
                            else{
                                seams[x - 1][--pos]++;
                            }
                        }
                    }
                    seamCount++;
                }
                break;
            }

        }
        return seams;
    }

    protected int[][] getSeams2(int[][] energy, char dim, int len){
        int width = energy.length;
        int height = energy[0].length;
        int oldWidth = width;
        int oldHeight = height;
        int[][] sumOfEnergy = new int[width][];
        int [][] seams = new int[width][];
        for (int i = 0; i < width; i++) {
            sumOfEnergy[i] = new int[height];
            seams[i] = new int[energy[0].length];
            for (int j = 0; j < height; j++)
                seams[i][j] = 0;
        }
        int[][] tempEnergy = new int[width][];
        for (int i = 0; i < width; i++){
            tempEnergy[i] = new int[height];
            for (int j = 0; j < height; j++)
                tempEnergy[i][j] = energy[i][j];
        }
        for (int i = 0; i < len; i++){
            int[] seam = getSingleSeam(tempEnergy, dim);
            switch (dim){
                case 'w':{
                    for (int j = 0; j < oldHeight; j++){
                        int count = seam[j] + 1;
                        int k = 0;
                        while (count != 0){
                            if (seams[k][j] == 0){
                                count--;
                            }
                            k++;
                        }
                        seams[k - 1][j] = 1;
                    }
                    width--;
                    int[][] newTempEnergy = new int[width][];
                    for (int j = 0; j < width; j++){
                        newTempEnergy[j] = new int[height];
                        for (int k = 0; k < height; k++){
                            if (j >= seam[k]){
                                newTempEnergy[j][k] = tempEnergy[j + 1][k];
                            }
                            else{
                                newTempEnergy[j][k] = tempEnergy[j][k];
                            }
                        }
                    }
                    tempEnergy = newTempEnergy;
                    break;
                }
                case 'h':{
                    for (int j = 0; j < oldWidth; j++){
                        int count = seam[j] + 1;
                        int k = 0;
                        while (count != 0){
                            if (seams[j][k] == 0){
                                count--;
                            }
                            k++;
                        }
                        seams[j][k - 1] = 1;
                    }
                    height--;
                    int[][] newTempEnergy = new int[width][];
                    for (int j = 0; j < width; j++){
                        newTempEnergy[j] = new int[height];
                        for (int k = 0; k < height; k++){
                            if (k >= seam[j]){
                                newTempEnergy[j][k] = tempEnergy[j][k + 1];
                            }
                            else{
                                newTempEnergy[j][k] = tempEnergy[j][k];
                            }
                        }
                    }
                    tempEnergy = newTempEnergy;
                    break;
                }
            }
        }
        return seams;
    }

    /**
     * 最多扩展2倍
     * @param img
     * @param width
     * @param height
     * @return
     */
    public BufferedImage convert(BufferedImage img, int width, int height){
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        int oriWidth = img.getWidth();
        int oriHeight = img.getHeight();
        for (int x = 0; x < oriWidth; x++)
            for (int y = 0; y < oriHeight; y++)
                newImg.setRGB(x, y, img.getRGB(x, y));

        if (width > oriWidth){
            newImg = addSeams(newImg, width, newImg.getHeight(), 'w', getSeams2(getEnergy(newImg), 'w', width - oriWidth));
        }
        else{
            newImg = removeSeams(newImg, width, height, 'w');
        }

        if (height > oriHeight){
            newImg = addSeams(newImg, newImg.getWidth(), height, 'h', getSeams2(getEnergy(newImg), 'h', height - oriHeight));
        }
        else{
            newImg = removeSeams(newImg, width, height, 'h');
        }
        return newImg;
    }

    protected BufferedImage addSeams(BufferedImage oriImg, int newWidth, int newHeight, char dim, int[][]seams){
        int offset = 0;
        int oldWidth = oriImg.getWidth();
        int oldHeight = oriImg.getHeight();
        switch (dim){
            case 'w': {
                BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < oldHeight; y++){
                    offset = 0;
                    for (int x = 0; x < oldWidth; x++){
                        if (seams[x][y] == 0)
                            newImg.setRGB(x + offset, y, oriImg.getRGB(x, y));
                        else{
                            for (int i = 0; i <= seams[x][y]; i++){
                                newImg.setRGB(x + i + offset, y, oriImg.getRGB(x, y));
                            }
                            offset += seams[x][y];
                        }
                    }
                }
                return newImg;
            }
            case 'h': {
                BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < oldWidth; x++){
                    offset = 0;
                    for (int y = 0; y < oldHeight; y++){
                        if (seams[x][y] == 0)
                            newImg.setRGB(x, y + offset, oriImg.getRGB(x, y));
                        else{
                            for (int i = 0; i <= seams[x][y]; i++){
                                newImg.setRGB(x, y + offset + i, oriImg.getRGB(x, y));
                            }
                            offset += seams[x][y];
                        }
                    }
                }
                return newImg;
            }
        }
        return null;
    }

    private int findKmin(int[] arr, int k){
        Arrays.sort(arr);
        return arr[k - 1];
    }

    protected int[] getSingleSeam(int[][] energy, char dim){
        int width = energy.length;
        int height = energy[0].length;
        int[][] sumOfEnergy = new int[width][];
        int[][] flagOfEnergy = new int[width][];
        for (int i = 0; i < width; i++) {
            sumOfEnergy[i] = new int[height];
            flagOfEnergy[i] = new int[height];
        }
        switch (dim){
            case 'w':{
                for (int x = 0; x < width; x++)
                    sumOfEnergy[x][0] = energy[x][0];
                for (int y = 1; y < height; y++){
                    if (sumOfEnergy[0][y - 1] <= sumOfEnergy[1][y - 1]){
                        sumOfEnergy[0][y] = sumOfEnergy[0][y - 1] + energy[0][y];
                        flagOfEnergy[0][y] = 0;
                    }
                    else{
                        sumOfEnergy[0][y] = sumOfEnergy[1][y - 1] + energy[0][y];
                        flagOfEnergy[0][y] = 1;
                    }

                    if (sumOfEnergy[width - 1][y - 1] <= sumOfEnergy[width - 2][y - 1]){
                        sumOfEnergy[width - 1][y] = sumOfEnergy[width - 1][y - 1] + energy[width - 1][y];
                        flagOfEnergy[width - 1][y] = 0;
                    }
                    else{
                        sumOfEnergy[width - 1][y] = sumOfEnergy[width - 2][y - 1] + energy[width - 1][y];
                        flagOfEnergy[width - 1][y] = -1;
                    }
                    for (int x = 1; x < width - 1; x++){
                        if (sumOfEnergy[x][y - 1] <= sumOfEnergy[x - 1][y - 1] && sumOfEnergy[x][y - 1] <= sumOfEnergy[x + 1][y - 1]){
                            sumOfEnergy[x][y] = sumOfEnergy[x][y - 1] + energy[x][y];
                            flagOfEnergy[x][y] = 0;
                        }
                        else if (sumOfEnergy[x - 1][y - 1] <= sumOfEnergy[x + 1][y - 1]){
                            sumOfEnergy[x][y] = sumOfEnergy[x - 1][y - 1] + energy[x][y];
                            flagOfEnergy[x][y] = -1;
                        }
                        else {
                            sumOfEnergy[x][y] = sumOfEnergy[x + 1][y - 1] + energy[x][y];
                            flagOfEnergy[x][y] = 1;
                        }
                    }
                }
                int minEnergy = 0x7fffffff;
                int pos = -1;
                for (int x = 0; x < width; x++){
                    if (sumOfEnergy[x][height - 1] < minEnergy){
                        minEnergy = sumOfEnergy[x][height - 1];
                        pos = x;
                    }
                }
                int[] seam = new int[height];
                seam[height - 1] = pos;
                for (int y = height - 1; y > 0; y--){
                    if (flagOfEnergy[pos][y] == 0)
                        seam[y - 1] = pos;
                    else if (flagOfEnergy[pos][y] == 1){
                        seam[y - 1] = (++pos);
                    }
                    else{
                        seam[y - 1] = (--pos);
                    }
                }
                return seam;
            }
            case 'h':{
                for (int y = 0; y < height; y++)
                    sumOfEnergy[0][y] = energy[0][y];
                for (int x = 1; x < width; x++){
                    if (sumOfEnergy[x - 1][0] <= sumOfEnergy[x - 1][1]){
                        sumOfEnergy[x][0] = sumOfEnergy[x - 1][0] + energy[x][0];
                        flagOfEnergy[x][0] = 0;
                    }
                    else{
                        sumOfEnergy[x][0] = sumOfEnergy[x - 1][1] + energy[x][0];
                        flagOfEnergy[x][0] = 1;
                    }

                    if (sumOfEnergy[x - 1][height - 1] <= sumOfEnergy[x -1][height - 2]){
                        sumOfEnergy[x][height - 1] = sumOfEnergy[x - 1][height - 1] + energy[x][height - 1];
                        flagOfEnergy[x][height - 1] = 0;
                    }
                    else{
                        sumOfEnergy[x][height - 1] = sumOfEnergy[x - 1][height - 2] + energy[x][height - 1];
                        flagOfEnergy[x][height - 1] = -1;
                    }
                    for (int y = 1; y < height - 1; y++){
                        if (sumOfEnergy[x - 1][y] <= sumOfEnergy[x - 1][y - 1] && sumOfEnergy[x - 1][y] <= sumOfEnergy[x - 1][y + 1]){
                            sumOfEnergy[x][y] = sumOfEnergy[x - 1][y] + energy[x][y];
                            flagOfEnergy[x][y] = 0;
                        }
                        else if (sumOfEnergy[x - 1][y - 1] <= sumOfEnergy[x - 1][y + 1]){
                            sumOfEnergy[x][y] = sumOfEnergy[x - 1][y - 1] + energy[x][y];
                            flagOfEnergy[x][y] = -1;
                        }
                        else {
                            sumOfEnergy[x][y] = sumOfEnergy[x - 1][y + 1] + energy[x][y];
                            flagOfEnergy[x][y] = 1;
                        }
                    }
                }

                int minEnergy = 0x7fffffff;
                int pos = -1;
                for (int y = 0; y < height; y++){
                    if (sumOfEnergy[width - 1][y] < minEnergy){
                        minEnergy = sumOfEnergy[width - 1][y];
                        pos = y;
                    }
                }
                int[] seam = new int[width];
                seam[width - 1] = pos;
                for (int x = width - 1; x > 0; x--){
                    if (flagOfEnergy[x][pos] == 0)
                        seam[x - 1] = pos;
                    else if (flagOfEnergy[x][pos] == 1){
                        seam[x - 1] = (++pos);
                    }
                    else{
                        seam[x - 1] = (--pos);
                    }
                }
                return seam;
            }
        }
        return null;
    }

    protected BufferedImage removeSeams(BufferedImage img, int newWidth, int newHeight, char dim){
        int oldWidth = img.getWidth();
        int oldHeight = img.getHeight();
        switch (dim){
            case 'w':{
                BufferedImage tempImg = new BufferedImage(oldWidth, oldHeight, BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < oldWidth; x++)
                    for (int y = 0; y < oldHeight; y++)
                        tempImg.setRGB(x, y, img.getRGB(x, y));
                while (tempImg.getWidth() != newWidth){
                    int tempWidth = tempImg.getWidth();
                    int tempHeight = tempImg.getHeight();
                    int[] seam = getSingleSeam(getEnergy(tempImg), 'w');
                    for (int y = 0; y < tempHeight; y++){
                        for (int x = 0; x < seam[y]; x++){
                            tempImg.setRGB(x, y, tempImg.getRGB(x, y));
                        }
                        for (int x = seam[y]; x < tempWidth - 1; x++){
                            tempImg.setRGB(x, y, tempImg.getRGB(x + 1, y));
                        }

                    }
                    tempImg = tempImg.getSubimage(0, 0, tempWidth - 1, tempHeight);

                }
                return tempImg;
            }
            case 'h':{
                BufferedImage tempImg = new BufferedImage(oldWidth, oldHeight, BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < oldWidth; x++)
                    for (int y = 0; y < oldHeight; y++)
                        tempImg.setRGB(x, y, img.getRGB(x, y));
                while (tempImg.getHeight() != newHeight){
                    int tempWidth = tempImg.getWidth();
                    int tempHeight = tempImg.getHeight();
                    int[] seam = getSingleSeam(getEnergy(tempImg), 'h');
                    for (int x = 0; x < tempWidth; x++){
                        for (int y = 0; y < seam[x]; y++){
                            tempImg.setRGB(x, y, tempImg.getRGB(x, y));
                        }
                        for (int y = seam[x]; y < tempHeight - 1; y++){
                            tempImg.setRGB(x, y, tempImg.getRGB(x, y + 1));
                        }
                    }
                    tempImg = tempImg.getSubimage(0, 0, tempWidth, tempHeight - 1);
                }
                return tempImg;
            }
        }
        return null;
    }
}
