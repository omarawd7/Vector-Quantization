//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;
//////////////////the block class :we creat our block array and setting it/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class Block {
    public int[][] BlockArray;
    public int code;

    Block() {
        int[][] BlockArray = new int[0][0];
        code = -1;
    }

    Block(int[][] ba) {
        BlockArray = ba;
        code = -1;
    }

}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class VectorQ {
    //////////////in image class we read Image or write image //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class ImageClass {
        //read Image
        public static int[][] readImage(String p) { //path
            BufferedImage image;
            try {
                image = ImageIO.read(new File(p));
                int Image_Hieght = image.getHeight();
                int Image_Width = image.getWidth();

                int[][] The_Image_pixels = new int[Image_Hieght][Image_Width];
                for (int x = 0; x < Image_Width; x++) {
                    for (int y = 0; y < Image_Hieght; y++) {

                        int pixel = image.getRGB(x, y);

                        int red = (pixel & 0x00ff0000) >> 16;
                        int grean = (pixel & 0x0000ff00) >> 8;
                        int blue = pixel & 0x000000ff;
                        int alpha = (pixel & 0xff000000) >> 24;
                        The_Image_pixels[y][x] = red;
                    }
                }

                return The_Image_pixels;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return null;
            }

        }
        //write image
        public static void writeImage(int[][] imagePixels, String outPath) {

            BufferedImage image = new BufferedImage(imagePixels.length, imagePixels[0].length, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < imagePixels.length; y++) {
                for (int x = 0; x < imagePixels[y].length; x++) {
                    int value = -1 << 24;
                    value = 0xff000000 | (imagePixels[y][x] << 16) | (imagePixels[y][x] << 8) | (imagePixels[y][x]);
                    image.setRGB(x, y, value);
                }
            }

            File ImageFile = new File(outPath);
            try {
                ImageIO.write(image, "jpg", ImageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
    //////////////here we Perform the vector : diving the image -- Getting Idx ofBlock -- choosing the Range of the vector  --getAverage of the vector//////////////////////////////////////////////////////////////////////////
    static Vector<Block> Blocks = new Vector<Block>();   //the vector of blocks
    static Vector<Block> codeBook = new Vector<Block>(); //the vector of code book
    //2*2,4*4
    public static void DivideImage(int[][] pixels, int vs) { //vs is an input
        int division = pixels.length/vs;
        int ji = 0,Jj = 0;
        while (ji != division*vs)
        {
            while (Jj != division*vs)
            {
                int[][] v = new int[vs][vs];

                for (int i = 0 + ji,x=0; i < vs + ji; i++,x++) {
                    for (int j = 0 + Jj,y=0; j < vs + Jj; j++,y++) {
                        v[x][y] = pixels[i][j];
                    }
                }
                Block b = new Block(v);
                Blocks.add(b);
                Jj += vs;
            }
            Jj = 0;
            ji +=vs;
        }

    }
    public static int GetIdxofBlock(Block block1,Vector<Vector<Block>> t) {
        int IdxofBlock = -1; //give every Block an Index
        for (int i = 0; i < t.size(); i++)
            for (int j = 1; j < t.get(i).size(); j++)
                if (t.get(i).get(j).BlockArray == block1.BlockArray)
                    IdxofBlock=i;
        return IdxofBlock;
    }
    ////choose the range of the vector block //////////////////////////////////////////////////////////////////////////////////
    public static int chooseRange(Block block1, Vector<Vector<Block>> t) { //compare
        int idxOfBlock = -1,mn =Integer.MAX_VALUE,diff=0;
        for(int k=0;k<t.size();k++) {
            for (int i = 0; i < block1.BlockArray.length; i++) {
                for (int j = 0; j < block1.BlockArray.length; j++) {
                    diff += Math.abs(block1.BlockArray[i][j]-(t.get(k).get(0).BlockArray[i][j]));
                }
            }
            if(diff<mn)
            {
                mn = diff;
                idxOfBlock = k;
                diff = 0;
            }
        }
        return idxOfBlock;
    }
    //////////calculating the average of a vector block ///////////////////////////////////////////////////////////////////////////////////////////////
    public static int[][] getAverage(Vector<Block> Blocks) {
        int vs = Blocks.elementAt(0).BlockArray.length;
        int[][] mid = new int[vs][vs];
        for (int i = 0; i < vs; i++)
            for (int j = 0; j < vs; j++)
                mid[i][j] = 0;
        for (int k = 0; k < Blocks.size(); k++)
            for (int i = 0; i < vs; i++)
                for (int j = 0; j < vs; j++)
                    mid[i][j] += Blocks.elementAt(k).BlockArray[i][j];
        for (int i = 0; i < vs; i++)
            for (int j = 0; j < vs; j++)
                mid[i][j] /= Blocks.size();
        return mid;
    }

    //////////Comprssing  the Image/////////
    public static void compress(int nov) throws IOException {
        int val = (int) Math.sqrt(Blocks.size());
        int[][] cBlock = new int[val][val];

//the vector of the tree
        Vector<Vector<Block>> The_Vector_Tree = new Vector<Vector<Block>>();
        int[][] average_of_blocks= getAverage(Blocks);// getting the average of blocks

        int vs = Blocks.elementAt(0).BlockArray.length;  //the vector size of original pic

        //Splitting it into two pointers////////////////////////////////////////
        int[][] avgerageleft = new int[vs][vs]; //the range of the average left
        int[][] averageRight = new int[vs][vs];//the range of the average right

        for (int i = 0; i < vs; i++) {
            for (int j = 0; j < vs; j++) {
                avgerageleft[i][j] = average_of_blocks[i][j] - 1;
                averageRight[i][j] = average_of_blocks[i][j] + 1;
            }
        }
        Block leftBlock1 = new Block(avgerageleft), RightBlock2 = new Block(averageRight);

        Vector<Block> vectorL = new Vector<Block>(), vectorR = new Vector<Block>(); //all blocks in left in vector l

        vectorL.add(leftBlock1);
        vectorR.add(RightBlock2);

        The_Vector_Tree.add(vectorL);
        The_Vector_Tree.add(vectorR);

        int split = (int) (Math.log10(nov) / Math.log10(2.));

        int flag = 0;

        for (int i = 0; i < split; i++) {

            for (int j = 0; j < Blocks.size(); j++) {
                Block bl = Blocks.elementAt(j);
                int ind = chooseRange(Blocks.elementAt(j),The_Vector_Tree);
                The_Vector_Tree.elementAt(ind).add(bl);
            }
            int The_Tree_Size = The_Vector_Tree.size();
///////////////vector Block of mid points ////////////////////////////////////
            Vector<Block> midpoints = new Vector<Block>();

            for (int j = 0; j < The_Tree_Size; j++) {

                average_of_blocks = new int[vs][vs];
                average_of_blocks=getAverage(The_Vector_Tree.get(j));

                Block b = new Block(average_of_blocks);
                midpoints.add(b);

                //update the average point in the tree////////////////////////////////////////////////////////////////////////////////
                The_Vector_Tree.get(j).get(0).BlockArray = average_of_blocks;
            }

            //splitting it into two points///////////////////////////////// //////////
            if (i != split - 1) {
                The_Vector_Tree = new Vector<Vector<Block>>();
                for (int r = 0; r < midpoints.size(); r++) {
                    avgerageleft = new int[vs][vs];
                    averageRight = new int[vs][vs];
                    for (int l = 0; l < vs; l++) {
                        for (int m = 0; m < vs; m++) {
                            avgerageleft[l][m] = midpoints.elementAt(r).BlockArray[l][m] - 1;
                            averageRight[l][m] = midpoints.elementAt(r).BlockArray[l][m] + 1;
                        }
                    }
                    leftBlock1 = new Block(avgerageleft);
                    RightBlock2 = new Block(averageRight);
                    vectorL = new Vector<Block>();
                    vectorR = new Vector<Block>();
                    vectorL.add(leftBlock1);
                    vectorR.add(RightBlock2);
                    The_Vector_Tree.add(vectorL);
                    The_Vector_Tree.add(vectorR);
                }
            } else if (flag == 0) {
                split += 1;
                flag = 1;
            }
        }

        // adding in Code book /////////////////////////////////////////////////////
        for (int i = 0; i < The_Vector_Tree.size(); i++) {
            Block cb = new Block();
            cb = The_Vector_Tree.get(i).get(0);
            cb.code = i;
            codeBook.add(cb);
        }
        //compressing 
        int idx = 0;
        for (int i = 0; i < cBlock.length; i++) {
            for (int j = 0; j < cBlock.length; j++) {
                cBlock[i][j] = GetIdxofBlock(Blocks.elementAt(idx), The_Vector_Tree);
                idx++;
            }
        }
///////writing the compressed image in the file///////////////////////////////////////////////////////////////////////////
        ObjectOutputStream obj=new ObjectOutputStream(new FileOutputStream("Compress.txt"));
        obj.writeObject(cBlock);
        FileWriter fileWriter=new FileWriter("Comp.txt");
        for (int i = 0; i <cBlock.length ; i++) {
            for (int j = 0; j < cBlock.length; j++) {
                fileWriter.write(cBlock[i][j]+" ");
            }
            fileWriter.write("\n");
        }
        fileWriter.close();
    }
    public static int GetCodeBook(int c) {
        int n = -1;
        for (int i = 0; i < codeBook.size(); i++)
            if (codeBook.elementAt(i).code == c)
                n = i;
        return n;
    }
    ////////the decompression code/////////////////////////////////////////////////////////////////////////
    public static void Decompress() throws IOException, ClassNotFoundException {
        ObjectInputStream in=new ObjectInputStream(new BufferedInputStream(new BufferedInputStream(new FileInputStream("Compress.txt"))));

        int [][]cBlock=(int[][])in.readObject();
        System.out.println(cBlock.length);

        int vs = codeBook.elementAt(0).BlockArray.length;

        int The_Image_Length = (int) Math.sqrt(Blocks.size() * vs * vs);

        int[][] decompressed_Block1 = new int[The_Image_Length][The_Image_Length];

        int dv = The_Image_Length / vs;
        int ji = 0, Jj = 0;
        int a = 0, b = 0;

        while (ji != dv * vs) {
            while (Jj != dv * vs) {
                int ind = GetCodeBook(cBlock[a][b]);
                int[][] cd = codeBook.elementAt(ind).BlockArray;
                for (int i = 0 + ji, x = 0; i < vs + ji; i++, x++) {
                    for (int j = 0 + Jj, y = 0; j < vs + Jj; j++, y++) {
                        decompressed_Block1[i][j] = cd[x][y];
                    }
                }
                Jj += vs;
                b++;
            }
            Jj = 0;
            b = 0;
            ji += vs;
            a++;
        }
///to write the Image (the compression )in the file////////////////////////////////////////////////////////
        VectorQ.ImageClass.writeImage(decompressed_Block1, "image_out.jpg");
        FileWriter fileWriter=new FileWriter("deComp.txt"); //the decompression file
        for (int i = 0; i <decompressed_Block1.length ; i++) {
            for (int j = 0; j < decompressed_Block1.length; j++) {
                fileWriter.write(decompressed_Block1[i][j] + " ");
            }
            fileWriter.write("\n");
        }
        fileWriter.close();
    }
}
