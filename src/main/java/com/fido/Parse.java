package com.fido;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.fido.entity.Slide;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Parse {
    private static Integer tagsID_AI = 0;
    private static Map<String, Integer> tagsID = new HashMap<>();

    private static Integer photosID_AI = 0;
    private static Map<String, BitSet> verticalPhotos = new HashMap<>();
    private static Map<String, Boolean> verticalParsedPairs = new HashMap<>();
    private static List<Slide> slides = new LinkedList<>();

    private static int filesCountPromised = 0;
    private static int filesCountFound = 0;

    private static Integer getTagID(String word) {
        Integer tagID = tagsID.get(word);
        if (tagID == null) {
            tagsID.put(word, tagsID_AI);
            tagsID_AI++;
            return tagsID_AI - 1;
        }
        return tagID;
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        System.out.println("Parsing input file " + Config.filename);
        try {
            FileInputStream fin = new FileInputStream(Config.filename);
            System.out.println("Combining vertical photos");
            System.out.println("Start to parse file");
            String word = "";
            int i;
            char charI;

            while((i = fin.read()) != -1){
                charI = (char) i;
                if (charI == '\n') {
                    filesCountPromised = Integer.valueOf(word);
                    break;
                } else if (charI != '\r') {
                    word += charI;
                }
            }
            word = "";

            boolean isHorizontal = false;

            int tagsCountPromised = 0;
            int tagsCountFound = 0;
            int state = 0;
            BitSet tags = new BitSet();

            while((i = fin.read()) != -1){
                charI = (char) i;
                if (charI == ' ') {
                    if (state == 2) {
                        Integer tagId = getTagID(word);
                        tags.set(tagId);
                        //System.out.println("tag #" + tagId + ": " + word);
                        tagsCountFound++;
                    } else if (state == 1) {
                        tagsCountPromised = Integer.parseInt(word);
                        state = 2;
                    } else {
                        isHorizontal = word.equals("H");
                        state = 1;
                    }
                    word = "";
                } else if (charI == '\n') {
                    Integer tagId = getTagID(word);
                    tags.set(tagId);
                    //System.out.println("tag #" + tagId + ": " + word);
                    tagsCountFound++;
                    if (tagsCountFound != tagsCountPromised) {
                        System.out.flush();
                        System.err.println("tags count mismatch, promised " + tagsCountPromised + " but finded " + tagsCountFound);
                        System.err.flush();
                        System.exit(-1);
                    } else {
                        if (isHorizontal) {
                            Slide newSlide = new Slide(photosID_AI.toString(), tags);
                            slides.add(newSlide);
                        } else {
                            verticalPhotos.put(photosID_AI.toString(), tags);
                        }
                        //System.out.println("tags count match, promised " + tagsCountPromised + " found " + tagsCountFound);
                    }

                    word = "";
                    tagsCountPromised = 0;
                    tagsCountFound = 0;
                    state = 0;
                    tags = new BitSet();

                    photosID_AI++;
                    filesCountFound++;
                } else if (charI != '\r') {
                    word += charI;
                }
            }
            long endTime   = System.nanoTime();
            long totalTime = (endTime - startTime) / 1000 / 1000 / 1000;
            if (filesCountPromised != filesCountFound) {
                System.out.flush();
                System.err.println("files count mismatch, promised " + filesCountPromised + " but found " + filesCountFound);
                System.err.flush();
                System.exit(-1);
            }
            System.out.println("Done in " + totalTime + "s");
        } catch(IOException exception){
            exception.printStackTrace();
        }
        System.out.println("Combining vertical photos");
        startTime = System.nanoTime();
        for(Map.Entry<String, BitSet> verticalPhoto1 : verticalPhotos.entrySet()) {
            for(Map.Entry<String, BitSet> verticalPhoto2 : verticalPhotos.entrySet()) {
                if (!verticalPhoto1.equals(verticalPhoto2)) {
                    Integer verticalPhoto1ID = Integer.valueOf(verticalPhoto1.getKey());
                    Integer verticalPhoto2ID = Integer.valueOf(verticalPhoto2.getKey());
                    String slidesKey = verticalPhoto1ID < verticalPhoto2ID ? verticalPhoto1.getKey() + " " + verticalPhoto2.getKey() : verticalPhoto2.getKey() + " " + verticalPhoto1.getKey();
                    if (!verticalParsedPairs.containsKey(slidesKey)) {
                        BitSet twoVerticalPhotoTags = (BitSet) verticalPhoto1.getValue().clone();
                        twoVerticalPhotoTags.or(verticalPhoto2.getValue());

                        Slide newSlide = new Slide(photosID_AI.toString(), twoVerticalPhotoTags);
                        slides.add(newSlide);
                        verticalParsedPairs.put(slidesKey, true);
                    }
                }
            }
            System.gc();
        }
        long endTime   = System.nanoTime();
        long totalTime = (endTime - startTime) / 1000 / 1000 / 1000;
        System.out.println("Done in " + totalTime + "s");
        try {
            System.out.println("Saving binary");
            startTime = System.nanoTime();
            Kryo kryo = new Kryo();
            kryo.addDefaultSerializer(BitSet.class, new BitSetSerializer());

            Output output = new Output(new FileOutputStream(Config.filename + ".bin"));
            kryo.writeObject(output, slides);
            output.close();
            endTime   = System.nanoTime();
            totalTime = (endTime - startTime)  / 1000 / 1000 / 1000;
            System.out.print("Saved to " + Config.filename + ".bin in " + totalTime + "s");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
